package br.edu.pucgoias.sgc.competencias.servico;

import java.util.Set;

/**
 * Pergunta informada no editor. {@code id} nulo = pergunta nova. {@code tipo}: "ESCALA" ou "ABERTA".
 * Perguntas com texto em branco são ignoradas.
 */
public record DadosPergunta(Long id, Long competenciaId, String texto, String tipo, boolean obrigatoria,
                            Set<TipoAvaliador> avaliadores) {
}
