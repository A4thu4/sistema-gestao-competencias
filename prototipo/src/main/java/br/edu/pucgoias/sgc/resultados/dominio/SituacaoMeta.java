package br.edu.pucgoias.sgc.resultados.dominio;

/** Situação de uma meta do plano de desenvolvimento (RF-27). */
public enum SituacaoMeta {
    NAO_INICIADA("Não iniciada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String rotulo;

    SituacaoMeta(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    /** Não iniciada ou em andamento: ainda pede revisão (lembrete trimestral, RF-28). */
    public boolean aberta() {
        return this == NAO_INICIADA || this == EM_ANDAMENTO;
    }
}
