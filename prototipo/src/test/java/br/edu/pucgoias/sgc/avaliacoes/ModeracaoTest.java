package br.edu.pucgoias.sgc.avaliacoes;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RascunhoRespostaRepository;
import br.edu.pucgoias.sgc.avaliacoes.servico.ComentarioParaModeracao;
import br.edu.pucgoias.sgc.avaliacoes.servico.ComentarioResumo;
import br.edu.pucgoias.sgc.avaliacoes.servico.ConsultaRespostasService;
import br.edu.pucgoias.sgc.avaliacoes.servico.ModeracaoService;
import br.edu.pucgoias.sgc.avaliacoes.servico.RespostaConsolidavel;
import br.edu.pucgoias.sgc.avaliacoes.servico.UsoQuestionarioEmRespostas;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import br.edu.pucgoias.sgc.compartilhado.servico.AuditoriaService;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** UC-09 Reabrir avaliação e moderar comentários, e a API das respostas para o pacote resultados. */
class ModeracaoTest extends TesteIntegracao {

    private static final String RH = "rh@exemplo.com";
    private static final String GROSSEIRO = "Ele é arrogante e ninguém aguenta trabalhar com ele.";

    @Autowired
    private CicloService ciclos;
    @Autowired
    private QuestionarioService questionarios;
    @Autowired
    private UsuarioService usuarios;
    @Autowired
    private RascunhoRespostaRepository rascunhos;
    @Autowired
    private ConsultaRespostasService consulta;
    @Autowired
    private ModeracaoService moderacao;
    @Autowired
    private AuditoriaService auditoria;
    @Autowired
    private EmailService emails;
    @Autowired
    private UsoQuestionarioEmRespostas usoQuestionario;

    private ApoioAvaliacoes apoio;

    @BeforeEach
    void preparar() {
        apoio = new ApoioAvaliacoes(ciclos, questionarios, usuarios);
    }

    @Test
    void reaberturaDevolveAsRespostasAoRascunhoAuditaEAvisa() throws Exception {
        CicloResumo aberto = apoio.aberto();
        DesignacaoResumo d = ciclos.designacoesDoCiclo(aberto.id()).stream()
                .filter(x -> x.enviada() && !x.externa()).findFirst().orElseThrow();
        int antes = consulta.avaliacoesEnviadas(aberto.id(), d.avaliadoId(), d.relacao());
        assertThat(rascunhos.findByDesignacaoId(d.id())).isEmpty();

        mvc.perform(get("/moderacao").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/moderacao/reabrir/" + d.id())));

        // Sem motivo: não reabre.
        mvc.perform(post("/moderacao/reabrir/" + d.id()).param("motivo", " ").with(como(RH)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Informe o motivo da reabertura.")));
        assertThat(ciclos.designacao(d.id()).enviada()).isTrue();

        mvc.perform(post("/moderacao/reabrir/" + d.id()).param("motivo", "Marcou uma nota errada, pediu por e-mail.")
                        .with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/moderacao?ciclo=" + aberto.id()));

        assertThat(ciclos.designacao(d.id()).emPreenchimento()).isTrue();
        assertThat(rascunhos.findByDesignacaoId(d.id())).isNotEmpty();
        assertThat(consulta.avaliacoesEnviadas(aberto.id(), d.avaliadoId(), d.relacao())).isEqualTo(antes - 1);

        RegistroAuditoria registro = auditoria.listar(0).getContent().stream()
                .filter(r -> r.getAcao().equals("AVALIACAO_REABERTA")).findFirst().orElseThrow();
        assertThat(registro.getMotivo()).isEqualTo("Marcou uma nota errada, pediu por e-mail.");
        assertThat(registro.getUsuario()).isEqualTo(RH);
        String avaliador = usuarios.buscar(d.avaliadorId()).email();
        assertThat(emails.enviadosPara(avaliador)).anyMatch(e -> e.getAssunto().contains("reaberta"));

        // O avaliador encontra as respostas de volta e pode enviar de novo.
        mvc.perform(apoio.tudoRespondido(post("/avaliacoes/" + d.id() + "/enviar"), d).param("confirmado", "true")
                        .with(como(avaliador)).with(csrf()))
                .andExpect(redirectedUrl("/avaliacoes"));
        assertThat(consulta.avaliacoesEnviadas(aberto.id(), d.avaliadoId(), d.relacao())).isEqualTo(antes);
    }

    @Test
    void cicloEncerradoNaoReabre() throws Exception {
        DesignacaoResumo d = ciclos.designacoesDoCiclo(apoio.encerrado().id()).get(0);
        mvc.perform(post("/moderacao/reabrir/" + d.id()).param("motivo", "Pedido do avaliador.")
                        .with(como(RH)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("está encerrado")));
        assertThat(ciclos.designacao(d.id()).enviada()).isTrue();
    }

    @Test
    void moderacaoOcultaEOComentarioSomeDosResultados() throws Exception {
        CicloResumo encerrado = apoio.encerrado();
        Long bruno = apoio.id("gestor@exemplo.com");
        assertThat(consulta.comentariosVisiveis(encerrado.id(), bruno)).extracting(ComentarioResumo::texto)
                .contains(GROSSEIRO);

        mvc.perform(get("/moderacao/comentarios").param("ciclo", encerrado.id().toString()).with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(GROSSEIRO)))
                .andExpect(content().string(not(containsString("Subordinado"))));

        Long respostaId = moderacao.comentarios(encerrado.id()).stream()
                .filter(c -> c.texto().equals(GROSSEIRO)).map(ComentarioParaModeracao::respostaId)
                .findFirst().orElseThrow();

        // Sem motivo: continua visível.
        mvc.perform(post("/moderacao/comentarios/" + respostaId + "/ocultar").param("ciclo", encerrado.id().toString())
                        .with(como(RH)).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(consulta.comentariosVisiveis(encerrado.id(), bruno)).extracting(ComentarioResumo::texto)
                .contains(GROSSEIRO);

        mvc.perform(post("/moderacao/comentarios/" + respostaId + "/ocultar").param("ciclo", encerrado.id().toString())
                        .param("motivo", "Ofensivo e sem relação com o trabalho.").with(como(RH)).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(consulta.comentariosVisiveis(encerrado.id(), bruno)).extracting(ComentarioResumo::texto)
                .doesNotContain(GROSSEIRO);
        assertThat(auditoria.listar(0).getContent()).anyMatch(r -> r.getAcao().equals("COMENTARIO_OCULTADO")
                && "Ofensivo e sem relação com o trabalho.".equals(r.getMotivo()));

        mvc.perform(post("/moderacao/comentarios/" + respostaId + "/exibir").param("ciclo", encerrado.id().toString())
                        .with(como(RH)).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(consulta.comentariosVisiveis(encerrado.id(), bruno)).extracting(ComentarioResumo::texto)
                .contains(GROSSEIRO);
        assertThat(auditoria.listar(0).getContent()).anyMatch(r -> r.getAcao().equals("COMENTARIO_EXIBIDO"));
    }

    @Test
    void soORhAcessaAModeracao() throws Exception {
        mvc.perform(get("/moderacao").with(como("colaborador@exemplo.com"))).andExpect(status().isForbidden());
        mvc.perform(get("/moderacao/comentarios").with(como("gestor@exemplo.com"))).andExpect(status().isForbidden());
    }

    @Test
    void apiParaResultadosTrazRespostasSemAutor() {
        CicloResumo encerrado = apoio.encerrado();
        Long carla = apoio.id("colaborador@exemplo.com");
        assertThat(consulta.avaliacoesEnviadas(encerrado.id(), carla, TipoAvaliador.CHEFE)).isEqualTo(1);
        assertThat(consulta.avaliacoesEnviadas(encerrado.id(), carla, TipoAvaliador.AUTO)).isEqualTo(1);
        assertThat(consulta.avaliacoesEnviadas(encerrado.id(), carla, TipoAvaliador.PAR)).isEqualTo(3);

        List<RespostaConsolidavel> lista = consulta.respostasDoCiclo(encerrado.id());
        assertThat(lista).isNotEmpty()
                .allMatch(r -> r.competenciaId() != null && r.avaliacaoId() != null)
                .allMatch(r -> r.naoAvalia() == (r.nota() == null))
                .anyMatch(RespostaConsolidavel::naoAvalia);
        assertThat(lista).filteredOn(r -> r.avaliadoId().equals(carla)).extracting(RespostaConsolidavel::relacao)
                .contains(TipoAvaliador.CHEFE, TipoAvaliador.AUTO, TipoAvaliador.PAR);

        DesignacaoResumo d = apoio.designacao(encerrado, "gestor@exemplo.com", "colaborador@exemplo.com",
                TipoAvaliador.CHEFE);
        assertThat(usoQuestionario.possuiRespostas(d.questionarioId())).isTrue();
    }
}
