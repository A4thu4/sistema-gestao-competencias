package br.edu.pucgoias.sgc.resultados.servico;

import java.math.BigDecimal;
import java.util.List;

/** Série de um gráfico (Chart.js): rótulo e um valor por ponto; {@code null} quando o valor não pode aparecer. */
public record SerieGrafico(String rotulo, List<BigDecimal> valores) {
}
