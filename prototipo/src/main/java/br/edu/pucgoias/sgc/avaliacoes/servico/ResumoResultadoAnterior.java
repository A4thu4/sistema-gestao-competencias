package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.util.List;

/**
 * Resultado do ciclo anterior mostrado ao gestor durante a avaliação (RF-18). {@code mediaGeral} e as médias das
 * competências vão de 1 até {@code escalaMax}; {@code null} quando não há nota.
 */
public record ResumoResultadoAnterior(Long cicloId, String cicloNome, int escalaMax, Double mediaGeral,
                                      List<MediaCompetencia> competencias) {

    /** Média de uma competência no ciclo anterior. */
    public record MediaCompetencia(Long competenciaId, String nome, Double media) {
    }
}
