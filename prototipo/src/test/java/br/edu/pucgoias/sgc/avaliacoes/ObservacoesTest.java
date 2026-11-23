package br.edu.pucgoias.sgc.avaliacoes;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.Observacao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.ObservacaoRepository;
import br.edu.pucgoias.sgc.avaliacoes.servico.DadosObservacao;
import br.edu.pucgoias.sgc.avaliacoes.servico.FonteObservacoes;
import br.edu.pucgoias.sgc.avaliacoes.servico.ObservacaoResumo;
import br.edu.pucgoias.sgc.avaliacoes.servico.ObservacaoService;
import br.edu.pucgoias.sgc.identidade.servico.AutenticacaoService;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** UC-08 Registrar observações sobre a equipe (RF-17, RNF-03). */
class ObservacoesTest extends TesteIntegracao {

    private static final String BRUNO = "gestor@exemplo.com";
    private static final String CARLA = "colaborador@exemplo.com";
    private static final LocalDate HOJE = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

    @Autowired
    private ObservacaoService observacoes;
    @Autowired
    private ObservacaoRepository repositorio;
    @Autowired
    private FonteObservacoes fonte;
    @Autowired
    private UsuarioService usuarios;
    @Autowired
    private AutenticacaoService autenticacao;

    @Test
    void gestorRegistraEVeNaListaDoColaborador() throws Exception {
        Long carla = id(CARLA);
        int antes = doColaborador(carla);

        mvc.perform(post("/observacoes").with(como(BRUNO)).with(csrf())
                        .param("colaboradorId", carla.toString()).param("tipo", "POSITIVO")
                        .param("descricao", "  Atendeu o cliente da filial de Rio Verde com muita paciência. ")
                        .param("dataFato", HOJE.minusDays(2).toString()))
                .andExpect(redirectedUrl("/observacoes/" + carla));

        assertThat(doColaborador(carla)).isEqualTo(antes + 1);
        mvc.perform(get("/observacoes/" + carla).with(como(BRUNO)))
                .andExpect(status().isOk())
                .andExpect(view().name("avaliacoes/observacoes-colaborador"))
                .andExpect(content().string(containsString("Atendeu o cliente da filial de Rio Verde com muita paciência.")))
                .andExpect(content().string(containsString("Ponto positivo")));
        mvc.perform(get("/observacoes").with(como(BRUNO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("Fábio Nunes")))
                .andExpect(content().string(not(containsString("Igor Matos"))));
    }

    @Test
    void autorEditaEExcluiAPropriaObservacao() throws Exception {
        Long carla = id(CARLA);
        Long id = observacoes.registrar(eu(BRUNO), new DadosObservacao(carla, "MELHORIA", "Texto inicial.", HOJE));

        mvc.perform(post("/observacoes/" + carla + "/" + id + "/editar").with(como(BRUNO)).with(csrf())
                        .param("tipo", "POSITIVO").param("descricao", "Texto corrigido.")
                        .param("dataFato", HOJE.minusDays(1).toString()))
                .andExpect(redirectedUrl("/observacoes/" + carla));
        Observacao alterada = repositorio.findById(id).orElseThrow();
        assertThat(alterada.getDescricao()).isEqualTo("Texto corrigido.");
        assertThat(alterada.getTipo()).isEqualTo(TipoObservacao.POSITIVO);
        assertThat(alterada.getDataFato()).isEqualTo(HOJE.minusDays(1));

        mvc.perform(post("/observacoes/" + carla + "/" + id + "/excluir").with(como(BRUNO)).with(csrf()))
                .andExpect(redirectedUrl("/observacoes/" + carla));
        assertThat(repositorio.findById(id)).isEmpty();
    }

    @Test
    void dataDoFatoNoFuturoERecusada() throws Exception {
        Long carla = id(CARLA);
        int antes = doColaborador(carla);
        mvc.perform(post("/observacoes").with(como(BRUNO)).with(csrf())
                        .param("colaboradorId", carla.toString()).param("tipo", "MELHORIA")
                        .param("descricao", "Fato que ainda não aconteceu.")
                        .param("dataFato", HOJE.plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("avaliacoes/observacoes-colaborador"))
                .andExpect(content().string(containsString("A data do fato não pode ser depois de hoje.")))
                .andExpect(content().string(containsString("Fato que ainda não aconteceu.")));
        assertThat(doColaborador(carla)).isEqualTo(antes);
    }

    @Test
    void camposObrigatoriosSaoConferidos() throws Exception {
        mvc.perform(post("/observacoes").with(como(BRUNO)).with(csrf())
                        .param("colaboradorId", id(CARLA).toString()).param("descricao", "  ").param("dataFato", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Escolha se é um ponto positivo ou um ponto de melhoria.")))
                .andExpect(content().string(containsString("Descreva o fato observado.")))
                .andExpect(content().string(containsString("Informe a data em que o fato aconteceu.")));
    }

    @Test
    void gestorNaoVeNemRegistraParaQuemNaoEDaEquipe() throws Exception {
        Long igor = id("igor@exemplo.com");
        int antes = doColaborador(igor);
        mvc.perform(get("/observacoes/" + igor).with(como(BRUNO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("relatório de vendas"))));
        mvc.perform(post("/observacoes").with(como(BRUNO)).with(csrf())
                        .param("colaboradorId", igor.toString()).param("tipo", "POSITIVO")
                        .param("descricao", "Tentativa de fora da equipe.").param("dataFato", HOJE.toString()))
                .andExpect(status().isForbidden());
        assertThat(doColaborador(igor)).isEqualTo(antes);

        // Nem pela edição de uma observação de outra equipe.
        Long doGustavo = repositorio.findByColaboradorIdOrderByDataFatoDescCriadaEmDesc(igor).get(0).getId();
        mvc.perform(post("/observacoes/" + igor + "/" + doGustavo + "/excluir").with(como(BRUNO)).with(csrf()))
                .andExpect(status().isForbidden());
        assertThat(repositorio.findById(doGustavo)).isPresent();
    }

    @Test
    void soQuemRegistrouPodeAlterar() throws Exception {
        Long carla = id(CARLA);
        Observacao deOutro = repositorio.save(new Observacao(id("helena@exemplo.com"), carla, TipoObservacao.POSITIVO,
                "Registrada pela gestora anterior.", HOJE.minusDays(200), OffsetDateTime.now().minusDays(199)));

        mvc.perform(get("/observacoes/" + carla).with(como(BRUNO)))
                .andExpect(content().string(containsString("Registrada pela gestora anterior.")))
                .andExpect(content().string(containsString("Helena Prado")));
        mvc.perform(post("/observacoes/" + carla + "/" + deOutro.getId() + "/excluir").with(como(BRUNO)).with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/observacoes/" + carla + "/" + deOutro.getId() + "/editar").with(como(BRUNO)))
                .andExpect(status().isForbidden());
        assertThat(repositorio.findById(deOutro.getId())).isPresent();
    }

    @Test
    void colaboradorNaoAcessaAsObservacoes() throws Exception {
        mvc.perform(get("/observacoes").with(como(CARLA))).andExpect(status().isForbidden());
        mvc.perform(get("/observacoes/" + id(CARLA)).with(como(CARLA))).andExpect(status().isForbidden());
        mvc.perform(get("/observacoes").with(como("rh@exemplo.com"))).andExpect(status().isForbidden());
    }

    @Test
    void observacoesDoPeriodoFiltraPelaDataDoFato() {
        Long bruno = id(BRUNO);
        Long diego = id("diego@exemplo.com");
        UsuarioAutenticado eu = eu(BRUNO);
        observacoes.registrar(eu, new DadosObservacao(diego, "POSITIVO", "Antes do período.", LocalDate.of(2025, 1, 31)));
        observacoes.registrar(eu, new DadosObservacao(diego, "MELHORIA", "Início do período.", LocalDate.of(2025, 2, 1)));
        observacoes.registrar(eu, new DadosObservacao(diego, "POSITIVO", "Fim do período.", LocalDate.of(2025, 3, 31)));
        observacoes.registrar(eu, new DadosObservacao(diego, "POSITIVO", "Depois do período.", LocalDate.of(2025, 4, 1)));

        List<ObservacaoResumo> periodo = observacoes.observacoesDoPeriodo(bruno, diego,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 3, 31));

        assertThat(periodo).extracting(ObservacaoResumo::descricao).containsExactly("Fim do período.", "Início do período.");
        assertThat(periodo).allMatch(o -> o.colaboradorId().equals(diego));
        assertThat(periodo.get(1).tipo()).isEqualTo("MELHORIA");

        assertThatThrownBy(() -> observacoes.observacoesDoPeriodo(id("gustavo@exemplo.com"), diego,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void copiaDosDadosPessoaisTrazAsObservacoesSobreAPessoa() throws Exception {
        Long carla = id(CARLA);
        observacoes.registrar(eu(BRUNO), new DadosObservacao(carla, "MELHORIA", "Esqueceu de atualizar o chamado.",
                LocalDate.of(2026, 3, 10)));

        assertThat(fonte.titulo()).isEqualTo("Observações registradas pelo gestor");
        List<String> itens = fonte.itens(carla);
        assertThat(itens).hasSize(doColaborador(carla));
        assertThat(itens).contains("10/03/2026 · Ponto de melhoria · Esqueceu de atualizar o chamado.");
        assertThat(fonte.itens(id("helena@exemplo.com"))).isEmpty();

        mvc.perform(get("/privacidade/meus-dados/copia").with(como(CARLA)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Observações registradas pelo gestor")))
                .andExpect(content().string(containsString("Esqueceu de atualizar o chamado.")));
    }

    @Test
    void paginaInicialDoGestorMostraOCartaoDeObservacoes() throws Exception {
        // Dados de exemplo: a última observação do Fábio tem mais de 60 dias.
        mvc.perform(get("/").with(como(BRUNO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Observações da equipe")))
                .andExpect(content().string(containsString("sem observação há mais de 60 dias: Fábio")));
        mvc.perform(get("/").with(como(CARLA)))
                .andExpect(content().string(not(containsString("sem observação há mais de"))));
    }

    private int doColaborador(Long colaboradorId) {
        return repositorio.findByColaboradorIdOrderByDataFatoDescCriadaEmDesc(colaboradorId).size();
    }

    private Long id(String email) {
        return usuarios.buscarPorEmail(email).id();
    }

    private UsuarioAutenticado eu(String email) {
        return (UsuarioAutenticado) autenticacao.loadUserByUsername(email);
    }
}
