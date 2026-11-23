package br.edu.pucgoias.sgc.ciclos;

import br.edu.pucgoias.sgc.ciclos.dominio.Ciclo;
import br.edu.pucgoias.sgc.ciclos.dominio.Designacao;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoCiclo;
import br.edu.pucgoias.sgc.ciclos.servico.DadosCiclo;
import br.edu.pucgoias.sgc.ciclos.servico.MontagemCicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Testes de unidade das regras do ciclo (sem banco). */
class RegrasDoCicloTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 23);

    @Test
    void validacaoDosDadosDoCiclo() {
        Map<String, String> ok = MontagemCicloService.errosDosDados(
                new DadosCiclo("Ciclo", Set.of(TipoAvaliador.CHEFE), HOJE, HOJE.plusDays(1)), HOJE);
        assertThat(ok).isEmpty();

        Map<String, String> erros = MontagemCicloService.errosDosDados(
                new DadosCiclo(" ", Set.of(), HOJE.minusDays(1), HOJE.minusDays(1)), HOJE);
        assertThat(erros).containsOnlyKeys("nome", "tipos", "abertura", "encerramento");

        assertThat(MontagemCicloService.errosDosDados(
                new DadosCiclo("C", Set.of(TipoAvaliador.AUTO), HOJE.plusDays(3), HOJE.plusDays(2)), HOJE))
                .containsOnlyKeys("encerramento");
        assertThat(MontagemCicloService.errosDosDados(new DadosCiclo("C", Set.of(TipoAvaliador.AUTO), null, null), HOJE))
                .containsOnlyKeys("abertura", "encerramento");
        assertThat(MontagemCicloService.errosDosDados(
                new DadosCiclo("x".repeat(121), Set.of(TipoAvaliador.PAR), HOJE, HOJE.plusDays(1)), HOJE))
                .containsOnlyKeys("nome");
        // O externo sozinho não é um tipo do RF-08.
        assertThat(MontagemCicloService.errosDosDados(
                new DadosCiclo("C", Set.of(TipoAvaliador.EXTERNO), HOJE, HOJE.plusDays(1)), HOJE))
                .containsOnlyKeys("tipos");
    }

    @Test
    void tokenDoExternoEAleatorioELongoEProprioParaUrl() throws Exception {
        Method novoToken = MontagemCicloService.class.getDeclaredMethod("novoToken");
        novoToken.setAccessible(true);
        Set<String> vistos = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            String t = (String) novoToken.invoke(null);
            assertThat(t).hasSize(43).matches("[A-Za-z0-9_-]+");
            vistos.add(t);
        }
        assertThat(vistos).hasSize(200);
    }

    @Test
    void transicoesDoCiclo() {
        Ciclo c = new Ciclo("Ciclo", Set.of(TipoAvaliador.CHEFE, TipoAvaliador.PAR), HOJE, HOJE.plusDays(10));
        assertThat(c.getSituacao()).isEqualTo(SituacaoCiclo.RASCUNHO);
        assertThat(c.tipos()).containsExactly(TipoAvaliador.CHEFE, TipoAvaliador.PAR);
        assertThatThrownBy(c::abrir).isInstanceOf(RegraDeNegocioException.class);
        c.agendar();
        assertThat(c.voltarARascunho()).isTrue();
        c.agendar();
        c.abrir();
        assertThat(c.isAberto()).isTrue();
        assertThat(c.getAbertoEm()).isNotNull();
        assertThatThrownBy(() -> c.alterarDados("Outro", Set.of(TipoAvaliador.AUTO), HOJE, HOJE.plusDays(2)))
                .hasMessageContaining("já está aberto");
        c.alterarEncerramento(HOJE.plusDays(20));
        assertThatThrownBy(c::liberarResultado).isInstanceOf(RegraDeNegocioException.class);
        c.encerrar();
        assertThat(c.isEncerrado()).isTrue();
        assertThat(c.getDataEncerramento()).isEqualTo(HOJE.plusDays(20));
        c.liberarResultado();
        assertThat(c.isResultadoLiberado()).isTrue();
    }

    @Test
    void retirarOUnicoTipoNaoEPermitido() {
        Ciclo c = new Ciclo("Ciclo", Set.of(TipoAvaliador.CHEFE), HOJE, HOJE.plusDays(10));
        assertThatThrownBy(() -> c.retirarTipo(TipoAvaliador.CHEFE)).isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void designacaoRespeitaARelacao() {
        assertThatThrownBy(() -> Designacao.interna(1L, 10L, 10L, TipoAvaliador.PAR))
                .hasMessageContaining("a si mesmo");
        assertThatThrownBy(() -> Designacao.interna(1L, 10L, 11L, TipoAvaliador.AUTO))
                .isInstanceOf(RegraDeNegocioException.class);
        Designacao d = Designacao.interna(1L, 10L, 11L, TipoAvaliador.CHEFE);
        d.marcarEmPreenchimento();
        d.marcarEnviada();
        assertThatThrownBy(d::marcarEmPreenchimento).isInstanceOf(RegraDeNegocioException.class);
        d.reabrir();
        assertThat(d.isPendente()).isTrue();
    }
}
