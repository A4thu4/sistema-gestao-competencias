package br.edu.pucgoias.sgc.ciclos.servico;

/** Avaliador de uma pessoa no ciclo, na montagem (UC-02, passo 9). Sem nada das respostas. */
public record AvaliadorNoCiclo(Long designacaoId, Long pessoaId, String nome, String email, boolean externo,
                               String situacao, boolean conviteEnviado) {
}
