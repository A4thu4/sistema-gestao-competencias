package br.edu.pucgoias.sgc.identidade.servico;

/**
 * Cartão da página inicial. {@code destaque} é o número ou texto grande do cartão; {@code cor} é uma cor do
 * Bootstrap (primary, success, warning, danger, secondary).
 */
public record CartaoPainel(int ordem, String titulo, String destaque, String descricao, String link, String cor) {
}
