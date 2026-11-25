package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.time.LocalDate;

/**
 * Designação para os outros pacotes: quem ({@code avaliadorId} ou o externo) avalia quem ({@code avaliadoId}), em
 * que relação, e o andamento. {@code situacao} é "NAO_INICIADA", "EM_PREENCHIMENTO" ou "ENVIADA";
 * {@code cicloSituacao} é a situação do ciclo. {@code questionarioId} é o questionário do cargo do avaliado no ciclo.
 * Para externos, {@code avaliadorId} é {@code null} e vêm {@code avaliadorExternoId}, {@code externoNome} e
 * {@code externoEmail}.
 */
public record DesignacaoResumo(Long id, Long cicloId, String cicloNome, String cicloSituacao,
                               LocalDate dataEncerramento, Long avaliadoId, Long avaliadorId, Long avaliadorExternoId,
                               String externoNome, String externoEmail, TipoAvaliador relacao, String situacao,
                               Long questionarioId) {

    public boolean externa() {
        return avaliadorExternoId != null;
    }

    public boolean enviada() {
        return "ENVIADA".equals(situacao);
    }

    public boolean emPreenchimento() {
        return "EM_PREENCHIMENTO".equals(situacao);
    }

    public boolean naoIniciada() {
        return "NAO_INICIADA".equals(situacao);
    }

    public boolean cicloAberto() {
        return "ABERTO".equals(cicloSituacao);
    }

    public boolean cicloEncerrado() {
        return "ENCERRADO".equals(cicloSituacao);
    }

    public String situacaoRotulo() {
        return switch (situacao) {
            case "ENVIADA" -> "Enviada";
            case "EM_PREENCHIMENTO" -> "Em preenchimento";
            default -> "Não iniciada";
        };
    }
}
