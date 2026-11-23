package br.edu.pucgoias.sgc.resultados.servico;

import java.util.List;

/** Gráfico radar do painel: uma série por grupo exibível, um eixo por competência (RF-20). */
public record DadosRadar(List<String> competencias, List<SerieGrafico> series, int escalaMax) {
}
