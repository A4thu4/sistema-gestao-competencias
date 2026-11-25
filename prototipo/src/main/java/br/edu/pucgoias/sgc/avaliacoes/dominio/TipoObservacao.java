package br.edu.pucgoias.sgc.avaliacoes.dominio;

public enum TipoObservacao {
    POSITIVO("Ponto positivo"),
    MELHORIA("Ponto de melhoria");

    private final String rotulo;

    TipoObservacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
