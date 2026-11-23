package br.edu.pucgoias.sgc.competencias.dominio;

import jakarta.persistence.Embeddable;

/** Competência escolhida para o questionário, com a posição em que aparece para o avaliador. */
@Embeddable
public class CompetenciaNoQuestionario {

    private Long competenciaId;
    private short ordem;

    protected CompetenciaNoQuestionario() {
    }

    public CompetenciaNoQuestionario(Long competenciaId, int ordem) {
        this.competenciaId = competenciaId;
        this.ordem = (short) ordem;
    }

    public Long getCompetenciaId() { return competenciaId; }
    public int getOrdem() { return ordem; }
}
