package br.edu.pucgoias.sgc.avaliacoes.dominio;

/** Tipo da observação do gestor (RF-17): ponto positivo ou ponto de melhoria. */
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
