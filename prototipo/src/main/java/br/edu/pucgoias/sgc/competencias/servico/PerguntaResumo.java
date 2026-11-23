package br.edu.pucgoias.sgc.competencias.servico;

import java.util.Set;

/**
 * Pergunta do questionário. {@code tipo} é "ESCALA" (nota de 1 até a escala, com "não tenho como avaliar") ou
 * "ABERTA" (texto livre, sem nota). {@code avaliadores} diz a quem ela aparece (RF-11). {@code ordem} é a posição
 * dentro da competência.
 */
public record PerguntaResumo(Long id, Long competenciaId, String texto, String tipo, boolean obrigatoria,
                             Set<TipoAvaliador> avaliadores, int ordem) {

    public boolean escala() {
        return "ESCALA".equals(tipo);
    }

    public boolean aberta() {
        return "ABERTA".equals(tipo);
    }

    public boolean seAplicaA(TipoAvaliador tipoAvaliador) {
        return avaliadores.contains(tipoAvaliador);
    }
}
