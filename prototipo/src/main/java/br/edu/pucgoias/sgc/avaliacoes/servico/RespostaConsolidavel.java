package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.UUID;

/**
 * Uma resposta de escala de uma avaliação enviada, para o pacote resultados consolidar (UC-04). Não identifica quem
 * respondeu (RNF-01): {@code avaliacaoId} é o UUID aleatório da avaliação enviada e serve só para contar quantas
 * avaliações há em cada grupo. {@code nota} é {@code null} quando {@code naoAvalia} ("não tenho como avaliar", RF-06).
 */
public record RespostaConsolidavel(UUID avaliacaoId, Long avaliadoId, TipoAvaliador relacao, Long perguntaId,
                                   Long competenciaId, Integer nota, boolean naoAvalia) {
}
