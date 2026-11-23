package br.edu.pucgoias.sgc.resultados.servico;

import java.time.LocalDate;

/** Dados de uma nova meta digitados pelo gestor (UC-06). {@code situacao}: nome de {@code SituacaoMeta}. */
public record DadosMeta(Long competenciaId, String descricao, String acoes, LocalDate prazo, String situacao) {
}
