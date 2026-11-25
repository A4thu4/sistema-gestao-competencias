package br.edu.pucgoias.sgc.competencias.servico;

/**
 * Competência do catálogo para os outros pacotes. {@code tipo} é "TECNICA" ou "COMPORTAMENTAL".
 */
public record CompetenciaResumo(Long id, String nome, String descricao, String tipo, boolean ativa) {

    public boolean tecnica() {
        return "TECNICA".equals(tipo);
    }

    public String tipoRotulo() {
        return tecnica() ? "Técnica" : "Comportamental";
    }
}
