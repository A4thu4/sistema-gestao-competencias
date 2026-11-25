package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.util.List;

/**
 * Resultado do ciclo anterior mostrado ao gestor durante a avaliação (RF-18). {@code mediaGeral} e as médias das
 * competências vão de 1 até {@code escalaMax}; {@code null} quando não há nota. Os textos vêm formatados por quem
 * calcula o resultado, com o mesmo arredondamento da tela de resultado.
 */
public record ResumoResultadoAnterior(Long cicloId, String cicloNome, int escalaMax, Double mediaGeral,
                                      String mediaGeralTexto, List<MediaCompetencia> competencias) {

    public record MediaCompetencia(Long competenciaId, String nome, Double media, String mediaTexto) {
    }
}
