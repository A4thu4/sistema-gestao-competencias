package br.edu.pucgoias.sgc.ciclos.dominio;

/** Andamento de uma designação. Não guarda nada da resposta. */
public enum SituacaoDesignacao {
    NAO_INICIADA("Não iniciada"),
    EM_PREENCHIMENTO("Em preenchimento"),
    ENVIADA("Enviada");

    private final String rotulo;

    SituacaoDesignacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
