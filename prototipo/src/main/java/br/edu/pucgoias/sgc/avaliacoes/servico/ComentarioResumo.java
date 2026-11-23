package br.edu.pucgoias.sgc.avaliacoes.servico;

/**
 * Comentário de pergunta aberta que pode aparecer nos resultados (não ocultado na moderação, RF-22). Sem autor e sem
 * relação (RNF-01): só o texto, a competência e a pergunta.
 */
public record ComentarioResumo(String texto, Long competenciaId, String competenciaNome, Long perguntaId,
                               String perguntaTexto) {
}
