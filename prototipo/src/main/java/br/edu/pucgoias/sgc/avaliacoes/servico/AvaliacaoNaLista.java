package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

/** Uma avaliação na lista do avaliador (UC-03, passo 2). */
public record AvaliacaoNaLista(DesignacaoResumo designacao, PessoaResumo avaliado) {

    public String relacaoTexto() {
        return Relacoes.texto(designacao.relacao());
    }

    public boolean anonima() {
        return Relacoes.anonima(designacao.relacao());
    }
}
