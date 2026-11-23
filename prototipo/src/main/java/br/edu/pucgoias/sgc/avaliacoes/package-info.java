/**
 * Avaliações: UC-08 Registrar observações sobre a equipe (RF-17) — pronto; UC-03 Responder avaliação e UC-09 Reabrir
 * avaliação e moderar comentários — entram depois do pacote ciclos. A resposta enviada não guarda quem respondeu
 * (RNF-01, ver {@code V4_1__cria_avaliacao_resposta.sql}).
 *
 * <p><b>UC-08 (responsabilidade):</b> o gestor registra, ao longo do ano, observações datadas (ponto positivo ou de
 * melhoria) sobre as pessoas da sua equipe direta, vê a lista cronológica de cada uma e edita ou exclui as que ele
 * mesmo registrou. Telas em {@code /observacoes/**} (só GESTOR pelo filtro; a equipe é conferida no serviço, RNF-03).
 * O colaborador não vê as observações na tela, mas elas entram na cópia dos dados pessoais dele (LGPD). Registrar,
 * alterar e excluir ficam no log de auditoria (sem o texto da observação).
 *
 * <p><b>Camadas:</b> {@code web} (controllers e formulários), {@code servico} (regras e interface pública),
 * {@code dominio} (entidades JPA {@code Observacao} e enum {@code TipoObservacao}), {@code repositorio} (Spring Data).
 * Migrações: V4_x (tabelas {@code observacao}, {@code rascunho_resposta}, {@code avaliacao_enviada}, {@code resposta}).
 *
 * <p><b>Interface oferecida</b> ({@code servico}, só records):
 * <ul>
 *   <li>{@code ObservacaoService.observacoesDoPeriodo(Long gestorId, Long colaboradorId, LocalDate inicio,
 *       LocalDate fim)}: observações com data do fato no período (inclusive), da mais recente para a mais antiga,
 *       só para o gestor direto do colaborador (senão {@code AccessDeniedException}). Usada no UC-03, passo 4;</li>
 *   <li>record {@code ObservacaoResumo(id, colaboradorId, tipo, descricao, dataFato, criadaEm)}, com {@code tipo}
 *       "POSITIVO" ou "MELHORIA".</li>
 * </ul>
 *
 * <p><b>Implementa pontos de extensão de outros pacotes:</b> {@code privacidade.servico.FonteDadosPessoais}
 * ({@code FonteObservacoes}: "Observações registradas pelo gestor", com data, tipo e descrição) e
 * {@code identidade.servico.ContribuicaoPainel} ({@code PainelObservacoes}: cartão "Observações da equipe" do gestor).
 *
 * <p><b>Depende de:</b> identidade ({@code UsuarioService}: equipe direta e pessoas; {@code UsuarioAutenticado}),
 * privacidade ({@code FonteDadosPessoais}) e compartilhado ({@code EventoAuditoria}, exceções). Com o UC-03, passa a
 * depender também de ciclos e competencias (pelos serviços).
 *
 * <p><b>Dados de exemplo:</b> {@code servico.DadosExemploAvaliacoes} ({@code @Order(40)}).
 *
 * <p>Responsável: Gabriel Araújo Caetano (#58).
 */
package br.edu.pucgoias.sgc.avaliacoes;
