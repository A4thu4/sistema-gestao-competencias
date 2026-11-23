package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/**
 * Inversão de dependência: o competencias precisa saber se um questionário já foi usado (UC-01, extensão 2b), mas
 * quem sabe disso são os pacotes ciclos (ciclos que usam o questionário) e avaliacoes (respostas enviadas), que
 * dependem do competencias. Eles implementam esta interface como {@code @Component}; cada um pode implementar só o
 * método que lhe cabe. Sem nenhuma implementação, vale o padrão: sem respostas e sem ciclos.
 */
public interface ConsultaUsoQuestionario {

    /** O questionário já tem respostas (enviadas ou em rascunho) em algum ciclo. Implementação esperada: avaliacoes. */
    default boolean possuiRespostas(Long questionarioId) {
        return false;
    }

    /** Nomes dos ciclos (de qualquer situação) que usam o questionário. Implementação esperada: ciclos. */
    default List<String> ciclosQueUsam(Long questionarioId) {
        return List.of();
    }
}
