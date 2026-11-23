package br.edu.pucgoias.sgc.ciclos.servico;

/** Linha da lista de ciclos do RH (UC-02, passo 2). */
public record CicloNaLista(CicloResumo ciclo, int avaliados, int designacoes, int enviadas) {

    public int percentualRespondido() {
        return designacoes == 0 ? 0 : (int) Math.round(100.0 * enviadas / designacoes);
    }
}
