package br.edu.pucgoias.sgc.ciclos.servico;

public record CicloNaLista(CicloResumo ciclo, int avaliados, int designacoes, int enviadas, int externos) {

    public String externosTexto() {
        return externos == 1 ? "+ 1 avaliador externo" : "+ " + externos + " avaliadores externos";
    }

    public int percentualRespondido() {
        return designacoes == 0 ? 0 : (int) Math.round(100.0 * enviadas / designacoes);
    }
}
