package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

import java.time.LocalDate;

/**
 * Pessoa da equipe direta do gestor com o resumo das observações: quantas, quantos pontos positivos e de melhoria,
 * a data do fato mais recente e se está sem observação há mais de {@link ObservacaoService#DIAS_SEM_OBSERVACAO} dias.
 */
public record MembroEquipe(PessoaResumo pessoa, int total, int positivos, int melhorias, LocalDate ultima,
                           boolean semObservacaoRecente) {

    public String ultimaTexto() {
        return Calendario.data(ultima);
    }
}
