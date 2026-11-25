package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;

/**
 * Pedido do titular. {@code dadoIncorreto} e {@code valorCorreto} só valem para correção;
 * {@code confirmouAvisoCiclo} é a confirmação da exclusão durante um ciclo aberto.
 */
public record NovaSolicitacao(TipoSolicitacao tipo, String descricao, String dadoIncorreto, String valorCorreto,
                              boolean confirmouAvisoCiclo) {
}
