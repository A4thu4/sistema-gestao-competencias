package br.edu.pucgoias.sgc.avaliacoes.servico;

/**
 * Linha da lista cronológica de um colaborador: a observação, quem registrou e se o gestor logado pode editá-la ou
 * excluí-la (só o autor).
 */
public record ObservacaoNaLista(ObservacaoResumo observacao, String autor, boolean minha, boolean podeAlterar) {
}
