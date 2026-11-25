package br.edu.pucgoias.sgc.ciclos.dominio;

public enum SituacaoCiclo {
    RASCUNHO("Rascunho"),
    AGENDADO("Agendado"),
    ABERTO("Aberto"),
    ENCERRADO("Encerrado");

    private final String rotulo;

    SituacaoCiclo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
