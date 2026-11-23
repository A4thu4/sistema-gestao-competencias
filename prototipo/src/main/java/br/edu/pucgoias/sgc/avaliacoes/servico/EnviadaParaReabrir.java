package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;

/**
 * Avaliação enviada que o RH pode reabrir (UC-09): quem avaliou quem e em que relação. É o que o RH já vê no
 * andamento do ciclo; as respostas não aparecem.
 */
public record EnviadaParaReabrir(DesignacaoResumo designacao, String avaliadorNome, String avaliadoNome) {

    public String relacaoRotulo() {
        return designacao.relacao().getRotulo();
    }
}
