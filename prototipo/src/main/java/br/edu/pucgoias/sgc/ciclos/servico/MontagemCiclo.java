package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.List;
import java.util.Map;

/** Tudo o que as telas de montagem e o resumo do ciclo mostram (UC-02, passos 6 a 11). */
public record MontagemCiclo(CicloResumo ciclo, List<TipoAvaliador> tipos, List<AvaliadoNoCiclo> avaliados,
                            List<QuestionarioDoCargo> questionarios, Verificacao verificacao,
                            Map<TipoAvaliador, Integer> totalPorTipo, int totalDesignacoes, int externos,
                            int convitesPendentes) {

    public int totalDo(TipoAvaliador tipo) {
        return totalPorTipo.getOrDefault(tipo, 0);
    }
}
