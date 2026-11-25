package br.edu.pucgoias.sgc.identidade.dominio;

/** O avaliador externo não tem perfil: entra só pelo link individual. */
public enum Perfil {
    RH("RH"),
    GESTOR("Gestor"),
    COLABORADOR("Colaborador");

    private final String rotulo;

    Perfil(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
