package br.edu.pucgoias.sgc.resultados;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.resultados.servico.CalculoConsolidacao;
import br.edu.pucgoias.sgc.resultados.servico.LinhaConsolidada;
import br.edu.pucgoias.sgc.resultados.servico.NotaParaConsolidar;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.AUTO;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.CHEFE;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.PAR;
import static org.assertj.core.api.Assertions.assertThat;

/** Testes de unidade do cálculo da consolidação (RF-19, RF-06, RNF-12). */
class CalculoConsolidacaoTest {

    private static final Long CARLA = 1L;
    private static final Long DIEGO = 2L;
    private static final Long COMUNICACAO = 10L;
    private static final Long ATENDIMENTO = 11L;

    @Test
    void mediaAritmeticaDeTodasAsNotasDoGrupoArredondadaEmDuasCasas() {
        UUID a1 = UUID.randomUUID();
        UUID a2 = UUID.randomUUID();
        UUID a3 = UUID.randomUUID();
        List<LinhaConsolidada> linhas = CalculoConsolidacao.consolidar(List.of(
                nota(a1, CARLA, PAR, COMUNICACAO, 4), nota(a1, CARLA, PAR, COMUNICACAO, 5),
                nota(a2, CARLA, PAR, COMUNICACAO, 4),
                nota(a3, CARLA, PAR, COMUNICACAO, 3)), id -> 5);

        assertThat(linhas).hasSize(1);
        LinhaConsolidada l = linhas.get(0);
        // (4 + 5 + 4 + 3) / 4 = 4,00
        assertThat(l.media()).isEqualByComparingTo("4.00");
        assertThat(l.media().scale()).isEqualTo(2);
        assertThat(l.numRespostas()).isEqualTo(3);
        assertThat(l.escalaMax()).isEqualTo(5);
    }

    @Test
    void arredondaParaCimaNaMetade() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        // 2 + 3 + 3 = 8 / 3 = 2,666… → 2,67 ; 4 + 4 + 5 = 13 / 3 = 4,333… → 4,33 ; 1 + 2 → 1,50
        List<LinhaConsolidada> linhas = CalculoConsolidacao.consolidar(List.of(
                nota(a, CARLA, PAR, COMUNICACAO, 2), nota(b, CARLA, PAR, COMUNICACAO, 3), nota(c, CARLA, PAR, COMUNICACAO, 3),
                nota(a, CARLA, PAR, ATENDIMENTO, 4), nota(b, CARLA, PAR, ATENDIMENTO, 4), nota(c, CARLA, PAR, ATENDIMENTO, 5),
                nota(a, DIEGO, CHEFE, COMUNICACAO, 1), nota(a, DIEGO, CHEFE, COMUNICACAO, 2)), id -> 5);

        assertThat(linhas).extracting(LinhaConsolidada::media).containsExactly(
                new BigDecimal("2.67"), new BigDecimal("4.33"), new BigDecimal("1.50"));
    }

    @Test
    void naoTenhoComoAvaliarFicaForaDaMediaEDaContagem() {
        UUID a1 = UUID.randomUUID();
        UUID a2 = UUID.randomUUID();
        UUID a3 = UUID.randomUUID();
        List<LinhaConsolidada> linhas = CalculoConsolidacao.consolidar(List.of(
                nota(a1, CARLA, PAR, COMUNICACAO, 5),
                naoAvalia(a1, CARLA, PAR, COMUNICACAO),
                nota(a2, CARLA, PAR, COMUNICACAO, 3),
                // a3 marcou "não tenho como avaliar" em tudo: não conta como resposta do grupo nessa competência
                naoAvalia(a3, CARLA, PAR, COMUNICACAO),
                naoAvalia(a3, CARLA, PAR, COMUNICACAO)), id -> 5);

        assertThat(linhas).hasSize(1);
        assertThat(linhas.get(0).media()).isEqualByComparingTo("4.00");
        assertThat(linhas.get(0).numRespostas()).isEqualTo(2);
    }

    @Test
    void grupoSoComNaoTenhoComoAvaliarNaoGeraLinha() {
        UUID a1 = UUID.randomUUID();
        List<LinhaConsolidada> linhas = CalculoConsolidacao.consolidar(List.of(
                naoAvalia(a1, CARLA, CHEFE, COMUNICACAO), nota(a1, CARLA, CHEFE, ATENDIMENTO, 4)), id -> 5);

        assertThat(linhas).extracting(LinhaConsolidada::competenciaId).containsExactly(ATENDIMENTO);
    }

    @Test
    void contaAvaliacoesPorGrupoSeparadamente() {
        UUID auto = UUID.randomUUID();
        UUID chefe = UUID.randomUUID();
        UUID par1 = UUID.randomUUID();
        UUID par2 = UUID.randomUUID();
        List<LinhaConsolidada> linhas = CalculoConsolidacao.consolidar(List.of(
                nota(auto, CARLA, AUTO, COMUNICACAO, 5),
                nota(chefe, CARLA, CHEFE, COMUNICACAO, 4),
                nota(par1, CARLA, PAR, COMUNICACAO, 3), nota(par1, CARLA, PAR, COMUNICACAO, 4),
                nota(par2, CARLA, PAR, COMUNICACAO, 2)), id -> 10);

        assertThat(linhas).extracting(LinhaConsolidada::grupo).containsExactly(CHEFE, AUTO, PAR);
        assertThat(linhas).extracting(LinhaConsolidada::numRespostas).containsExactly(1, 1, 2);
        assertThat(linhas).extracting(LinhaConsolidada::escalaMax).containsOnly(10);
        assertThat(linhas.get(2).media()).isEqualByComparingTo("3.00");
    }

    @Test
    void mesmoConjuntoDeRespostasGeraSempreOMesmoResultado() {
        Random sorteio = new Random(42);
        List<NotaParaConsolidar> notas = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            UUID avaliacao = UUID.randomUUID();
            TipoAvaliador grupo = TipoAvaliador.values()[sorteio.nextInt(5)];
            Long pessoa = 1L + sorteio.nextInt(4);
            for (long comp = 10; comp < 14; comp++) {
                notas.add(sorteio.nextInt(8) == 0 ? naoAvalia(avaliacao, pessoa, grupo, comp)
                        : nota(avaliacao, pessoa, grupo, comp, 1 + sorteio.nextInt(5)));
            }
        }
        List<LinhaConsolidada> primeira = CalculoConsolidacao.consolidar(notas, id -> 5);
        for (int vez = 0; vez < 5; vez++) {
            List<NotaParaConsolidar> embaralhadas = new ArrayList<>(notas);
            Collections.shuffle(embaralhadas, new Random(vez));
            assertThat(CalculoConsolidacao.consolidar(embaralhadas, id -> 5)).isEqualTo(primeira);
        }
    }

    private static NotaParaConsolidar nota(UUID avaliacao, Long pessoa, TipoAvaliador grupo, Long competencia, int nota) {
        return new NotaParaConsolidar(avaliacao, pessoa, grupo, competencia, nota, false);
    }

    private static NotaParaConsolidar naoAvalia(UUID avaliacao, Long pessoa, TipoAvaliador grupo, Long competencia) {
        return new NotaParaConsolidar(avaliacao, pessoa, grupo, competencia, null, true);
    }
}
