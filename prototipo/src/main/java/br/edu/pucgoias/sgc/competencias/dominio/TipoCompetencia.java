package br.edu.pucgoias.sgc.competencias.dominio;

/** Tipo da competência no catálogo (RF-01). */
public enum TipoCompetencia {
    TECNICA("Técnica"),
    COMPORTAMENTAL("Comportamental");

    private final String rotulo;

    TipoCompetencia(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
