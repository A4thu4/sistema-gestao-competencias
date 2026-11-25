package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/**
 * Se o questionário já foi usado. Bloqueado = tem respostas ou está em algum ciclo: aí as competências, perguntas e
 * escala não mudam mais, e o RH duplica para fazer outro.
 */
public record UsoQuestionario(boolean possuiRespostas, List<String> ciclos) {

    public boolean bloqueado() {
        return possuiRespostas || !ciclos.isEmpty();
    }

    public String motivo() {
        if (possuiRespostas) {
            return "Este questionário já tem respostas" + (ciclos.isEmpty() ? "" : " no ciclo " + String.join(", ", ciclos))
                    + ". Para preservar o histórico ele não pode ser alterado; duplique-o para fazer uma nova versão.";
        }
        if (!ciclos.isEmpty()) {
            return "Este questionário está em uso no ciclo " + String.join(", ", ciclos)
                    + ". Para não mudar o que os avaliadores vão responder, ele não pode ser alterado; duplique-o para fazer uma nova versão.";
        }
        return "";
    }
}
