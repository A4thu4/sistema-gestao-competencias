package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.List;

/**
 * Validação do ciclo antes de agendar (UC-02, passos 8 e 10). Pendências impedem o agendamento; alertas
 * (menos de 3 pares ou subordinados, RNF-02) só avisam. {@code semQuestionario} (8a) e {@code semPerguntas} (8b)
 * vêm separados para a tela oferecer as ações de correção.
 */
public record Verificacao(List<String> pendencias, List<String> alertas, List<CargoSemQuestionario> semQuestionario,
                          List<TipoSemPerguntas> semPerguntas) {

    public boolean podeAgendar() {
        return pendencias.isEmpty();
    }

    public record CargoSemQuestionario(Long cargoId, String cargoNome) {
    }

    public record TipoSemPerguntas(Long cargoId, String cargoNome, Long questionarioId, String questionarioNome,
                                   TipoAvaliador tipo) {
    }
}
