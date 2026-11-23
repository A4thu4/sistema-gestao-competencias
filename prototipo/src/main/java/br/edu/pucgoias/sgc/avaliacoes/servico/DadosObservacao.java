package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.time.LocalDate;

/** Dados informados pelo gestor para registrar ou alterar uma observação. {@code tipo}: "POSITIVO" ou "MELHORIA". */
public record DadosObservacao(Long colaboradorId, String tipo, String descricao, LocalDate dataFato) {
}
