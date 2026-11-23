package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

/** Pessoa na lista de planos: último resultado liberado (base do plano) e as metas. */
public record MembroPlano(PessoaResumo pessoa, CicloResumo ultimoLiberado, long metasAbertas, long metasConcluidas,
                          long metasTotal) {

    public boolean podeTerPlano() {
        return ultimoLiberado != null;
    }
}
