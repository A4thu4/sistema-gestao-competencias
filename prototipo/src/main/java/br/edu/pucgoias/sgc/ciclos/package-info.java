/**
 * UC-02 Criar ciclo de avaliação e UC-07 Acompanhar andamento do ciclo (RF-08 a RF-10, RF-12, RF-13, RF-24, RF-34,
 * RNF-02). Responsável: José Eduardo (#57).
 *
 * <p><b>Responsabilidade:</b> ciclos de avaliação (nome, tipos combinados, período e situação rascunho → agendado →
 * aberto → encerrado), quem é avaliado e com qual questionário publicado do cargo, quem avalia quem e em que relação
 * (designações, com sugestão pela estrutura das equipes e ajuste manual do RH), avaliadores externos com link
 * individual (token aleatório de 32 bytes), validação antes de agendar (pendências e alerta de menos de 3 pares ou
 * subordinados), abertura e encerramento automáticos por rotina agendada, convites e lembretes por e-mail e o painel
 * de andamento (RH: organização; gestor: equipe direta, conferido no servidor). Guarda só o andamento de cada
 * designação, nunca as respostas (RNF-01).
 *
 * <p><b>Camadas:</b> {@code web} ({@code CiclosController} só RH, {@code AndamentoController} RH e gestor),
 * {@code servico} (regras e interface pública), {@code dominio} (entidades {@code Ciclo}, {@code Designacao},
 * {@code AvaliadorExterno}), {@code repositorio} (Spring Data). Migrações: V3_1 (ciclo, ciclo_questionario,
 * avaliador_externo, designacao) e V3_2 (ciclo_avaliado: pessoas avaliadas e o cargo de cada uma no ciclo).
 *
 * <p><b>Interface oferecida aos outros pacotes</b> (records do {@code servico}):
 * <ul>
 *   <li>{@code CicloService}: {@code ciclo(id)}, {@code abertos()}, {@code encerrados()},
 *       {@code aceitaRespostas(cicloId)}, {@code designacoesDoAvaliador(usuarioId)}, {@code designacao(id)},
 *       {@code designacaoPorToken(token)}, {@code marcarEmPreenchimento(id)}, {@code marcarEnviada(id)},
 *       {@code reabrir(id)}, {@code designacoesDoCiclo(cicloId)}, {@code avaliadosDoCiclo(cicloId)},
 *       {@code questionarioDoAvaliado(cicloId, avaliadoId)}, {@code liberarResultado(cicloId)};</li>
 *   <li>records {@code CicloResumo} e {@code DesignacaoResumo} (situações como texto; relação como
 *       {@code TipoAvaliador});</li>
 *   <li>evento {@code CicloEncerradoEvento(cicloId)}, publicado de forma síncrona na transação do encerramento
 *       (o pacote resultados escuta para consolidar).</li>
 * </ul>
 *
 * <p><b>Implementa pontos de extensão de outros pacotes</b> (inversão de dependência):
 * {@code competencias.servico.ConsultaUsoQuestionario#ciclosQueUsam} ({@code UsoQuestionarioEmCiclos}),
 * {@code privacidade.servico.ParticipacaoEmCiclo} ({@code ParticipacaoEmCicloAberto}) e
 * {@code identidade.servico.ContribuicaoPainel} ({@code PainelCiclos}).
 *
 * <p><b>Depende de:</b> identidade ({@code UsuarioService}: pessoas, cargos, áreas, equipe direta e pares;
 * {@code UsuarioAutenticado}), competencias ({@code QuestionarioService}: {@code publicadosDoCargo},
 * {@code temPerguntasPara}, {@code resumo}; enum {@code TipoAvaliador}), privacidade (só a interface
 * {@code ParticipacaoEmCiclo}) e compartilhado ({@code EmailService}, {@code EventoAuditoria}, exceções).
 */
package br.edu.pucgoias.sgc.ciclos;
