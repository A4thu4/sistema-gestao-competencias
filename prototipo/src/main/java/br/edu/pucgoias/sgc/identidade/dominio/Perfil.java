package br.edu.pucgoias.sgc.identidade.dominio;

/** Perfis de acesso (RF-33). O avaliador externo não tem perfil: entra só pelo link individual (RF-34). */
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
