package br.edu.pucgoias.sgc.privacidade.dominio;

/** Direitos do titular cobertos pelo RF-30. */
public enum TipoSolicitacao {
    ACESSO("Acesso aos dados", "Receber uma cópia dos dados que o sistema guarda sobre você."),
    CORRECAO("Correção", "Corrigir um dado pessoal que está errado ou desatualizado."),
    EXCLUSAO("Exclusão", "Pedir que os seus dados pessoais sejam apagados.");

    private final String rotulo;
    private final String explicacao;

    TipoSolicitacao(String rotulo, String explicacao) {
        this.rotulo = rotulo;
        this.explicacao = explicacao;
    }

    public String getRotulo() { return rotulo; }
    public String getExplicacao() { return explicacao; }
}
