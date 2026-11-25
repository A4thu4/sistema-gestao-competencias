package br.edu.pucgoias.sgc.avaliacoes.servico;

/**
 * Comentário de pergunta aberta que pode aparecer nos resultados (não ocultado na moderação, RF-22). Sem autor e sem
 * relação (RNF-01): só o texto, a competência e a pergunta. {@code autoavaliacao} marca o comentário que a própria
 * pessoa avaliada escreveu, que a tela mostra à parte.
 */
public record ComentarioResumo(String texto, Long competenciaId, String competenciaNome, Long perguntaId,
                               String perguntaTexto, boolean autoavaliacao) {
}
