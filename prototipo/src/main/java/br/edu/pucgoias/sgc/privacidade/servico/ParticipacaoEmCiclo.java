package br.edu.pucgoias.sgc.privacidade.servico;

/**
 * Pergunta ao pacote ciclos se a pessoa participa de um ciclo de avaliação em andamento, para avisar antes de um
 * pedido de exclusão. Sem implementação, a resposta é "não participa" e o aviso não aparece.
 */
public interface ParticipacaoEmCiclo {

    boolean participaDeCicloAberto(Long usuarioId);
}
