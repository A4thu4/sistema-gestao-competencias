# Prompt B4-brief-avaliacoes (texto completo)

Ferramenta: Anthropic, Claude (Claude Code). Data: 23/11/2026. Texto entregue a cada sessão de desenvolvimento; endereços internos omitidos.

```text
# Pacote `avaliacoes`, parte 2 — UC-03 Responder avaliação e UC-09 Reabrir avaliação e moderar comentários

Casos de uso: `git show origin/docs/parte-3-realinhamento:docs/11-casos-de-uso/UC-03-responder-avaliacao.md` (completo)
e `UC-09-reabrir-avaliacao-moderar-comentarios.md` (só o resumo). Telas: `TL-03-responder-avaliacao.png` (TL-09 não
existe). Requisitos: RF-06, RF-11, RF-14 a RF-18, RF-22, RF-25, RF-34, RNF-01, RNF-05, RNF-08. Tabelas: `rascunho_resposta`,
`avaliacao_enviada`, `resposta` em `V4_1__cria_avaliacao_resposta.sql` — **leia o comentário do topo dessa migração**:
ele define o desenho do anonimato (DA-06), que é o ponto mais importante deste pacote.

O pacote `ciclos` já está pronto e juntado na sua branch-base: leia `ciclos/package-info.java` e `ciclos/servico/`
(designações, `TipoAvaliador` vem de `competencias.servico`, `aceitaRespostas`, `marcarEmPreenchimento`, `marcarEnviada`,
`reabrir`, `designacaoPorToken`, `questionarioDoAvaliado`...). Faça `git merge feat/prototipo-completo` no seu worktree
antes de começar.

## UC-03 Responder avaliação

- `/avaliacoes` (todos os perfis internos): avaliações sob responsabilidade do usuário, agrupadas por ciclo, com avaliado,
  relação, situação (não iniciada, em preenchimento, enviada) e encerramento do ciclo. 2a: sem pendências, mensagem.
- `/avaliacoes/{designacaoId}`: questionário do cargo do avaliado (`QuestionarioService.detalharPara(questionarioId,
  relacao)` — só as perguntas da relação, RF-11), agrupado por competência, com a descrição de cada nível (RF-25) e
  "Não tenho como avaliar" em cada item de escala (RF-06), comentários nas abertas. Visual da TL-03 (escala em cartões
  clicáveis: classe `escala` do `app.css`), mas pode mostrar todas as competências numa página rolável com uma barra de
  progresso — o que for mais rápido de preencher no celular (RNF-07, RNF-08: 10 minutos por pessoa).
- Aviso claro de anonimato quando a relação é par, subordinado ou externo (RNF-01); para chefe e autoavaliação, aviso
  de que a resposta é identificada.
- **Salvamento automático (RF-14):** cada resposta vai por `fetch` (POST com o token CSRF do `<meta>` ou do formulário)
  para um endpoint que grava em `rascunho_resposta` e marca a designação EM_PREENCHIMENTO; indicador "Salvo às 14:32".
  Funciona também sem JS (botão "Salvar rascunho"). \*a: ao voltar, reabre com as respostas.
- Obrigatórias pendentes marcadas no item (RF-15, classe `pendente`); 8a: envio barrado, lista dos pendentes e foco no
  primeiro.
- Envio (passos 7–10) com confirmação ("não poderá ser alterada depois"). Operação `enviarAvaliacao` (contrato CO-02 do
  documento de arquitetura): confere que a designação é do usuário, que o ciclo aceita respostas e que não há
  obrigatória pendente; cria `avaliacao_enviada` com **id UUID aleatório, sem designação, sem avaliador, sem data**, e
  `verificador = HMAC-SHA256(sgc.chave-verificador, designacaoId)` em hexadecimal; copia o rascunho para `resposta`;
  apaga o rascunho; chama `ciclos.marcarEnviada`. Tudo numa transação. 10a: enviada não se edita.
- Gestor avaliando o subordinado (relação CHEFE): mostra as observações do período (UC-08, `observacoesDoPeriodo` entre
  o encerramento do ciclo anterior ou 12 meses atrás e hoje — RF-17) e o resultado do ciclo anterior (RF-18). O
  resultado vem do pacote `resultados`, que depende de você: declare a interface `ResultadoAnterior` em
  `avaliacoes.servico` (ex.: `Optional<ResumoResultadoAnterior> doAvaliado(Long avaliadoId, Long cicloAtualId)` com
  nome do ciclo e médias por competência) e injete com `ObjectProvider`; sem implementação, mostre "sem resultado
  anterior" (4b). 4a: autoavaliação sem observações.
- \*b / RF-09: ciclo encerrado bloqueia salvar e enviar, com mensagem.
- **Avaliador externo (1a, RF-34):** `/externo/{token}` sem login (a URL já é pública no filtro). Página com layout
  simples (sem menu lateral; crie um fragmento `layout-externo` no seu pacote ou use `head` + um topo próprio), mesmas
  regras de salvamento e envio, perguntas da relação EXTERNO. Link inválido, já enviado ou ciclo encerrado: mostra o
  motivo e não mostra o questionário. O CSRF continua valendo nos POSTs.
- RNF-03: avaliador só abre as designações dele; `/avaliacoes/{id}` de outra pessoa dá 403.
- Implemente `competencias.servico.ConsultaUsoQuestionario.possuiRespostas` (há `avaliacao_enviada` ou
  `rascunho_resposta` de perguntas daquele questionário) como `@Component`.
- Página inicial: cartão "Avaliações pendentes" (com o número) para quem tem.

## UC-09 Reabrir avaliação e moderar comentários (RH, `/moderacao/**` já exige RH no filtro)

- **Reabrir:** escolher ciclo (aberto), ver as designações ENVIADA com avaliador e avaliado (isso o RH já sabe pelo
  andamento — não mostra respostas), informar o motivo (obrigatório) e confirmar. O serviço acha a `avaliacao_enviada`
  pelo `verificador` (HMAC da designação), copia as respostas de volta para `rascunho_resposta`, apaga a avaliação
  enviada, chama `ciclos.reabrir`, registra auditoria (usuário, data, motivo) e avisa o avaliador por e-mail. O
  avaliador corrige e envia de novo (o UC-03 cuida). Ciclo encerrado não reabre (mensagem).
- **Moderar comentários:** escolher ciclo, listar os textos das perguntas abertas **sem autor e sem relação** (só
  avaliado, competência e pergunta), ocultar com motivo (obrigatório) ou voltar a exibir. Auditoria das duas ações.
  Comentário oculto some dos resultados (RF-22).
- O `verificador` e os ids das avaliações enviadas **nunca** aparecem em tela, URL, log ou exportação: use o id da
  resposta (comentário) e o id da designação nas URLs.

## API pública para o pacote `resultados`

- `List<RespostaConsolidavel> respostasDoCiclo(Long cicloId)`: record com `avaliacaoId` (UUID, só para contar quantas
  avaliações há por grupo), `avaliadoId`, `relacao` (`TipoAvaliador`), `perguntaId`, `competenciaId`, `nota` (Integer
  ou null), `naoAvalia`. Só perguntas de escala.
- `List<ComentarioResumo> comentariosVisiveis(Long cicloId, Long avaliadoId)`: texto, competência e pergunta, sem
  autor, sem relação, só os não ocultos.
- `int avaliacoesEnviadas(Long cicloId, Long avaliadoId, TipoAvaliador relacao)`.

## Dados de exemplo (complete `DadosExemploAvaliacoes`, `@Order(40)`)

Use o próprio serviço de envio (`enviarAvaliacao`) sempre que possível, para o exemplo seguir as mesmas regras:
- **Ciclo 2026.1 (encerrado):** respostas de todas as designações dele. Notas realistas e com perfil por pessoa (por
  exemplo: Carla forte em Atendimento ao cliente e fraca em Pensamento analítico; Diego o inverso; Bruno bem avaliado
  pelos subordinados em Comunicação e pior em Liderança), com algum "não tenho como avaliar" e comentários abertos
  plausíveis — inclua **um comentário grosseiro** (sem palavrão; algo como "Ele é arrogante e ninguém aguenta
  trabalhar com ele") para a demonstração da moderação. Use `java.util.Random` com semente fixa para ser reproduzível.
  Como o ciclo está encerrado, o serviço vai recusar o envio: crie um caminho interno do seed (ou abra o ciclo, envie e
  feche) — o importante é que as linhas fiquem iguais às de um envio normal.
- **Ciclo 2026.2 (aberto):** respostas das designações que o `ciclos` deixou ENVIADA, e rascunhos parciais nas
  EM_PREENCHIMENTO. Não toque nas NAO_INICIADA.

## Testes

Envio grava respostas sem designação/avaliador e apaga o rascunho; a tabela `avaliacao_enviada` não tem como chegar
ao avaliador (teste do anonimato: confira que nenhuma coluna aponta para a designação ou o usuário); salvamento
automático; obrigatória pendente barra o envio; enviada não edita; ciclo encerrado bloqueia; externo por token
(válido, inválido, já enviado); 403 na designação de outra pessoa; perguntas filtradas pela relação; gestor vê
observações e o colaborador na autoavaliação não; reabertura devolve as respostas ao rascunho e audita; moderação
oculta e o comentário some de `comentariosVisiveis`; colaborador recebe 403 em `/moderacao`.
```
