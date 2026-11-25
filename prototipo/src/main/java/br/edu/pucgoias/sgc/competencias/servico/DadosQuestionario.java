package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/**
 * Tudo o que o RH informa no editor do questionário.
 *
 * @param competenciaIds competências escolhidas, na ordem em que vão aparecer
 * @param niveis         descrição de cada nível; a posição 0 é o nível 1
 * @param padraoDoCargo  usar a seleção como conjunto padrão do cargo
 */
public record DadosQuestionario(String nome, Long cargoId, int escalaMax, List<Long> competenciaIds,
                                List<DadosPergunta> perguntas, List<String> niveis, boolean padraoDoCargo) {
}
