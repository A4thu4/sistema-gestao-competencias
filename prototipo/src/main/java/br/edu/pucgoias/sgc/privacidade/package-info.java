/**
 * UC-05 Gerenciar consentimento e dados pessoais (LGPD): RF-29, RF-30, RNF-01, RNF-03, RNF-06 e RNF-09.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>barreira do termo ({@code web.BarreiraDoTermo}): quem não aceitou a versão vigente vai para
 *       {@code /privacidade/termo} antes de qualquer tela (é assim que o UC-10 apresenta o termo no primeiro acesso);</li>
 *   <li>aceite e recusa do termo, com versão, data e hora; publicação de nova versão pelo RH, com resumo das
 *       mudanças e novo aceite de todos (extensão 2a);</li>
 *   <li>área "Meus dados": termo aceito, dados pessoais mantidos, cópia na tela e em PDF (7a) e solicitações de
 *       acesso, correção e exclusão com protocolo LGPD-AAAA-NNNNNN e e-mail de confirmação;</li>
 *   <li>atendimento das solicitações pelo RH (10a e 10b): correção de nome e anonimização pelo
 *       {@code UsuarioService}, aviso por e-mail e registro no log de auditoria.</li>
 * </ul>
 *
 * <p>Interface oferecida aos outros pacotes ({@code servico}):
 * <ul>
 *   <li>{@code ConsentimentoService}: {@code termoVigente()}, {@code idTermoVigente()},
 *       {@code aceitouVigente(Long usuarioId)}, {@code ultimoAceite(Long usuarioId)} (devolve {@code AceiteResumo});</li>
 *   <li>{@code SolicitacaoService.emAnalise()} (quantidade de pedidos esperando o RH).</li>
 * </ul>
 *
 * <p>Pontos de extensão (inversão de dependência) que outros pacotes implementam com {@code @Component}:
 * <ul>
 *   <li>{@code servico.FonteDadosPessoais}: dados que o pacote guarda sobre a pessoa, para a cópia dos dados
 *       (avaliacoes: observações do gestor; resultados: resultados consolidados e metas do plano). Nunca revelar a
 *       autoria de respostas de pares e subordinados (RNF-01);</li>
 *   <li>{@code servico.ParticipacaoEmCiclo}: se a pessoa participa de um ciclo aberto (ciclos), para o aviso da
 *       extensão 7c. Sem implementação, o aviso não aparece.</li>
 * </ul>
 *
 * <p>Depende de {@code identidade} ({@code UsuarioService}, {@code UsuarioAutenticado}, {@code ContribuicaoPainel})
 * e de {@code compartilhado} ({@code EmailService}, {@code EventoAuditoria}, exceções). Nenhum pacote de negócio
 * além desses. Migrações: V6_x (tabelas {@code termo_consentimento}, {@code aceite} e {@code solicitacao_titular}).
 *
 * <p>Responsável: Arthur Mamedes Borges (#60).
 */
package br.edu.pucgoias.sgc.privacidade;
