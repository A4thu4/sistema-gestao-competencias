package br.edu.pucgoias.sgc.privacidade.dominio;

public enum SituacaoSolicitacao {
    EM_ANALISE("Em análise"),
    ATENDIDA("Atendida"),
    ATENDIDA_PARCIAL("Atendida em parte"),
    NEGADA("Negada");

    private final String rotulo;

    SituacaoSolicitacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() { return rotulo; }

    public boolean concluida() { return this != EM_ANALISE; }
}
