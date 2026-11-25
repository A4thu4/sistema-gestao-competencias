# Prompt B5: revisão do protótipo (texto completo)

Ferramenta: Anthropic, Claude (Claude Code, modelo Opus 5.5). Data: 25/11/2026. Endereços internos omitidos.

## Pedido de Gabriel Araújo Caetano à ferramenta

```text
ta temos ainda algumas hrs e ta bem top quero q passe por um agente revisor ou algo para ver se esta atendendo as espectativas nos pedidos da professora registrados anotar melhorias obrigatorias e nao obrigatorias .... mandar pros agentes do paperclip desenvolverem as melhorias em ate 5 hrs
```

```text
e tbm tirar a cara de ia q esta hj no sistema isso e uma melhoria obrigatoria e importante
```

```text
paginas sem nexo faltando coisas ou sobre coisas q n deveriam esta na cara ali
```

## Prompt entregue ao agente revisor

```text
Você é revisor independente (somente leitura) de um trabalho de faculdade. Responda em português do Brasil.

## Contexto
Disciplina Arquitetura e Desenho de Software, PUC Goiás 2026/2. Sistema de Gestão de Competências (SGC): avaliação de competências chefe–colaborador, pares e 360º, anonimato, resultados, histórico, plano de desenvolvimento, LGPD. Protótipo em Java 21 + Spring Boot 3 + Thymeleaf + PostgreSQL/Flyway.

Hoje à noite (25/11/2026) a professora quer ver **a tela de login e o sistema rodando**. A entrega final (documento Parte III + apresentação) é 03 e 04/12. O grupo quer impressionar hoje e ter uma lista de melhorias para os agentes implementarem em até 5 horas.

## Onde está tudo
- Repo local, branch `feat/prototipo-completo` (código em `prototipo/`, docs em `docs/`). NÃO altere nada, não faça checkout, não faça commit.
- Enunciado oficial da Parte III (resumo): `git show origin/docs/parte-3-realinhamento:docs/15-documento-arquitetura/enunciado-parte-iii.md`. Os casos de uso UC-06 a UC-10 completos e telas TL-06..TL-10 também estão nesse branch (`docs/11-casos-de-uso/`, `docs/12-prototipos/`). Use `git show <branch>:<caminho>` para ler.
- Requisitos: `docs/08-requisitos/lista-requisitos.md`, ERS `docs/13-especificacao-requisitos/ers-ieee830.md`, casos de uso `docs/11-casos-de-uso/`, protótipos de tela (Figma/PNG) `docs/12-prototipos/`, matrizes `docs/14-rastreabilidade/`, documento de arquitetura `docs/15-documento-arquitetura/` (convenção: nomes de pacotes/classes dos diagramas = código), situação do protótipo `docs/17-prototipo/README.md` e `prototipo/README.md`.
- Sistema rodando: [endereço interno] (senha de todos `senha123`; usuários `rh@exemplo.com`, `gestor@exemplo.com`, `colaborador@exemplo.com`, `katia@exemplo.com` primeiro acesso). Pode usar `curl` com cookie/CSRF. Para navegador de verdade há Playwright na VM, script `prints.mjs` (uso no cabeçalho do arquivo; grava PNG em `shots/`).

## Regras de ambiente (obrigatórias)
- A Raspberry Pi tem pouca RAM: NÃO rode Maven, build, testes, npm install nem Chromium na Pi. Nada de instalar. Leitura de arquivos, grep, git show e curl são ok.
- Não reinicie nem derrube contêineres na VM (ela roda outros serviços). Não altere dados de forma destrutiva no sistema (não encerre ciclos, não libere resultados, não exclua nada); navegar e abrir telas é ok.

## O que revisar
1. **Pedidos da professora** registrados (enunciado + o pedido de hoje: login + sistema rodando + protótipo em código combinado em aula). O protótipo atende? Login funciona, perfis, controle de acesso.
2. **Casos de uso UC-01 a UC-10**: para cada um, o fluxo principal e as principais extensões descritas no documento existem no sistema? Marque implementado / parcial / ausente, com evidência (arquivo:linha ou URL/tela).
3. **Fidelidade às telas TL-01..TL-10** do protótipo de IHC (diferenças grandes que a professora notaria).
4. **Consistência código × arquitetura**: pacotes do código batem com o que a documentação diz (monolito modular, pacotes identidade/competencias/ciclos/avaliacoes/resultados/privacidade/compartilhado, camadas web/servico/dominio/repositorio, regras de dependência)? Há violações (controller usando repositório, pacote usando entidade de outro)?
5. **RNFs visíveis** (segurança/LGPD, usabilidade no celular, mensagens de erro, anonimato com mínimo de respostas).
6. **Pontos que impressionam ou que envergonham numa demo de 5 minutos hoje** (erros 500, textos em inglês, telas quebradas, dados estranhos).

## Saída (seu relatório final, só isto)
Duas listas, cada item com: ID (M-01.. para obrigatórias, O-01.. para opcionais), título curto, por que (qual pedido/UC/RF/RNF), evidência, o que fazer concretamente, pacote/arquivos afetados, estimativa em horas (seja realista; o total das obrigatórias precisa caber em ~4h de agente) e risco de quebrar algo.
- **Obrigatórias**: falha frente a um pedido explícito da professora ou ao caso de uso documentado, ou algo que envergonha na demo.
- **Não obrigatórias**: melhorias que impressionam ou aumentam a nota, mas não bloqueiam.
Inclua no topo um parágrafo de veredito (atende ou não, nota de prontidão 0–10) e no fim uma tabela UC × situação. Seja específico e honesto; não invente o que não verificou — marque "não verificado" quando for o caso.
```

## Complementos enviados ao revisor durante o trabalho

```text
Novo critério do dono, OBRIGATÓRIO e de alta prioridade: identificar tudo que dá "cara de IA" ao sistema e listar como itens M-xx. Olhe no sistema rodando e no código: visual genérico de template (paleta azul/roxa padrão, cartões iguais, ícones decorativos em excesso, gradientes, layout de dashboard de "SaaS gerado"), textos de interface com tom de IA (frases longas e explicativas demais, travessões em excesso, "Olá, Ana" + parágrafos didáticos, microcopy redundante, emojis), dados de exemplo artificiais demais, comentários/Javadoc prolixos ou "explicando o óbvio" no código, nomes e mensagens de commit com padrão de IA, linhas Co-Authored-By/Generated with Claude nos commits e PR, README com cara de gerado. Para cada item: onde está (tela/URL ou arquivo:linha), exemplo concreto do trecho atual e como deve ficar (texto curto e natural de sistema feito por estudante/empresa real, visual com identidade própria). Separe "visual", "textos da interface", "código/comentários" e "git/PR". Estime horas.
```

```text
Mais um critério OBRIGATÓRIO do dono: páginas sem nexo — telas onde falta algo que o usuário esperaria ali (ação, informação, ligação com o fluxo) ou onde aparece coisa que não deveria estar na cara (informação técnica, ID interno, texto de explicação de sistema, cartão irrelevante para aquele perfil, menu com item que o perfil não usa, dados de outro contexto). Percorra as telas principais de cada perfil (RH, gestor, colaborador, primeiro acesso, avaliador externo) e liste item a item: tela/URL, o que sobra, o que falta, como deve ficar. Entra na lista M-xx.
```
