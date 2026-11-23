/**
 * Avaliações: UC-03 Responder avaliação, UC-08 Registrar observações sobre a equipe e UC-09 Reabrir avaliação e
 * moderar comentários (RF-06, RF-11, RF-14 a RF-18, RF-22, RF-25, RF-34, RNF-01, RNF-03, RNF-05, RNF-08).
 * Responsável: Gabriel Araújo Caetano (#58).
 *
 * <p><b>Anonimato (RNF-01, DA-06; ver {@code V4_1__cria_avaliacao_resposta.sql}).</b> Enquanto o avaliador preenche,
 * as respostas ficam em {@code rascunho_resposta}, ligadas à designação (salvamento automático, RF-14). No envio
 * ({@code RespostaService.enviarAvaliacao}, contrato CO-02) tudo vira, numa transação, uma {@code avaliacao_enviada}
 * com id UUID aleatório e só ciclo, avaliado, relação e questionário — sem designação, avaliador nem data — e as
 * {@code resposta} dela; o rascunho é apagado e a designação é marcada como enviada no pacote ciclos. O
 * {@code verificador} (HMAC-SHA256 do id da designação com {@code sgc.chave-verificador}) só serve para o RH reabrir
 * a avaliação (UC-09) e nunca sai do pacote: as telas usam o id da designação e o id da resposta.
 *
 * <p><b>Responsabilidades:</b>
 * <ul>
 *   <li>UC-03: {@code /avaliacoes} (lista por ciclo e formulário do avaliador interno; o serviço confere que a
 *       designação é do usuário) e {@code /externo/{token}} (avaliador externo, sem login). Perguntas só da relação
 *       (RF-11), descrição dos níveis (RF-25), "não tenho como avaliar" (RF-06), salvamento automático por
 *       {@code fetch} e "Salvar rascunho" sem JavaScript, obrigatórias pendentes (RF-15), envio com confirmação,
 *       bloqueio depois do envio (RF-16) e do encerramento (RF-09). Para o gestor do avaliado (relação CHEFE): as
 *       observações do período (RF-17) e o resultado do ciclo anterior (RF-18);</li>
 *   <li>UC-08: {@code /observacoes} (só GESTOR; equipe direta conferida no serviço). Só o autor edita ou exclui, e só
 *       enquanto a observação não entrou numa avaliação de ciclo encerrado;</li>
 *   <li>UC-09: {@code /moderacao} (só RH): reabrir avaliação enviada de ciclo aberto, com motivo, auditoria e e-mail ao
 *       avaliador; moderar comentários (sem autor e sem relação), ocultando com motivo ou voltando a exibir (RF-22).</li>
 * </ul>
 *
 * <p><b>Camadas:</b> {@code web} (controllers e formulários), {@code servico} (regras e interface pública),
 * {@code dominio} (entidades {@code Observacao}, {@code RascunhoResposta}, {@code AvaliacaoEnviada},
 * {@code Resposta}; enum {@code TipoObservacao}), {@code repositorio} (Spring Data). Migrações: V4_x (só a V4_1).
 *
 * <p><b>Interface oferecida aos outros pacotes</b> ({@code servico}, só records e enums):
 * <ul>
 *   <li>{@code ConsultaRespostasService} (para resultados):
 *       {@code List<RespostaConsolidavel> respostasDoCiclo(Long cicloId)} (só perguntas de escala),
 *       {@code List<ComentarioResumo> comentariosVisiveis(Long cicloId, Long avaliadoId)} (sem os ocultos) e
 *       {@code int avaliacoesEnviadas(Long cicloId, Long avaliadoId, TipoAvaliador relacao)};</li>
 *   <li>records {@code RespostaConsolidavel(UUID avaliacaoId, Long avaliadoId, TipoAvaliador relacao, Long perguntaId,
 *       Long competenciaId, Integer nota, boolean naoAvalia)} e {@code ComentarioResumo(String texto,
 *       Long competenciaId, String competenciaNome, Long perguntaId, String perguntaTexto)};</li>
 *   <li>{@code ObservacaoService.observacoesDoPeriodo(Long gestorId, Long colaboradorId, LocalDate inicio,
 *       LocalDate fim)} e record {@code ObservacaoResumo}.</li>
 * </ul>
 *
 * <p><b>Inversão de dependência declarada aqui:</b> {@code servico.ResultadoAnterior}
 * ({@code Optional<ResumoResultadoAnterior> doAvaliado(Long avaliadoId, Long cicloAtualId)}), a ser implementada
 * pelo pacote resultados; sem implementação, a tela mostra "sem resultado anterior" (UC-03, 4b).
 *
 * <p><b>Implementa pontos de extensão de outros pacotes:</b> {@code competencias.servico.ConsultaUsoQuestionario
 * #possuiRespostas} ({@code UsoQuestionarioEmRespostas}), {@code privacidade.servico.FonteDadosPessoais}
 * ({@code FonteObservacoes}) e {@code identidade.servico.ContribuicaoPainel} ({@code PainelAvaliacoes}: "Avaliações
 * pendentes"; {@code PainelObservacoes}: "Observações da equipe").
 *
 * <p><b>Depende de:</b> ciclos ({@code CicloService}: designações, situação e prazo do ciclo, marcar em
 * preenchimento, enviada e reaberta), competencias ({@code QuestionarioService.detalharPara}, {@code TipoAvaliador}),
 * identidade ({@code UsuarioService}, {@code UsuarioAutenticado}), privacidade (só a interface
 * {@code FonteDadosPessoais}) e compartilhado ({@code EmailService}, {@code EventoAuditoria}, exceções).
 *
 * <p><b>Dados de exemplo:</b> {@code servico.DadosExemploAvaliacoes} ({@code @Order(40)}): observações e as
 * respostas dos ciclos de exemplo (com um comentário grosseiro sobre o Bruno no Ciclo 2026.1 para a moderação).
 */
package br.edu.pucgoias.sgc.avaliacoes;
