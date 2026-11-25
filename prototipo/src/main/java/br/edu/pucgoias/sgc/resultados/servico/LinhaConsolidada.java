package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.math.BigDecimal;

/**
 * Média de um grupo de avaliadores numa competência de uma pessoa (uma linha de {@code resultado_consolidado}).
 * {@code numRespostas} = avaliações do grupo que deram ao menos uma nota na competência.
 */
public record LinhaConsolidada(Long avaliadoId, Long competenciaId, TipoAvaliador grupo, BigDecimal media,
                               int numRespostas, int escalaMax) {
}
