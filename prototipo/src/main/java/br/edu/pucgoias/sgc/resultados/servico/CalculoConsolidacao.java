package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Cálculo da consolidação (RF-19), sem banco e sem Spring, para ser testado por unidade. Para cada pessoa avaliada ×
 * competência × grupo de avaliadores: média aritmética de todas as notas das perguntas de escala, sem os itens
 * "não tenho como avaliar" (RF-06), arredondada em 2 casas (HALF_UP), e o número de avaliações do grupo que deram
 * ao menos uma nota na competência (para o mínimo de 3, RNF-02).
 *
 * <p>Reprodutível (RNF-12): só usa somas exatas em {@link BigDecimal} e devolve as linhas sempre na mesma ordem
 * (pessoa, competência, grupo), qualquer que seja a ordem das respostas recebidas. Uma combinação sem nenhuma nota
 * (todos marcaram "não tenho como avaliar") não gera linha.
 */
public final class CalculoConsolidacao {

    public static final int CASAS = 2;

    private CalculoConsolidacao() {
    }

    /**
     * @param notas             respostas de escala do ciclo (sem autor)
     * @param escalaDoAvaliado  nota máxima do questionário usado para cada pessoa (5 ou 10)
     */
    public static List<LinhaConsolidada> consolidar(Collection<NotaParaConsolidar> notas,
                                                    Function<Long, Integer> escalaDoAvaliado) {
        Map<Chave, Acumulador> grupos = new TreeMap<>();
        for (NotaParaConsolidar n : notas) {
            if (!n.contaNaMedia()) {
                continue;
            }
            grupos.computeIfAbsent(new Chave(n.avaliadoId(), n.competenciaId(), n.grupo()), k -> new Acumulador())
                    .somar(n);
        }
        List<LinhaConsolidada> linhas = new ArrayList<>();
        grupos.forEach((k, a) -> linhas.add(new LinhaConsolidada(k.avaliadoId(), k.competenciaId(), k.grupo(),
                a.media(), a.avaliacoes.size(), escalaDoAvaliado.apply(k.avaliadoId()))));
        return linhas;
    }

    private record Chave(Long avaliadoId, Long competenciaId, TipoAvaliador grupo) implements Comparable<Chave> {

        private static final Comparator<Chave> ORDEM = Comparator.comparing(Chave::avaliadoId)
                .thenComparing(Chave::competenciaId).thenComparing(Chave::grupo);

        @Override
        public int compareTo(Chave outra) {
            return ORDEM.compare(this, outra);
        }
    }

    private static final class Acumulador {
        private BigDecimal soma = BigDecimal.ZERO;
        private int quantidade;
        private final Set<Object> avaliacoes = new HashSet<>();

        void somar(NotaParaConsolidar n) {
            soma = soma.add(BigDecimal.valueOf(n.nota()));
            quantidade++;
            avaliacoes.add(n.avaliacaoId());
        }

        BigDecimal media() {
            return soma.divide(BigDecimal.valueOf(quantidade), CASAS, RoundingMode.HALF_UP);
        }
    }
}
