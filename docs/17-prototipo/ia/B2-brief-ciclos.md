# Prompt B2-brief-ciclos (texto completo)

Ferramenta: Anthropic, Claude (Claude Code). Data: 23/11/2026. Texto entregue a cada sessão de desenvolvimento; endereços internos omitidos.

```text
# Pacote `ciclos` — UC-02 Criar ciclo de avaliação e UC-07 Acompanhar andamento do ciclo

Casos de uso: `git show origin/docs/parte-3-realinhamento:docs/11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md` e
`UC-07-acompanhar-andamento-ciclo.md` (só o resumo: RF-12, RF-13). Telas: `TL-02-criar-ciclo.png` (TL-07 não existe,
desenhe no mesmo estilo). Tabelas: `V3_1__cria_ciclo_designacao.sql`. Requisitos: RF-08, RF-09, RF-10, RF-12, RF-13,
RF-34, RNF-02.

Use a API pública do pacote `competencias` (leia `competencias/servico` e o `package-info.java` dele): `TipoAvaliador`,
`publicadosDoCargo`, `temPerguntasPara`, `resumo` etc. Use `UsuarioService` para pessoas, cargos, áreas, equipe direta
e pares.

## UC-02 (só RH)

- `/ciclos`: lista (nome, período, situação, quantidade de avaliados) e "Novo ciclo".
- Criação/edição em etapas ou em uma página com seções:
  1. nome, tipos (chefe–colaborador, autoavaliação, pares, subordinados avaliando o líder), abertura e encerramento.
     Validação 6a: nome, ao menos um tipo, abertura não anterior a hoje, encerramento depois da abertura.
  2. avaliados por cargo, por área ou individualmente; para cada cargo, o questionário publicado. 8a: cargo sem
     questionário publicado. 8b: questionário sem perguntas para um tipo escolhido.
  3. avaliadores sugeridos (chefe = gestor direto; subordinados = equipe direta; pares = mesmo gestor; autoavaliação =
     a própria pessoa) e ajuste manual por avaliado e tipo (incluir/remover). 9a: pessoa sem chefe/subordinados fica
     marcada como pendente. 9b: avaliador externo (nome e e-mail) por avaliado, com token aleatório seguro (32+ bytes,
     URL-safe) gravado na designação.
  4. validação 10/10b (avaliado sem avaliador em um tipo escolhido — exceto quando o tipo não se aplica: subordinados
     só valem para quem tem equipe; ninguém avalia a si mesmo fora da autoavaliação; e-mail válido) e alerta 10a
     (menos de 3 pares ou subordinados: resultado desse grupo não aparece, RNF-02 — só alerta).
  5. resumo e "Agendar ciclo" / "Salvar rascunho". 13a: abertura hoje abre na hora.
- 2a: ciclo em rascunho/agendado pode ser editado. 2b: ciclo aberto só permite mudar o encerramento e incluir
  avaliadores (que recebem o convite na hora). 2c: encerrado só consulta.
- **Abertura (passo 14):** rotina `@Scheduled` (ex.: a cada 5 min e ao subir a aplicação) passa AGENDADO → ABERTO quando
  chega a data e envia os convites pelo `EmailService` (interno: link para `/avaliacoes`; externo: link
  `${sgc.url-base}/externo/{token}`), gravando `convite_enviado_em`. 14a: falha de envio fica registrada e o RH vê os
  pendentes para reenviar.
- **Encerramento (RF-09):** a mesma rotina passa ABERTO → ENCERRADO quando passa a data de encerramento. Para a
  demonstração, botões do RH "Abrir agora" (agendado) e "Encerrar agora" (aberto), com confirmação e auditoria — não
  mudam as datas (há um `check` no banco), só a situação e `aberto_em`/`encerrado_em`.
- Ao encerrar, publique o evento `CicloEncerradoEvento(Long cicloId)` (record em `ciclos.servico`) com
  `ApplicationEventPublisher`, de forma síncrona, na mesma transação. O pacote `resultados` vai escutar e consolidar.
- Auditoria: criação, agendamento, abertura, encerramento, alterações em ciclo aberto.

## UC-07 Acompanhar andamento (`/ciclos/andamento`, RH e gestor)

- Ciclo aberto (seletor quando houver mais de um; também mostra encerrados para consulta). Percentual respondido,
  contagem por situação (não iniciada, em preenchimento, enviada), lista por **avaliador** com as avaliações pendentes
  e enviadas (RF-13). RH vê a organização inteira; gestor vê só as designações em que o avaliado ou o avaliador é da
  sua equipe direta (conferido no servidor). Mostrar quem já respondeu não fere o anonimato, porque a resposta não
  aponta para a designação; mas **não** mostre nada das respostas.
- Botão "Enviar lembrete aos pendentes" (RH e gestor, este só para a própria equipe) e rotina automática de lembrete
  (ex.: 3 dias antes do encerramento e no último dia), gravando `ultimo_lembrete_em` e sem repetir no mesmo dia
  (RF-12). Auditoria do lembrete manual.

## API pública que `avaliacoes` e `resultados` vão usar (records no `servico`)

- `CicloResumo(id, nome, situacao, dataAbertura, dataEncerramento, resultadoLiberado, tiposAtivos: Set<TipoAvaliador>)`
- `DesignacaoResumo(id, cicloId, cicloNome, cicloSituacao, dataEncerramento, avaliadoId, avaliadorId, avaliadorExternoId,
  externoNome, externoEmail, relacao: TipoAvaliador, situacao, questionarioId)` — o `questionarioId` é o do cargo do avaliado no ciclo.
- `CicloService` (ou `DesignacaoService`):
  - `CicloResumo ciclo(Long id)`; `List<CicloResumo> encerrados()`; `List<CicloResumo> abertos()`;
  - `boolean aceitaRespostas(Long cicloId)` (aberto e hoje ≤ encerramento);
  - `List<DesignacaoResumo> designacoesDoAvaliador(Long usuarioId)` (ciclos abertos e encerrados);
  - `DesignacaoResumo designacao(Long id)`; `Optional<DesignacaoResumo> designacaoPorToken(String token)`;
  - `void marcarEmPreenchimento(Long designacaoId)`; `void marcarEnviada(Long designacaoId)`;
    `void reabrir(Long designacaoId)` (ENVIADA → EM_PREENCHIMENTO; quem chama é o `avaliacoes` no UC-09);
  - `List<DesignacaoResumo> designacoesDoCiclo(Long cicloId)`; `List<Long> avaliadosDoCiclo(Long cicloId)`;
  - `Long questionarioDoAvaliado(Long cicloId, Long avaliadoId)`;
  - `void liberarResultado(Long cicloId)` (RF-24; quem chama é o `resultados`, que audita e avisa por e-mail).
- Implemente, como `@Component`, `ConsultaUsoQuestionario.ciclosQueUsam` (interface de `competencias.servico`; os dois
  métodos têm padrão, implemente só `ciclosQueUsam` — nomes dos ciclos em `ciclo_questionario`, de qualquer situação; o
  `avaliacoes` implementa `possuiRespostas` em outra classe) e `ParticipacaoEmCiclo.participaDeCicloAberto` (interface
  de `privacidade.servico`: a pessoa é avaliada ou avaliadora em ciclo ABERTO).
- Na API de `competencias`, tipos e situações vêm como String nos records; `TipoAvaliador` fica em
  `competencias.servico`.

## Dados de exemplo (`DadosExemploCiclos`, `@Order(30)`)

Grave direto pelos serviços/repositórios do próprio pacote (pode usar um método interno que pule a validação de data,
porque os ciclos de exemplo estão no passado):

1. **"Ciclo 2026.1"**, ENCERRADO (abertura 01/05/2026, encerramento 31/05/2026), tipos chefe, auto, pares e
   subordinados, avaliados: equipe do Bruno (Carla, Diego, Elisa, Fábio), o próprio Bruno, e a equipe do Gustavo
   (Igor, Júlia, Kátia) com o Gustavo. Designações completas (chefe, auto, pares entre colegas, subordinados avaliando
   Bruno e Gustavo, Helena como chefe dos dois coordenadores), todas ENVIADA. As respostas serão criadas depois pelo
   `avaliacoes`. Não publique o evento de encerramento no seed.
2. **"Ciclo 2026.2"**, ABERTO (abertura = hoje − 10 dias, encerramento = hoje + 12 dias), mesmos tipos e mesmas pessoas,
   mais 1 avaliador externo ("Marcos Tavares", `marcos.cliente@exemplo.com`) avaliando a Carla. Situações variadas:
   algumas ENVIADA, algumas EM_PREENCHIMENTO, a maioria NAO_INICIADA. **Deixe NAO_INICIADA** as designações em que
   Carla, Bruno e Diego são avaliadores, e a do externo, para a demonstração. Convites gravados como enviados (e na caixa de e-mails).
3. Um ciclo "Ciclo 2027.1" em RASCUNHO, para mostrar a edição.

Logue o link do avaliador externo no INFO ao subir, para a demonstração.

## Página inicial

RH: ciclos abertos e % respondido; gestor: pendentes da equipe.

## Testes

Criar e agendar ciclo válido; validações 6a, 8a, 10b; sugestão de avaliadores; externo recebe token; abertura com data
de hoje abre e manda convites; "Encerrar agora" publica o evento (verifique com um `@RecordApplicationEvents` ou
contando registros de auditoria); andamento do gestor só com a equipe dele (ver um avaliado de outra equipe pela URL dá
403); colaborador recebe 403 em `/ciclos` e `/ciclos/andamento`.
```
