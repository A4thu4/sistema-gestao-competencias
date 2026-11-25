package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.QuestionarioResumo;

import java.util.List;

/** Questionário escolhido para um cargo do ciclo e os publicados disponíveis para ele (UC-02, passo 7). */
public record QuestionarioDoCargo(Long cargoId, String cargoNome, int avaliados, Long questionarioId,
                                  String questionarioNome, List<QuestionarioResumo> opcoes) {
}
