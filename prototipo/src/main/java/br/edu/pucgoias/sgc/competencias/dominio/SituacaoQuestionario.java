package br.edu.pucgoias.sgc.competencias.dominio;

/** Rascunho pode ter pendências; publicado está completo e pode ser usado em ciclos. */
public enum SituacaoQuestionario {
    RASCUNHO("Rascunho"),
    PUBLICADO("Publicado");

    private final String rotulo;

    SituacaoQuestionario(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
