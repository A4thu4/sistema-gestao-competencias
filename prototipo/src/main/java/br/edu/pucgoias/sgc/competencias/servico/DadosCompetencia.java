package br.edu.pucgoias.sgc.competencias.servico;

/** Dados para cadastrar ou editar uma competência (RF-01). {@code tipo}: "TECNICA" ou "COMPORTAMENTAL". */
public record DadosCompetencia(String nome, String descricao, String tipo) {
}
