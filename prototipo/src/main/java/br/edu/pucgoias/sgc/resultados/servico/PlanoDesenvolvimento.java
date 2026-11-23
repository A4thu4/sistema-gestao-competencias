package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

import java.util.List;
import java.util.Map;

/**
 * Plano de desenvolvimento de uma pessoa (UC-06) do ponto de vista de quem consulta.
 *
 * @param cicloBase       último ciclo com resultado liberado (base das sugestões), ou {@code null}
 * @param competencias    competências que podem receber meta (as do resultado do ciclo base), id → nome
 * @param podeCriar       gestor direto com resultado liberado
 * @param podeAtualizar   a própria pessoa ou o gestor direto (o RH só consulta)
 * @param proprio         quem consulta é a própria pessoa
 */
public record PlanoDesenvolvimento(PessoaResumo colaborador, CicloResumo cicloBase, List<SugestaoMeta> sugestoes,
                                   Map<Long, String> competencias, List<MetaResumo> metas, boolean podeCriar,
                                   boolean podeAtualizar, boolean proprio) {

    public boolean somenteLeitura() {
        return !podeAtualizar;
    }

    public long abertas() {
        return metas.stream().filter(MetaResumo::aberta).count();
    }
}
