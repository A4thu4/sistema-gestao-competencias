package br.edu.pucgoias.sgc.privacidade.servico;

/**
 * Pergunta ao pacote ciclos se a pessoa participa de um ciclo de avaliação em andamento, para avisar antes de um
 * pedido de exclusão (UC-05, 7c). O ciclos implementa com um {@code @Component}; sem implementação, a resposta é
 * "não participa" e o aviso não aparece (inversão de dependência).
 */
public interface ParticipacaoEmCiclo {

    boolean participaDeCicloAberto(Long usuarioId);
}
