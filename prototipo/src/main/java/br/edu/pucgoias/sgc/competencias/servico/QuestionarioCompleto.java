package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/**
 * Questionário com tudo o que o avaliador vê: escala com a descrição de cada nível e competências em ordem, cada
 * uma com as suas perguntas. É o que os pacotes avaliacoes e resultados usam para montar o formulário e o relatório.
 *
 * @param origemId questionário de onde este foi copiado, ou {@code null}
 */
public record QuestionarioCompleto(Long id, String nome, Long cargoId, int escalaMax, String situacao, boolean modelo,
                                   Long origemId, List<NivelResumo> niveis,
                                   List<CompetenciaDoQuestionario> competencias) {

    public boolean publicado() {
        return "PUBLICADO".equals(situacao);
    }

    /** Todas as perguntas, na ordem em que aparecem. */
    public List<PerguntaResumo> perguntas() {
        return competencias.stream().flatMap(c -> c.perguntas().stream()).toList();
    }

    public int totalPerguntas() {
        return competencias.stream().mapToInt(c -> c.perguntas().size()).sum();
    }

    public List<Long> competenciaIds() {
        return competencias.stream().map(CompetenciaDoQuestionario::id).toList();
    }
}
