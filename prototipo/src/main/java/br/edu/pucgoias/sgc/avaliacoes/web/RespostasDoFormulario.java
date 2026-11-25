package br.edu.pucgoias.sgc.avaliacoes.web;

import br.edu.pucgoias.sgc.avaliacoes.servico.RespostaInformada;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;

import java.util.HashMap;
import java.util.Map;

/**
 * Lê as respostas enviadas pelo formulário da avaliação: {@code nota_<perguntaId>} (número ou "NA" = não tenho como
 * avaliar) e {@code texto_<perguntaId>}.
 */
final class RespostasDoFormulario {

    static final String NAO_AVALIA = "NA";

    private RespostasDoFormulario() {
    }

    static Map<Long, RespostaInformada> ler(Map<String, String> parametros) {
        Map<Long, String> notas = new HashMap<>();
        Map<Long, String> textos = new HashMap<>();
        parametros.forEach((nome, valor) -> {
            if (nome.startsWith("nota_")) {
                id(nome.substring(5), notas, valor);
            } else if (nome.startsWith("texto_")) {
                id(nome.substring(6), textos, valor);
            }
        });
        Map<Long, RespostaInformada> respostas = new HashMap<>();
        notas.forEach((id, nota) -> respostas.put(id, uma(nota, textos.get(id))));
        textos.forEach((id, texto) -> respostas.putIfAbsent(id, uma(null, texto)));
        return respostas;
    }

    /** Uma resposta do salvamento automático. */
    static RespostaInformada uma(String nota, String texto) {
        if (nota != null && nota.equals(NAO_AVALIA)) {
            return new RespostaInformada(null, true, null);
        }
        Integer valor = null;
        if (nota != null && !nota.isBlank()) {
            try {
                valor = Integer.valueOf(nota.trim());
            } catch (NumberFormatException e) {
                throw new RegraDeNegocioException("Nota inválida.");
            }
        }
        return new RespostaInformada(valor, false, texto);
    }

    private static void id(String texto, Map<Long, String> destino, String valor) {
        try {
            destino.put(Long.valueOf(texto), valor);
        } catch (NumberFormatException e) {
            // parâmetro que não é de pergunta: ignorado
        }
    }
}
