package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

/** Textos das relações do ponto de vista de quem avalia, e quais são anônimas (RNF-01). */
public final class Relacoes {

    private Relacoes() {
    }

    public static String texto(TipoAvaliador relacao) {
        return switch (relacao) {
            case CHEFE -> "Você avalia como gestor";
            case AUTO -> "Autoavaliação";
            case PAR -> "Você avalia como colega (par)";
            case SUBORDINADO -> "Você avalia o seu gestor";
            case EXTERNO -> "Avaliação externa";
        };
    }

    /** Pares, subordinados e externos respondem de forma anônima; chefe e autoavaliação são identificados. */
    public static boolean anonima(TipoAvaliador relacao) {
        return relacao == TipoAvaliador.PAR || relacao == TipoAvaliador.SUBORDINADO
                || relacao == TipoAvaliador.EXTERNO;
    }
}
