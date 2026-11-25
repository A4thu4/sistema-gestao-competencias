package br.edu.pucgoias.sgc.resultados;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.avaliacoes.servico.ComentarioResumo;
import br.edu.pucgoias.sgc.avaliacoes.servico.ConsultaRespostasService;
import br.edu.pucgoias.sgc.avaliacoes.servico.RespostaConsolidavel;
import br.edu.pucgoias.sgc.avaliacoes.servico.ResultadoAnterior;
import br.edu.pucgoias.sgc.avaliacoes.servico.ResumoResultadoAnterior;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.servico.AuditoriaService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.resultados.dominio.ResultadoConsolidado;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaDesenvolvimentoRepository;
import br.edu.pucgoias.sgc.resultados.repositorio.ResultadoConsolidadoRepository;
import br.edu.pucgoias.sgc.resultados.servico.ConsolidacaoService;
import br.edu.pucgoias.sgc.resultados.servico.RegrasDeExibicao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Consolidação a partir das respostas reais (RF-19, RNF-12): carga de exemplo, reprodutibilidade, evento de
 * encerramento, botão "Recalcular", comentários (RF-22) e o resultado anterior para o UC-03 (RF-18).
 */
class ConsolidacaoTest extends TesteIntegracao {

    @Autowired
    private ConsolidacaoService consolidacao;
    @Autowired
    private ResultadoConsolidadoRepository consolidado;
    @Autowired
    private ConsultaRespostasService respostas;
    @Autowired
    private CicloService ciclos;
    @Autowired
    private UsuarioService usuarios;
    @Autowired
    private AuditoriaService auditoria;
    @Autowired
    private ResultadoAnterior resultadoAnterior;
    @Autowired
    private MetaDesenvolvimentoRepository metas;

    private CicloResumo ciclo20261() {
        return ciclos.encerrados().stream().filter(c -> c.nome().equals("Ciclo 2026.1")).findFirst().orElseThrow();
    }

    @Test
    void cargaDeExemploConsolidaELiberaO20261ECriaOsPlanos() {
        CicloResumo c = ciclo20261();
        assertThat(c.resultadoLiberado()).isTrue();
        assertThat(consolidado.existsByCicloId(c.id())).isTrue();
        assertThat(consolidado.existsByCicloId(ciclos.abertos().get(0).id())).isFalse();
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        PessoaResumo diego = usuarios.buscarPorEmail("diego@exemplo.com");
        assertThat(metas.findByColaboradorIdOrderByPrazoAscIdAsc(carla.id())).hasSize(2);
        assertThat(metas.findByColaboradorIdOrderByPrazoAscIdAsc(diego.id())).isNotEmpty();
    }

    @Test
    void consolidadoBateComAsRespostasArmazenadas() {
        Long ciclo = ciclo20261().id();
        List<RespostaConsolidavel> lista = respostas.respostasDoCiclo(ciclo);
        assertThat(lista).isNotEmpty();
        for (ResultadoConsolidado r : consolidado.findAll().stream().filter(r -> r.getCicloId().equals(ciclo)).toList()) {
            List<RespostaConsolidavel> doGrupo = lista.stream()
                    .filter(x -> x.avaliadoId().equals(r.getAvaliadoId()) && x.competenciaId().equals(r.getCompetenciaId())
                            && x.relacao() == r.getGrupo() && !x.naoAvalia() && x.nota() != null)
                    .toList();
            BigDecimal soma = doGrupo.stream().map(x -> BigDecimal.valueOf(x.nota())).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal esperada = soma.divide(BigDecimal.valueOf(doGrupo.size()), 2, RoundingMode.HALF_UP);
            long avaliacoes = doGrupo.stream().map(RespostaConsolidavel::avaliacaoId).distinct().count();
            assertThat(r.getMedia()).isEqualByComparingTo(esperada);
            assertThat(r.getNumRespostas()).isEqualTo((int) avaliacoes);
            assertThat(r.getNumRespostas()).isLessThanOrEqualTo(
                    respostas.avaliacoesEnviadas(ciclo, r.getAvaliadoId(), r.getGrupo()));
            assertThat(r.getEscalaMax()).isIn(5, 10);
        }
    }

    @Test
    void recalcularGeraExatamenteOMesmoResultado() {
        Long ciclo = ciclo20261().id();
        List<String> antes = retrato(ciclo);
        consolidacao.consolidarResultados(ciclo);
        consolidacao.consolidarResultados(ciclo);
        assertThat(retrato(ciclo)).isNotEmpty().isEqualTo(antes);
    }

    @Test
    void botaoRecalcularSoParaORhEAuditado() throws Exception {
        Long ciclo = ciclo20261().id();
        List<String> antes = retrato(ciclo);
        mvc.perform(get("/resultados").param("ciclo", ciclo.toString()).with(como("rh@exemplo.com")))
                .andExpect(content().string(containsString("Recalcular")));
        mvc.perform(post("/resultados/" + ciclo + "/recalcular").with(csrf()).with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/resultados/" + ciclo + "/recalcular").with(csrf()).with(como("rh@exemplo.com")))
                .andExpect(redirectedUrl("/resultados?ciclo=" + ciclo))
                .andExpect(flash().attribute("sucesso", containsString("mesmo resultado")));
        assertThat(retrato(ciclo)).isEqualTo(antes);
        assertThat(auditoria.listar(0).getContent()).anyMatch(r -> r.getAcao().equals("RESULTADO_RECALCULADO"));

        Long aberto = ciclos.abertos().get(0).id();
        mvc.perform(post("/resultados/" + aberto + "/recalcular").with(csrf()).with(como("rh@exemplo.com")))
                .andExpect(flash().attribute("erro", containsString("encerrado")));
    }

    @Test
    void encerrarOCicloConsolidaPeloEvento() {
        Long aberto = ciclos.abertos().get(0).id();
        assertThat(consolidado.existsByCicloId(aberto)).isFalse();
        ciclos.encerrar(aberto, true);
        assertThat(consolidado.existsByCicloId(aberto)).isTrue();
        assertThat(auditoria.listar(0).getContent()).anyMatch(r -> r.getAcao().equals("RESULTADO_CONSOLIDADO"));
    }

    @Test
    void comentariosVisiveisAparecemSemAutorNoPainel() throws Exception {
        Long ciclo = ciclo20261().id();
        PessoaResumo bruno = usuarios.buscarPorEmail("gestor@exemplo.com");
        List<ComentarioResumo> comentarios = respostas.comentariosVisiveis(ciclo, bruno.id());
        assertThat(comentarios).isNotEmpty();
        // um trecho só com letras e espaços, que a tela mostra sem nenhum escape
        String trecho = comentarios.stream().map(ComentarioResumo::texto)
                .map(t -> t.substring(0, Math.min(20, t.length())))
                .filter(t -> t.matches("[\\p{L} ]+")).findFirst().orElseThrow();
        mvc.perform(get("/resultados/" + ciclo + "/pessoas/" + bruno.id()).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sem identificação de quem escreveu")))
                .andExpect(content().string(containsString(trecho)));
    }

    @Test
    void resultadoAnteriorParaOGestorNoUc03() {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        CicloResumo anterior = ciclo20261();
        Long atual = ciclos.abertos().get(0).id();

        Optional<ResumoResultadoAnterior> r = resultadoAnterior.doAvaliado(carla.id(), atual);
        assertThat(r).isPresent();
        assertThat(r.get().cicloNome()).isEqualTo("Ciclo 2026.1");
        assertThat(r.get().escalaMax()).isEqualTo(5);
        assertThat(r.get().competencias()).isNotEmpty();
        assertThat(r.get().mediaGeral()).isNotNull();
        // Antes do 2026.1 vem o 2025.2; antes deste, nenhum (extensão 4b).
        CicloResumo primeiro = ciclos.encerrados().stream().filter(c -> c.nome().equals("Ciclo 2025.2"))
                .findFirst().orElseThrow();
        assertThat(resultadoAnterior.doAvaliado(carla.id(), anterior.id())).get()
                .extracting(ResumoResultadoAnterior::cicloNome).isEqualTo("Ciclo 2025.2");
        assertThat(resultadoAnterior.doAvaliado(carla.id(), primeiro.id())).isEmpty();
    }

    @Test
    void cargaDeExemploTemEvolucaoEntreCiclos() throws Exception {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        mvc.perform(get("/resultados/" + ciclo20261().id() + "/pessoas/" + carla.id())
                        .with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("graficoEvolucao")))
                .andExpect(content().string(containsString("Ciclo 2025.2")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Ainda não há histórico para comparar"))));
    }

    @Test
    void resultadoAnteriorAplicaOMinimoDeTres() {
        CicloResumo c = ciclo20261();
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        Long competencia = consolidado.findByCicloIdAndAvaliadoId(c.id(), carla.id()).get(0).getCompetenciaId();
        consolidado.apagarDoCiclo(c.id());
        consolidado.saveAll(List.of(
                new ResultadoConsolidado(c.id(), carla.id(), competencia, TipoAvaliador.CHEFE, new BigDecimal("4.25"), 1, 5),
                new ResultadoConsolidado(c.id(), carla.id(), competencia, TipoAvaliador.PAR, new BigDecimal("1.00"), 2, 5)));
        ResumoResultadoAnterior r = resultadoAnterior.doAvaliado(carla.id(), ciclos.abertos().get(0).id()).orElseThrow();
        assertThat(r.competencias()).singleElement().satisfies(m -> {
            assertThat(m.media()).isEqualTo(4.25);
            assertThat(m.mediaTexto()).isEqualTo("4,3"); // RNF-12: mesmo arredondamento da tela de resultado
        });
        assertThat(r.mediaGeralTexto()).isEqualTo("4,3");
        assertThat(RegrasDeExibicao.MINIMO_RESPOSTAS).isEqualTo(3);
    }

    /** O consolidado do ciclo como texto, em ordem fixa, para comparar dois cálculos. */
    private List<String> retrato(Long ciclo) {
        return consolidado.findAll().stream().filter(r -> r.getCicloId().equals(ciclo))
                .sorted(Comparator.comparing(ResultadoConsolidado::getAvaliadoId)
                        .thenComparing(ResultadoConsolidado::getCompetenciaId)
                        .thenComparing(ResultadoConsolidado::getGrupo))
                .map(r -> r.getAvaliadoId() + "|" + r.getCompetenciaId() + "|" + r.getGrupo() + "|"
                        + r.getMedia().toPlainString() + "|" + r.getNumRespostas() + "|" + r.getEscalaMax())
                .collect(Collectors.toList());
    }
}
