package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/**
 * Tudo o que o RH informa no editor do questionário (UC-01, passos 7 a 11).
 *
 * @param competenciaIds competências escolhidas, na ordem em que vão aparecer
 * @param niveis         descrição de cada nível; a posição 0 é o nível 1
 * @param padraoDoCargo  usar a seleção como conjunto padrão do cargo (extensão 9c)
 */
public record DadosQuestionario(String nome, Long cargoId, int escalaMax, List<Long> competenciaIds,
                                List<DadosPergunta> perguntas, List<String> niveis, boolean padraoDoCargo) {
}
