# Prompt B3-brief-resultados (texto completo)

Ferramenta: Anthropic, Claude (Claude Code). Data: 23/11/2026. Texto entregue a cada sessão de desenvolvimento; endereços internos omitidos.

```text
# Pacote `resultados` — UC-04 Consultar resultados e UC-06 Gerenciar plano de desenvolvimento

Casos de uso: `git show origin/docs/parte-3-realinhamento:docs/11-casos-de-uso/UC-04-consultar-resultados.md`
(completo) e `UC-06-gerenciar-plano-desenvolvimento.md` (só o resumo). Telas: `TL-04-consultar-resultados.png` (TL-06
não existe). Requisitos: RF-19 a RF-24, RF-26, RF-27, RF-28, RF-34, RNF-01, RNF-02, RNF-03, RNF-05, RNF-12. Tabelas:
`V5_1__cria_resultado_plano.sql`.

Os pacotes `identidade`, `competencias`, `ciclos`, `avaliacoes` e `privacidade` estão prontos na sua branch-base. Leia o
`package-info.java` e o `servico/` de cada um. Você usa: `ciclos` (`CicloEncerradoEvento`, `encerrados()`,
`avaliadosDoCiclo`, `liberarResultado`, `questionarioDoAvaliado`), `avaliacoes` (`respostasDoCiclo`,
`comentariosVisiveis`, `avaliacoesEnviadas`), `competencias` (`competenciasPorIds`, `detalhar`) e `identidade`.

## Consolidação (RF-19, contrato CO-03 `consolidarResultados(Long cicloId)`)

- `@EventListener` de `CicloEncerradoEvento` chama `consolidarResultados`. Também há botão do RH "Recalcular" no ciclo
  encerrado (demonstra RNF-12).
- Para cada avaliado × competência × grupo (CHEFE, AUTO, PAR, SUBORDINADO, EXTERNO): média aritmética das notas das
  perguntas de escala daquela competência, **ignorando "não tenho como avaliar"** (RF-06); `num_respostas` = número de
  avaliações enviadas daquele grupo para o avaliado que deram ao menos uma nota na competência; `escala_max` do
  questionário. Apaga as linhas do ciclo e grava de novo: o mesmo conjunto de respostas sempre gera o mesmo resultado
  (RNF-12). Arredonde com `BigDecimal` HALF_UP, 2 casas.
- Testes de unidade do cálculo (média, exclusão do "não tenho como avaliar", reprodutibilidade, contagem por grupo).

## Regras de exibição (as mais cobradas na avaliação)

- **Mínimo de 3 (RNF-02):** média de PAR, SUBORDINADO e EXTERNO só aparece com `num_respostas >= 3`; abaixo disso,
  "—" e o aviso "respostas insuficientes para preservar o anonimato". Isso vale em tela, gráfico, relatório de equipe e
  exportação. CHEFE e AUTO não têm mínimo (são identificados por natureza).
- **Nota final:** média das médias dos grupos **exibíveis** exceto a autoavaliação (documente essa regra na tela, num
  texto de ajuda curto). Média geral = média das notas finais das competências. Ponto forte = maior nota final; foco de
  desenvolvimento = menor (como na TL-04).
- **Anonimato (RNF-01):** nenhuma tela ou arquivo mostra quem respondeu. Comentários só via `comentariosVisiveis`, sem
  autor (RF-22).
- **Visibilidade (RNF-03), no servidor:** colaborador vê só o próprio resultado e só depois de liberado (2b); gestor vê
  o próprio e o da equipe direta; RH vê todos. Pessoa de fora pela URL → 403 sem dados (\*a). Ciclo não encerrado → 2c.
- **Auditoria (RNF-05):** cada abertura de painel individual, cada liberação e cada exportação.

## Telas

- `/resultados`: ciclos com resultado disponível para o perfil, data de encerramento, situação da liberação; para gestor
  e RH, escolha da pessoa. RH vê "Liberar resultado" nos ciclos consolidados não liberados (2a): confirma, chama
  `ciclos.liberarResultado`, manda e-mail a cada avaliado e audita.
- **Painel individual** (TL-04): cabeçalho com colaborador e ciclo, botões Exportar PDF e Exportar Excel; três cartões
  (média geral / escala, ponto forte, foco de desenvolvimento); gráfico radar (Chart.js) com uma série por grupo
  exibível (autoavaliação, chefe, pares, subordinados, externos); tabela por competência com as colunas de cada grupo
  e a nota final (cores: verde para a maior, laranja para a menor, como na TL-04); comentários; **evolução** (RF-26):
  gráfico de linhas com a nota final por competência em cada ciclo encerrado da pessoa — se as escalas forem
  diferentes, normalize em percentual da escala e diga isso na legenda. 6a: sem histórico, mensagem.
- **Relatório da equipe ou área** (3a): tabela pessoas × competências (nota final) e média da equipe por competência,
  sem respondentes. Gestor: só a equipe direta; RH: qualquer equipe (por gestor) ou área.
- **Exportação** (RF-23): PDF com OpenPDF e Excel com Apache POI (já no pom), com os mesmos dados e as mesmas restrições
  da tela.

## UC-06 Plano de desenvolvimento (`/planos`)

- Gestor: escolhe um membro da equipe direta cujo resultado do último ciclo já foi liberado; vê as competências de
  menor nota sugeridas; cria metas (competência, descrição, ações previstas — ex.: um curso —, prazo, situação).
- Colaborador: vê o próprio plano, registra o progresso de cada ação; colaborador e gestor atualizam a situação (não
  iniciada, em andamento, concluída, cancelada); cada atualização grava data, autor e texto (`meta_atualizacao`) e
  aparece como linha do tempo.
- Lembrete trimestral (RF-28): rotina `@Scheduled` diária que manda e-mail ao gestor e ao colaborador quando uma meta
  aberta está sem atualização há 90 dias (grava `ultimo_lembrete_em`, sem repetir em menos de 90 dias); botão do gestor
  "Enviar lembrete de revisão agora" para a demonstração.
- RNF-03: colaborador só o próprio plano; gestor só a equipe direta; RH consulta todos (somente leitura).

## Inversões de dependência que você implementa (`@Component`)

- `avaliacoes.servico.ResultadoAnterior` (resultado do ciclo encerrado anterior do avaliado, para o gestor no UC-03,
  RF-18 — com as mesmas regras do mínimo de 3).
- `privacidade.servico.FonteDadosPessoais`: uma fonte com os resultados consolidados da pessoa (ciclo, competência,
  nota final; só grupos exibíveis) e outra com as metas do plano.

## Página inicial

Colaborador: "Seu resultado do ciclo X foi liberado"; gestor: metas da equipe em andamento; RH: ciclos encerrados sem
liberação.

## Dados de exemplo (`DadosExemploResultados`, `@Order(50)`)

Consolide o Ciclo 2026.1 (encerrado, com respostas criadas pelo `avaliacoes`) e **libere** o resultado. Crie um plano
para a Carla (2 metas nas competências de menor nota, uma em andamento com 2 atualizações, uma não iniciada) e um para
o Diego. Não consolide o 2026.2 (está aberto) — a demonstração vai encerrá-lo ao vivo.

## Testes

Consolidação (unidade e integração) e reprodutibilidade; grupo com menos de 3 não aparece na tela nem no Excel;
colaborador sem liberação vê 2b; colaborador não vê resultado de outro (403); gestor vê equipe e não vê outra equipe
(403); RH libera e os e-mails saem; acesso e exportação auditados; exportação PDF e Excel respondem com o tipo certo;
plano: gestor cria meta, colaborador atualiza, histórico gravado, colaborador de outra equipe 403; lembrete trimestral
dispara para meta parada há mais de 90 dias.
```
