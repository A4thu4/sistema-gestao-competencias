/**
 * UC-04 Consultar resultados e UC-06 Gerenciar plano de desenvolvimento (RF-19 a RF-24, RF-26 a RF-28, RNF-01,
 * RNF-02, RNF-03, RNF-05, RNF-12). Responsável: Lorenzo Oliveira Maciel (#59).
 *
 * <p><b>Responsabilidade:</b>
 * <ul>
 *   <li><b>Consolidação (RF-19, CO-03):</b> {@code servico.ConsolidacaoService.consolidarResultados(cicloId)} lê as
 *       respostas de escala do ciclo, calcula com {@code servico.CalculoConsolidacao} (lógica pura: média por pessoa ×
 *       competência × grupo, sem "não tenho como avaliar", 2 casas HALF_UP, número de avaliações por grupo) e
 *       substitui as linhas de {@code resultado_consolidado} do ciclo. O mesmo conjunto de respostas gera sempre o
 *       mesmo resultado (RNF-12). Roda no encerramento ({@code @EventListener} de {@code CicloEncerradoEvento}, na
 *       mesma transação) e pelo botão "Recalcular" do RH;</li>
 *   <li><b>Consulta (UC-04):</b> lista de ciclos por perfil, liberação pelo RH com e-mail a cada avaliado (2a), painel
 *       individual da TL-04 (média geral, ponto forte, foco, radar, tabela por grupo, nota final, comentários sem
 *       autor e evolução entre ciclos, normalizada em % quando as escalas diferem), relatório por equipe ou área (3a)
 *       e exportação em PDF (OpenPDF) e Excel (Apache POI) com os mesmos dados da tela;</li>
 *   <li><b>Regras de exibição</b> ({@code servico.RegrasDeExibicao}): média de pares, subordinados e externos só com
 *       pelo menos 3 respostas (RNF-02), em tela, gráfico, relatório, exportação e cópia de dados; nota final = média
 *       dos grupos exibíveis sem a autoavaliação; média geral = média das notas finais;</li>
 *   <li><b>Visibilidade (RNF-03), no servidor:</b> a pessoa vê o próprio resultado só depois da liberação (2b); o
 *       gestor vê a equipe direta desde o encerramento; o RH vê todos. Fora disso, 403 sem dados (*a). Ciclo não
 *       encerrado: mensagem 2c;</li>
 *   <li><b>Auditoria (RNF-05):</b> abertura de painel e de relatório, liberação, exportação, metas criadas e
 *       lembretes;</li>
 *   <li><b>Plano de desenvolvimento (UC-06):</b> o gestor direto cria metas (competência do último resultado liberado,
 *       com as de menor nota sugeridas; descrição, ações, prazo, situação); a pessoa e o gestor registram progresso e
 *       situação ({@code meta_atualizacao}, linha do tempo); o RH só consulta. Lembrete trimestral por rotina diária
 *       ({@code servico.RotinaLembreteMetas}): meta aberta sem atualização há 90 dias gera e-mail à pessoa e ao
 *       gestor, sem repetir em menos de 90 dias; o gestor também pode mandar o lembrete na hora.</li>
 * </ul>
 *
 * <p><b>Camadas:</b> {@code web} ({@code ResultadosController} em {@code /resultados}, {@code PlanosController} em
 * {@code /planos}), {@code servico} (regras, cálculo e exportação), {@code dominio} (entidades
 * {@code ResultadoConsolidado}, {@code MetaDesenvolvimento}, {@code MetaAtualizacao} e enum {@code SituacaoMeta}),
 * {@code repositorio} (Spring Data). Migração: V5_1 ({@code resultado_consolidado}, {@code meta_desenvolvimento},
 * {@code meta_atualizacao}).
 *
 * <p><b>Interface oferecida aos outros pacotes</b> ({@code servico}):
 * <ul>
 *   <li>{@code ConsolidacaoService.consolidarResultados(Long cicloId)}: recalcula o consolidado do ciclo (CO-03);
 *       {@code recalcular(UsuarioAutenticado, Long cicloId)}: o mesmo, só RH e auditado;</li>
 *   <li>{@code ResultadoService.ultimoLiberado(Long pessoaId)}: {@code Optional<CicloResumo>} do último ciclo
 *       liberado com resultado da pessoa.</li>
 * </ul>
 *
 * <p><b>Implementa pontos de extensão de outros pacotes</b> (inversão de dependência):
 * {@code avaliacoes.servico.ResultadoAnterior} ({@code ResultadoAnteriorDoAvaliado}: nota final por competência e
 * média geral do último ciclo encerrado antes do atual, com o mínimo de 3; RF-18),
 * {@code identidade.servico.ContribuicaoPainel} ({@code PainelResultados}: resultado liberado, metas do próprio plano,
 * metas da equipe em andamento, ciclos para liberar) e {@code privacidade.servico.FonteDadosPessoais}
 * ({@code FonteResultados}: notas finais e médias exibíveis dos ciclos liberados; {@code FontePlano}: metas e
 * atualizações).
 *
 * <p><b>Depende de:</b> identidade ({@code UsuarioService}, {@code UsuarioAutenticado}, {@code ContribuicaoPainel}),
 * ciclos ({@code CicloService}: {@code ciclo}, {@code abertos}, {@code encerrados}, {@code avaliadosDoCiclo},
 * {@code questionarioDoAvaliado}, {@code liberarResultado}; evento {@code CicloEncerradoEvento}), avaliacoes
 * ({@code ConsultaRespostasService.respostasDoCiclo}, {@code comentariosVisiveis}, {@code avaliacoesEnviadas};
 * interface {@code ResultadoAnterior}), competencias ({@code CompetenciaService.competenciasPorIds},
 * {@code QuestionarioService.resumo}, enum {@code TipoAvaliador}), privacidade (interface {@code FonteDadosPessoais})
 * e compartilhado ({@code EmailService}, {@code EventoAuditoria}, exceções).
 *
 * <p><b>Dados de exemplo:</b> {@code servico.DadosExemploResultados} ({@code @Order(50)}): consolida e libera o
 * "Ciclo 2026.1" (com os avisos por e-mail) e cria os planos da Carla (uma meta em andamento com duas atualizações e
 * uma não iniciada) e do Diego. O "Ciclo 2026.2" fica aberto, para ser encerrado ao vivo.
 */
package br.edu.pucgoias.sgc.resultados;
