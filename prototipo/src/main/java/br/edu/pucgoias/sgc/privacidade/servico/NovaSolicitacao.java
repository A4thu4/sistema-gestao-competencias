package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;

/**
 * Pedido do titular. {@code dadoIncorreto} e {@code valorCorreto} só valem para correção (UC-05, 7b);
 * {@code confirmouAvisoCiclo} é a confirmação da exclusão durante um ciclo aberto (7c).
 */
public record NovaSolicitacao(TipoSolicitacao tipo, String descricao, String dadoIncorreto, String valorCorreto,
                              boolean confirmouAvisoCiclo) {
}
