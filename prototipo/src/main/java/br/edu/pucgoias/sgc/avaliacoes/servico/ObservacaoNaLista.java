package br.edu.pucgoias.sgc.avaliacoes.servico;

/**
 * Linha da lista cronológica de um colaborador: a observação, quem registrou e se o gestor logado pode editá-la ou
 * excluí-la (só o autor, e só enquanto ela não entrou na avaliação de um ciclo encerrado). {@code travadaPor} é o
 * nome desse ciclo, ou {@code null}.
 */
public record ObservacaoNaLista(ObservacaoResumo observacao, String autor, boolean minha, boolean podeAlterar,
                                String travadaPor) {
}
