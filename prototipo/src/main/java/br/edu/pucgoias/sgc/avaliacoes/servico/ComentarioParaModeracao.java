package br.edu.pucgoias.sgc.avaliacoes.servico;

/**
 * Comentário de pergunta aberta na tela de moderação do RH (UC-09). Sem autor e sem relação (RNF-01): só a pessoa
 * avaliada, a competência e a pergunta. {@code respostaId} é o id da resposta, usado na URL das ações.
 */
public record ComentarioParaModeracao(Long respostaId, String avaliadoNome, String competenciaNome,
                                      String perguntaTexto, String texto, boolean oculto, String motivoOcultacao) {
}
