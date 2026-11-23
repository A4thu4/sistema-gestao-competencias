package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.Collection;
import java.util.List;

/** Grupos de avaliadores na ordem das telas de resultado (TL-04: autoavaliação antes do chefe) e seus rótulos. */
public final class Grupos {

    public static final List<TipoAvaliador> ORDEM = List.of(TipoAvaliador.AUTO, TipoAvaliador.CHEFE,
            TipoAvaliador.PAR, TipoAvaliador.SUBORDINADO, TipoAvaliador.EXTERNO);

    private Grupos() {
    }

    public static String rotulo(TipoAvaliador grupo) {
        return switch (grupo) {
            case AUTO -> "Autoavaliação";
            case CHEFE -> "Chefe";
            case PAR -> "Pares";
            case SUBORDINADO -> "Subordinados";
            case EXTERNO -> "Externos";
        };
    }

    public static List<TipoAvaliador> emOrdem(Collection<TipoAvaliador> grupos) {
        return ORDEM.stream().filter(grupos::contains).toList();
    }
}
