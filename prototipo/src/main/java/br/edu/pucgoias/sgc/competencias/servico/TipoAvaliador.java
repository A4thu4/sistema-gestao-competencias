package br.edu.pucgoias.sgc.competencias.servico;

/**
 * Relação do avaliador com o avaliado (RF-11). Os nomes são os mesmos da coluna {@code relacao} das tabelas
 * {@code designacao} e {@code avaliacao_enviada}, para os pacotes ciclos e avaliacoes converterem com
 * {@link #valueOf(String)}.
 */
public enum TipoAvaliador {
    CHEFE("Chefe"),
    AUTO("Autoavaliação"),
    PAR("Par"),
    SUBORDINADO("Subordinado"),
    EXTERNO("Externo");

    private final String rotulo;

    TipoAvaliador(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    /** Para o Thymeleaf: {@code ${t.rotulo}}. */
    public String getRotulo() {
        return rotulo;
    }
}
