package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

import java.util.List;

/**
 * Painel de andamento do ciclo (UC-07, RF-13): contagem por situação e lista por avaliador. {@code daEquipe} indica
 * a visão do gestor, limitada à equipe direta; {@code pessoas} são as que podem ser usadas no filtro.
 */
public record AndamentoCiclo(CicloResumo ciclo, int total, int naoIniciadas, int emPreenchimento, int enviadas,
                             List<AvaliadorNoAndamento> avaliadores, boolean daEquipe, List<PessoaResumo> pessoas,
                             PessoaResumo filtro, boolean aceitaLembrete) {

    public int percentualRespondido() {
        return total == 0 ? 0 : (int) Math.round(100.0 * enviadas / total);
    }

    public int pendentes() {
        return total - enviadas;
    }

    public long avaliadoresPendentes() {
        return avaliadores.stream().filter(a -> !a.concluiu()).count();
    }
}
