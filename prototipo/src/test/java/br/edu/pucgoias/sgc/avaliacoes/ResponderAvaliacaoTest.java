package br.edu.pucgoias.sgc.avaliacoes;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.AvaliacaoEnviada;
import br.edu.pucgoias.sgc.avaliacoes.dominio.RascunhoResposta;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.AvaliacaoEnviadaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RascunhoRespostaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RespostaRepository;
import br.edu.pucgoias.sgc.avaliacoes.servico.ConsultaRespostasService;
import br.edu.pucgoias.sgc.avaliacoes.servico.FormularioAvaliacao;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** UC-03 Responder avaliação: salvamento automático, envio anônimo, bloqueios, acesso e externo (RF-34). */
class ResponderAvaliacaoTest extends TesteIntegracao {

    private static final String CARLA = "colaborador@exemplo.com";
    private static final String BRUNO = "gestor@exemplo.com";

    @Autowired
    private CicloService ciclos;
    @Autowired
    private QuestionarioService questionarios;
    @Autowired
    private UsuarioService usuarios;
    @Autowired
    private RascunhoRespostaRepository rascunhos;
    @Autowired
    private AvaliacaoEnviadaRepository avaliacoes;
    @Autowired
    private RespostaRepository respostas;
    @Autowired
    private ConsultaRespostasService consulta;
    @Autowired
    private JdbcTemplate jdbc;

    private ApoioAvaliacoes apoio;
    private CicloResumo aberto;
    /** Autoavaliação da Carla no ciclo aberto: não iniciada nos dados de exemplo. */
    private DesignacaoResumo autoDaCarla;

    @BeforeEach
    void preparar() {
        apoio = new ApoioAvaliacoes(ciclos, questionarios, usuarios);
        aberto = apoio.aberto();
        autoDaCarla = apoio.designacao(aberto, CARLA, CARLA, TipoAvaliador.AUTO);
    }

    @Test
    void listaMostraAsAvaliacoesDoUsuarioPorCiclo() throws Exception {
        mvc.perform(get("/avaliacoes").with(como(CARLA)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ciclo 2026.2")))
                .andExpect(content().string(containsString("Ciclo 2026.1")))
                .andExpect(content().string(containsString("Autoavaliação")))
                .andExpect(content().string(containsString("/avaliacoes/" + autoDaCarla.id())));
        mvc.perform(get("/").with(como(CARLA)))
                .andExpect(content().string(containsString("Avaliações pendentes")));
    }

    @Test
    void salvamentoAutomaticoGravaORascunhoEMarcaEmPreenchimento() throws Exception {
        assertThat(autoDaCarla.naoIniciada()).isTrue();
        PerguntaResumo p = apoio.primeiraDeEscala(autoDaCarla);

        mvc.perform(post("/avaliacoes/" + autoDaCarla.id() + "/resposta").with(como(CARLA)).with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("salvoEm")));

        List<RascunhoResposta> salvas = rascunhos.findByDesignacaoId(autoDaCarla.id());
        assertThat(salvas).singleElement().satisfies(r -> {
            assertThat(r.getPerguntaId()).isEqualTo(p.id());
            assertThat(r.getNota()).isEqualTo(4);
        });
        assertThat(ciclos.designacao(autoDaCarla.id()).emPreenchimento()).isTrue();

        // *a: ao voltar, a avaliação reabre com a resposta salva.
        FormularioAvaliacao f = formulario(CARLA, autoDaCarla.id());
        assertThat(f.marcada(p.id(), 4)).isTrue();

        // "Não tenho como avaliar" (RF-06) troca a nota.
        mvc.perform(post("/avaliacoes/" + autoDaCarla.id() + "/resposta").with(como(CARLA)).with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "NA"))
                .andExpect(status().isOk());
        assertThat(rascunhos.findByDesignacaoIdAndPerguntaId(autoDaCarla.id(), p.id()).orElseThrow())
                .satisfies(r -> {
                    assertThat(r.isNaoAvalia()).isTrue();
                    assertThat(r.getNota()).isNull();
                });

        // Nota fora da escala é recusada.
        mvc.perform(post("/avaliacoes/" + autoDaCarla.id() + "/resposta").with(como(CARLA)).with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "9"))
                .andExpect(status().isConflict());
    }

    @Test
    void obrigatoriaPendenteBarraOEnvio() throws Exception {
        PerguntaResumo p = apoio.primeiraDeEscala(autoDaCarla);
        int antes = consulta.avaliacoesEnviadas(aberto.id(), apoio.id(CARLA), TipoAvaliador.AUTO);

        MvcResult r = mvc.perform(post("/avaliacoes/" + autoDaCarla.id() + "/enviar").with(como(CARLA)).with(csrf())
                        .param("nota_" + p.id(), "5").param("confirmado", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("avaliacoes/responder"))
                .andExpect(content().string(containsString("perguntas obrigatórias")))
                .andReturn();

        FormularioAvaliacao f = (FormularioAvaliacao) r.getModelAndView().getModel().get("f");
        assertThat(f.pendentes()).isNotEmpty().noneMatch(x -> x.id().equals(p.id()));
        assertThat(consulta.avaliacoesEnviadas(aberto.id(), apoio.id(CARLA), TipoAvaliador.AUTO)).isEqualTo(antes);
        assertThat(ciclos.designacao(autoDaCarla.id()).enviada()).isFalse();
    }

    @Test
    void envioGravaRespostasSemDesignacaoNemAvaliadorEApagaORascunho() throws Exception {
        Long carla = apoio.id(CARLA);
        int antes = consulta.avaliacoesEnviadas(aberto.id(), carla, TipoAvaliador.AUTO);
        Set<java.util.UUID> idsAntes = avaliacoes.findByCicloIdAndAvaliadoId(aberto.id(), carla).stream()
                .map(AvaliacaoEnviada::getId).collect(Collectors.toSet());

        // Sem confirmação: pede para confirmar (passo 8).
        mvc.perform(apoio.tudoRespondido(post("/avaliacoes/" + autoDaCarla.id() + "/enviar"), autoDaCarla)
                        .with(como(CARLA)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enviar a avaliação?")));
        assertThat(rascunhos.findByDesignacaoId(autoDaCarla.id())).isNotEmpty();

        mvc.perform(apoio.tudoRespondido(post("/avaliacoes/" + autoDaCarla.id() + "/enviar"), autoDaCarla)
                        .param("confirmado", "true").with(como(CARLA)).with(csrf()))
                .andExpect(redirectedUrl("/avaliacoes"));

        assertThat(ciclos.designacao(autoDaCarla.id()).enviada()).isTrue();
        assertThat(rascunhos.findByDesignacaoId(autoDaCarla.id())).isEmpty();
        assertThat(consulta.avaliacoesEnviadas(aberto.id(), carla, TipoAvaliador.AUTO)).isEqualTo(antes + 1);

        AvaliacaoEnviada nova = avaliacoes.findByCicloIdAndAvaliadoId(aberto.id(), carla).stream()
                .filter(a -> !idsAntes.contains(a.getId())).findFirst().orElseThrow();
        assertThat(nova.getRelacao()).isEqualTo(TipoAvaliador.AUTO);
        assertThat(nova.getVerificador()).matches("[0-9a-f]{64}")
                .isNotEqualTo(autoDaCarla.id().toString());
        int perguntas = apoio.questionario(autoDaCarla).totalPerguntas();
        assertThat(respostas.findByAvaliacaoEnviadaId(nova.getId())).hasSize(perguntas)
                .anyMatch(x -> "Comentário de teste sobre o período.".equals(x.getTexto()));

        // 10a: enviada não se edita.
        PerguntaResumo p = apoio.primeiraDeEscala(autoDaCarla);
        mvc.perform(post("/avaliacoes/" + autoDaCarla.id() + "/resposta").with(como(CARLA)).with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "2"))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("já foi enviada")));
        mvc.perform(get("/avaliacoes/" + autoDaCarla.id()).with(como(CARLA)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("já foi enviada")))
                .andExpect(content().string(not(containsString("id=\"formAvaliacao\""))));
    }

    @Test
    void tabelaDaAvaliacaoEnviadaNaoTemComoChegarAoAvaliador() {
        List<String> colunas = jdbc.queryForList("select column_name from information_schema.columns "
                + "where table_name = 'avaliacao_enviada' order by column_name", String.class);
        assertThat(colunas).containsExactlyInAnyOrder("id", "ciclo_id", "avaliado_id", "relacao",
                "questionario_id", "verificador");
        assertThat(jdbc.queryForObject("select data_type from information_schema.columns "
                + "where table_name = 'avaliacao_enviada' and column_name = 'id'", String.class)).isEqualTo("uuid");

        List<String> colunasResposta = jdbc.queryForList("select column_name from information_schema.columns "
                + "where table_name = 'resposta'", String.class);
        assertThat(colunasResposta).noneMatch(c -> c.contains("designacao") || c.contains("avaliador")
                || c.contains("usuario") || c.contains("_em"));

        List<Map<String, Object>> chaves = jdbc.queryForList("""
                select tc.table_name as tabela, kcu.column_name as coluna, ccu.table_name as referencia
                from information_schema.table_constraints tc
                join information_schema.key_column_usage kcu on kcu.constraint_name = tc.constraint_name
                join information_schema.constraint_column_usage ccu on ccu.constraint_name = tc.constraint_name
                where tc.constraint_type = 'FOREIGN KEY' and tc.table_name in ('avaliacao_enviada', 'resposta')""");
        assertThat(chaves).isNotEmpty().noneMatch(c -> "designacao".equals(c.get("referencia")));
        assertThat(chaves).filteredOn(c -> "usuario".equals(c.get("referencia")))
                .extracting(c -> c.get("coluna")).containsOnly("avaliado_id");
    }

    @Test
    void cicloEncerradoBloqueiaSalvarEEnviar() throws Exception {
        DesignacaoResumo antiga = apoio.designacao(apoio.encerrado(), CARLA, CARLA, TipoAvaliador.AUTO);
        PerguntaResumo p = apoio.primeiraDeEscala(antiga);
        mvc.perform(post("/avaliacoes/" + antiga.id() + "/resposta").with(como(CARLA)).with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "3"))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("encerrado")));
        mvc.perform(get("/avaliacoes/" + antiga.id()).with(como(CARLA)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("não aceita mais respostas")));
    }

    @Test
    void avaliadorNaoAbreADesignacaoDeOutraPessoa() throws Exception {
        PerguntaResumo p = apoio.primeiraDeEscala(autoDaCarla);
        mvc.perform(get("/avaliacoes/" + autoDaCarla.id()).with(como(BRUNO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString(p.texto()))));
        mvc.perform(post("/avaliacoes/" + autoDaCarla.id() + "/resposta").with(como(BRUNO)).with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "1"))
                .andExpect(status().isForbidden());
        assertThat(rascunhos.findByDesignacaoId(autoDaCarla.id())).isEmpty();
    }

    @Test
    void perguntasSaoFiltradasPelaRelacaoEOGestorVeObservacoes() throws Exception {
        String soDoChefe = "Mantém-se atualizado sobre as novidades e mudanças do produto.";
        DesignacaoResumo chefe = apoio.designacao(aberto, BRUNO, CARLA, TipoAvaliador.CHEFE);

        mvc.perform(get("/avaliacoes/" + autoDaCarla.id()).with(como(CARLA)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Conhece as funcionalidades do produto")))
                .andExpect(content().string(not(containsString(soDoChefe))))
                .andExpect(content().string(not(containsString("Suas observações do período"))))
                .andExpect(content().string(not(containsString("Resolveu sozinha a queda"))));

        mvc.perform(get("/avaliacoes/" + chefe.id()).with(como(BRUNO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(soDoChefe)))
                .andExpect(content().string(containsString("Suas observações do período")))
                .andExpect(content().string(containsString("Resolveu sozinha a queda")))
                .andExpect(content().string(containsString("Sem resultado anterior")));
    }

    @Test
    void externoRespondePeloLinkSemLogin() throws Exception {
        String token = jdbc.queryForObject("select token from designacao where ciclo_id = ? "
                + "and avaliador_externo_id is not null", String.class, aberto.id());
        DesignacaoResumo d = ciclos.designacaoPorToken(token).orElseThrow();
        String soDoChefe = "Mantém-se atualizado sobre as novidades e mudanças do produto.";

        mvc.perform(get("/externo/" + token))
                .andExpect(status().isOk())
                .andExpect(view().name("avaliacoes/externo"))
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("Resposta anônima")))
                .andExpect(content().string(not(containsString(soDoChefe))));

        mvc.perform(get("/externo/link-que-nao-existe"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Link inválido")))
                .andExpect(content().string(not(containsString("formAvaliacao"))));

        PerguntaResumo p = apoio.primeiraDeEscala(d);
        mvc.perform(post("/externo/" + token + "/resposta").with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "5"))
                .andExpect(status().isOk());
        assertThat(ciclos.designacao(d.id()).emPreenchimento()).isTrue();

        mvc.perform(apoio.tudoRespondido(post("/externo/" + token + "/enviar"), d).param("confirmado", "true")
                        .with(csrf()))
                .andExpect(redirectedUrl("/externo/" + token));
        assertThat(ciclos.designacao(d.id()).enviada()).isTrue();
        assertThat(consulta.avaliacoesEnviadas(aberto.id(), d.avaliadoId(), TipoAvaliador.EXTERNO)).isEqualTo(1);

        mvc.perform(get("/externo/" + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("já foi enviada")))
                .andExpect(content().string(not(containsString("formAvaliacao"))));
        mvc.perform(post("/externo/" + token + "/resposta").with(csrf())
                        .param("perguntaId", p.id().toString()).param("nota", "1"))
                .andExpect(status().isConflict());
    }

    private FormularioAvaliacao formulario(String email, Long designacaoId) throws Exception {
        MvcResult r = mvc.perform(get("/avaliacoes/" + designacaoId).with(como(email)))
                .andExpect(status().isOk()).andReturn();
        return (FormularioAvaliacao) r.getModelAndView().getModel().get("f");
    }
}
