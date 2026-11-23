package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;

/**
 * Ciclo na lista da área de resultados (UC-04, passo 2), do ponto de vista de quem consulta.
 *
 * @param consolidado há resultado calculado para o ciclo
 * @param pessoas quantas pessoas com resultado o usuário pode abrir neste ciclo
 * @param meuResultado o próprio usuário tem resultado neste ciclo
 * @param meuLiberado o próprio resultado já pode ser aberto por ele (liberado pelo RH, ou RH)
 * @param podeLiberar RH, ciclo consolidado e ainda não liberado (2a)
 */
public record CicloDisponivel(CicloResumo ciclo, boolean consolidado, int pessoas, boolean meuResultado,
                              boolean meuLiberado, boolean podeLiberar) {

    public String dataEncerramento() {
        return Formatos.data(ciclo.dataEncerramento());
    }

    /** Texto da situação para quem consulta. */
    public String situacaoTexto() {
        if (!ciclo.encerrado()) {
            return "Em andamento: o resultado sai depois do encerramento";
        }
        if (!consolidado) {
            return "Encerrado, sem resultado calculado";
        }
        return ciclo.resultadoLiberado() ? "Liberado aos colaboradores" : "Aguardando liberação pelo RH";
    }

    public String seloSituacao() {
        if (!ciclo.encerrado()) {
            return "selo-aberto";
        }
        if (!consolidado) {
            return "selo-rascunho";
        }
        return ciclo.resultadoLiberado() ? "selo-concluida" : "selo-em_analise";
    }
}
