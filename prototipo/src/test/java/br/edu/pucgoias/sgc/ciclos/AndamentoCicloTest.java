package br.edu.pucgoias.sgc.ciclos;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.privacidade.servico.ParticipacaoEmCiclo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-07 Acompanhar andamento do ciclo (RF-12, RF-13), a carga de exemplo dos ciclos e a interface pública do pacote
 * usada por avaliacoes, resultados, competencias e privacidade.
 */
class AndamentoCicloTest extends TesteIntegracao {

    @Autowired
    private CicloService ciclos;
    @Autowired
    private UsuarioService usuarios;
    @Autowired
    private EmailService emails;
    @Autowired
    private QuestionarioService questionarios;
    @Autowired
    private ParticipacaoEmCiclo participacao;

    @Test
    void rhVeAOrganizacaoInteira() throws Exception {
        CicloResumo aberto = ciclos.abertos().get(0);
        mvc.perform(get("/ciclos/andamento").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(aberto.nome())))
                .andExpect(content().string(containsString("Igor Matos")))
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("Marcos Tavares")))
                .andExpect(content().string(containsString("Enviar lembrete aos pendentes")));
        // Encerrado fica disponível para consulta.
        CicloResumo encerrado = ciclos.encerrados().get(0);
        mvc.perform(get("/ciclos/andamento").param("ciclo", encerrado.id().toString()).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("100%")))
                .andExpect(content().string(not(containsString("Enviar lembrete aos pendentes"))));
    }

    @Test
    void gestorVeSoAEquipeDiretaENaoVeOutraEquipePelaUrl() throws Exception {
        mvc.perform(get("/ciclos/andamento").with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("sua equipe direta")))
                .andExpect(content().string(not(containsString("Igor Matos"))))
                .andExpect(content().string(not(containsString("Júlia Campos"))));

        PessoaResumo carla = pessoa("colaborador");
        mvc.perform(get("/ciclos/andamento").param("pessoa", carla.id().toString()).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Filtrado por Carla Dias")));

        PessoaResumo igor = pessoa("igor");
        mvc.perform(get("/ciclos/andamento").param("pessoa", igor.id().toString()).with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Igor Matos"))));
    }

    @Test
    void colaboradorNaoAcessaOsCiclosNemOAndamento() throws Exception {
        for (String url : List.of("/ciclos", "/ciclos/andamento", "/ciclos/novo")) {
            mvc.perform(get(url).with(como("colaborador@exemplo.com"))).andExpect(status().isForbidden());
        }
        Long aberto = ciclos.abertos().get(0).id();
        mvc.perform(post("/ciclos/andamento/" + aberto + "/lembrete").with(como("colaborador@exemplo.com")).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void lembreteManualVaiAosPendentesUmaVezPorDia() throws Exception {
        Long aberto = ciclos.abertos().get(0).id();
        int antesCarla = emails.enviadosPara("colaborador@exemplo.com").size();
        int antesIgor = emails.enviadosPara("igor@exemplo.com").size();

        mvc.perform(post("/ciclos/andamento/" + aberto + "/lembrete").with(como("gestor@exemplo.com")).with(csrf()))
                .andExpect(redirectedUrl("/ciclos/andamento?ciclo=" + aberto))
                .andExpect(flash().attribute("sucesso", containsString("Lembrete enviado")));
        assertThat(emails.enviadosPara("colaborador@exemplo.com")).hasSize(antesCarla + 1)
                .anySatisfy(e -> assertThat(e.getAssunto()).startsWith("Lembrete"));
        // O gestor só lembra a própria equipe.
        assertThat(emails.enviadosPara("igor@exemplo.com")).hasSize(antesIgor);

        // No mesmo dia, não repete.
        mvc.perform(post("/ciclos/andamento/" + aberto + "/lembrete").with(como("gestor@exemplo.com")).with(csrf()))
                .andExpect(flash().attribute("aviso", containsString("já receberam lembrete hoje")));
        assertThat(emails.enviadosPara("colaborador@exemplo.com")).hasSize(antesCarla + 1);
        mvc.perform(get("/auditoria").with(como("rh@exemplo.com")))
                .andExpect(content().string(containsString("Enviou lembrete")));
    }

    @Test
    void cargaDeExemploMontaOsTresCiclos() {
        assertThat(ciclos.encerrados()).extracting(CicloResumo::nome).contains("Ciclo 2026.1");
        assertThat(ciclos.abertos()).extracting(CicloResumo::nome).contains("Ciclo 2026.2");
        CicloResumo aberto = ciclos.abertos().stream().filter(c -> c.nome().equals("Ciclo 2026.2")).findFirst().orElseThrow();
        CicloResumo encerrado = ciclos.encerrados().stream().filter(c -> c.nome().equals("Ciclo 2026.1")).findFirst().orElseThrow();

        assertThat(ciclos.designacoesDoCiclo(encerrado.id())).isNotEmpty().allMatch(DesignacaoResumo::enviada);
        List<DesignacaoResumo> doAberto = ciclos.designacoesDoCiclo(aberto.id());
        assertThat(doAberto).anyMatch(DesignacaoResumo::enviada).anyMatch(DesignacaoResumo::emPreenchimento);
        assertThat(doAberto.stream().filter(DesignacaoResumo::naoIniciada).count()).isGreaterThan(doAberto.size() / 2);
        List<Long> demonstracao = List.of(pessoa("colaborador").id(), pessoa("gestor").id(), pessoa("diego").id());
        assertThat(doAberto).filteredOn(d -> d.avaliadorId() != null && demonstracao.contains(d.avaliadorId())).isNotEmpty()
                .allMatch(DesignacaoResumo::naoIniciada);
        DesignacaoResumo externa = doAberto.stream().filter(DesignacaoResumo::externa).findFirst().orElseThrow();
        assertThat(externa.externoNome()).isEqualTo("Marcos Tavares");
        assertThat(externa.avaliadoId()).isEqualTo(pessoa("colaborador").id());
        assertThat(externa.naoIniciada()).isTrue();
        assertThat(emails.enviadosPara("marcos.cliente@exemplo.com")).isNotEmpty();
        assertThat(ciclos.avaliadosDoCiclo(aberto.id())).hasSize(8);

        // Kátia (contratação recente) fica fora dos dois ciclos; Ana (RH) não avalia ninguém como par.
        Long katia = pessoa("katia").id();
        Long ana = pessoa("rh").id();
        for (CicloResumo c : List.of(encerrado, aberto)) {
            assertThat(ciclos.designacoesDoCiclo(c.id()))
                    .noneMatch(d -> katia.equals(d.avaliadoId()) || katia.equals(d.avaliadorId()))
                    .noneMatch(d -> ana.equals(d.avaliadorId()) && d.relacao() == TipoAvaliador.PAR);
        }
        // Convites com a data da abertura do ciclo, não a da carga.
        assertThat(emails.enviadosPara("marcos.cliente@exemplo.com"))
                .allMatch(e -> e.getCriadoEm().isBefore(OffsetDateTime.now().minusDays(5)));

        // Questionário usado aparece como "em uso" no UC-01 (ConsultaUsoQuestionario).
        Long q = ciclos.questionarioDoAvaliado(aberto.id(), pessoa("colaborador").id());
        assertThat(questionarios.resumo(q).nome()).isEqualTo("Questionário — Analista de Suporte");
        assertThat(questionarios.uso(q).ciclos()).contains("Ciclo 2026.1", "Ciclo 2026.2");
    }

    @Test
    void interfacePublicaParaAvaliacoesEResultados() {
        PessoaResumo carla = pessoa("colaborador");
        CicloResumo aberto = ciclos.abertos().stream().filter(c -> c.nome().equals("Ciclo 2026.2")).findFirst().orElseThrow();
        CicloResumo encerrado = ciclos.encerrados().get(0);
        assertThat(aberto.tiposAtivos()).contains(TipoAvaliador.CHEFE, TipoAvaliador.AUTO, TipoAvaliador.PAR,
                TipoAvaliador.SUBORDINADO, TipoAvaliador.EXTERNO);
        assertThat(ciclos.aceitaRespostas(aberto.id())).isTrue();
        assertThat(ciclos.aceitaRespostas(encerrado.id())).isFalse();

        List<DesignacaoResumo> minhas = ciclos.designacoesDoAvaliador(carla.id());
        assertThat(minhas).extracting(DesignacaoResumo::cicloNome).contains("Ciclo 2026.1", "Ciclo 2026.2")
                .doesNotContain("Ciclo 2027.1");
        assertThat(minhas).allMatch(d -> d.questionarioId() != null);
        DesignacaoResumo auto = minhas.stream().filter(d -> d.cicloAberto() && d.relacao() == TipoAvaliador.AUTO)
                .findFirst().orElseThrow();

        ciclos.marcarEmPreenchimento(auto.id());
        ciclos.marcarEmPreenchimento(auto.id());
        assertThat(ciclos.designacao(auto.id()).situacao()).isEqualTo("EM_PREENCHIMENTO");
        ciclos.marcarEnviada(auto.id());
        assertThat(ciclos.designacao(auto.id()).enviada()).isTrue();
        assertThatThrownBy(() -> ciclos.marcarEnviada(auto.id())).isInstanceOf(RegraDeNegocioException.class);
        ciclos.reabrir(auto.id());
        assertThat(ciclos.designacao(auto.id()).situacao()).isEqualTo("EM_PREENCHIMENTO");
        assertThatThrownBy(() -> ciclos.reabrir(auto.id())).isInstanceOf(RegraDeNegocioException.class);

        // Liberação do resultado (RF-24): só encerrado, uma vez. A carga de exemplo do resultados já libera o 2026.1.
        assertThat(ciclos.ciclo(encerrado.id()).resultadoLiberado()).isTrue();
        assertThatThrownBy(() -> ciclos.liberarResultado(encerrado.id())).isInstanceOf(RegraDeNegocioException.class);
        assertThatThrownBy(() -> ciclos.liberarResultado(aberto.id())).isInstanceOf(RegraDeNegocioException.class);

        // Participação em ciclo aberto (UC-05, 7c).
        assertThat(participacao.participaDeCicloAberto(carla.id())).isTrue();
        assertThat(participacao.participaDeCicloAberto(pessoa("rh").id())).isFalse();
        assertThat(participacao.participaDeCicloAberto(pessoa("katia").id())).isFalse();
    }

    @Test
    void exclusaoDeDadosDeQuemEstaNoCicloAbertoPedeConfirmacao() throws Exception {
        // UC-05, 7c, com a ParticipacaoEmCiclo implementada por este pacote.
        mvc.perform(post("/privacidade/meus-dados/solicitacoes").with(como("elisa@exemplo.com")).with(csrf())
                        .param("tipo", "EXCLUSAO").param("descricao", "Quero sair."))
                .andExpect(status().isOk());
    }

    @Test
    void paginaInicialMostraOsCartoesDosCiclos() throws Exception {
        mvc.perform(get("/").with(como("rh@exemplo.com")))
                .andExpect(content().string(containsString("Ciclo 2026.2")));
        mvc.perform(get("/").with(como("gestor@exemplo.com")))
                .andExpect(content().string(containsString("Pendentes da equipe")));
    }

    private PessoaResumo pessoa(String usuario) {
        return usuarios.buscarPorEmail(usuario + "@exemplo.com");
    }
}
