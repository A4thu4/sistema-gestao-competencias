package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/** Competência dentro de um questionário, com as perguntas dela em ordem. {@code tipo}: "TECNICA" ou "COMPORTAMENTAL". */
public record CompetenciaDoQuestionario(Long id, String nome, String descricao, String tipo, boolean ativa,
                                        List<PerguntaResumo> perguntas) {

    public String tipoRotulo() {
        return "TECNICA".equals(tipo) ? "Técnica" : "Comportamental";
    }
}
