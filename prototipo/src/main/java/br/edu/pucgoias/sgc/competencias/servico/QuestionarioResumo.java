package br.edu.pucgoias.sgc.competencias.servico;

/**
 * Questionário em poucas palavras, para listas e para a criação de ciclos. {@code situacao} é "RASCUNHO" ou
 * "PUBLICADO"; {@code escalaMax} é 5 ou 10 (a escala vai de 1 até ele).
 */
public record QuestionarioResumo(Long id, String nome, Long cargoId, int escalaMax, String situacao) {

    public boolean publicado() {
        return "PUBLICADO".equals(situacao);
    }

    public String situacaoRotulo() {
        return publicado() ? "Publicado" : "Rascunho";
    }
}
