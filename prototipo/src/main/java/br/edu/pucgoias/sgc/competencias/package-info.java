/**
 * UC-01 Gerenciar competências e questionários (RF-01 a RF-07, RNF-13). Responsável: José Eduardo (#56).
 *
 * <p><b>Responsabilidade:</b> catálogo de competências técnicas e comportamentais (desativadas, nunca apagadas),
 * conjunto padrão de competências de cada cargo, questionários por cargo com perguntas de escala e abertas, a quais
 * avaliadores cada pergunta se aplica, escala de 1 a 5 ou 1 a 10 com a descrição de cada nível, modelos prontos
 * (chefe–colaborador, pares, 360º), cópia de questionário anterior, validação antes de publicar, pré-visualização e
 * bloqueio de questionário já usado. Só o perfil RH acessa as telas ({@code /competencias/**}).
 *
 * <p><b>Camadas:</b> {@code web} (controllers e formulários), {@code servico} (regras e interface pública),
 * {@code dominio} (entidades JPA), {@code repositorio} (Spring Data). Migrações: V2_x.
 *
 * <p><b>Interface oferecida aos outros pacotes</b> (só records e enums do {@code servico}):
 * <ul>
 *   <li>{@code QuestionarioService}: {@code publicadosDoCargo(cargoId)}, {@code publicados()}, {@code resumo(id)},
 *       {@code detalhar(id)}, {@code detalharPara(id, TipoAvaliador)}, {@code temPerguntasPara(id, TipoAvaliador)};</li>
 *   <li>{@code CompetenciaService}: {@code competenciasAtivas()}, {@code competenciasPorIds(ids)};</li>
 *   <li>records {@code QuestionarioResumo}, {@code QuestionarioCompleto}, {@code CompetenciaDoQuestionario},
 *       {@code PerguntaResumo}, {@code NivelResumo}, {@code CompetenciaResumo}; enum {@code TipoAvaliador}.</li>
 * </ul>
 *
 * <p><b>Inversão de dependência:</b> declara {@code servico.ConsultaUsoQuestionario} ({@code possuiRespostas},
 * {@code ciclosQueUsam}), a ser implementada pelos pacotes ciclos e avaliacoes, que dependem deste. Sem
 * implementação, vale "sem respostas e sem ciclos".
 *
 * <p><b>Depende de:</b> identidade ({@code UsuarioService}: cargos; {@code ContribuicaoPainel}: cartões da página
 * inicial) e compartilhado (exceções e {@code EventoAuditoria}).
 */
package br.edu.pucgoias.sgc.competencias;
