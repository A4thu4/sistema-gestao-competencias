package br.edu.pucgoias.sgc.privacidade.dominio;

/** Situação de uma solicitação do titular (UC-05, passo 10 e 10a). */
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
