# Prompt B1-brief-comum (texto completo)

Ferramenta: Anthropic, Claude (Claude Code). Data: 23/11/2026. Texto entregue a cada sessão de desenvolvimento; endereços internos omitidos.

```text
# Brief comum — protótipo do Sistema de Gestão de Competências (SGC)

Projeto da disciplina Arquitetura e Desenho de Software (PUC Goiás, 2026/2). Protótipo web em Java 21, Spring Boot 3.5,
Thymeleaf + Bootstrap 5, PostgreSQL 16 e Flyway. Tudo em português do Brasil: nomes de classes, métodos, textos de tela
e mensagens, em linguagem simples, sem jargão (RNF-09). A professora vai ver o sistema funcionando nesta semana; ele
precisa funcionar de ponta a ponta, ter aparência profissional e seguir os casos de uso.

## Onde está tudo

- Seu worktree e branch estão no seu pedido. O código fica em `<worktree>/prototipo`.
- **Leia antes de começar:**
  - `prototipo/README.md` (regras de organização e de dependência entre pacotes);
  - as migrações em `prototipo/src/main/resources/db/migration/` (o esquema do banco **já existe** para todos os
    pacotes; as entidades JPA devem bater com ele, porque `ddl-auto: validate`);
  - o código-base: `identidade/servico/` (`UsuarioService`, `PessoaResumo`, `CargoResumo`, `UsuarioAutenticado`,
    `ContribuicaoPainel`, `CartaoPainel`, `DadosExemploIdentidade` — a empresa de exemplo), `identidade/web/SegurancaConfig`,
    `compartilhado/` (`EventoAuditoria`, `EmailService`, `RegraDeNegocioException`, `NaoEncontradoException`,
    `TratamentoDeErros`);
  - `templates/fragments/layout.html`, `templates/identidade/pessoas.html` (modelo de página) e `static/css/app.css`
    (classes `cartao`, `titulo-pagina`, `subtitulo-pagina`, `selo selo-<situacao em minúsculas>`, `escala`, `pendente`).
- **Casos de uso** (versão mais nova, na branch da Parte III):
  `git show origin/docs/parte-3-realinhamento:docs/11-casos-de-uso/<arquivo>.md`. UC-01 a UC-05 estão completos;
  UC-06 a UC-10 só têm o resumo no topo: implemente a partir do resumo, dos requisitos citados e do bom senso.
- **Requisitos:** `git show origin/docs/parte-3-realinhamento:docs/13-especificacao-requisitos/ers-ieee830.md`.
- **Telas do Figma:** `docs/12-prototipos/telas/TL-0N-*.png` (abra com a ferramenta Read para ver a imagem). Siga o
  visual: barra lateral escura (já pronta no layout), topo branco com trilha, cartões brancos.

## Regras de arquitetura (valem nota)

- Pacote por módulo, quatro camadas: `web` (controllers), `servico` (`@Service`, `@Transactional`), `dominio`
  (entidades JPA e objetos de valor), `repositorio` (Spring Data).
- O controller só chama serviços. Nada de repositório no controller.
- Um pacote usa outro **só pelo serviço público dele**, recebendo records/DTOs. Nunca use entidade ou repositório de
  outro pacote. Entidades guardam referências a outros pacotes como `Long` (por exemplo `Long avaliadoId`), não como
  relação JPA.
- Quando um pacote precisa de uma informação de um pacote que depende dele (o que criaria ciclo), use **inversão de
  dependência**: o pacote declara uma interface no próprio `servico` e o outro pacote a implementa depois. Injete com
  `ObjectProvider<Interface>` e um comportamento padrão quando não houver implementação.
- **Controle de acesso no servidor (RNF-03):** regras por perfil com `@PreAuthorize("hasRole('RH')")` etc. nos
  controllers. **Não edite `SegurancaConfig`** (outros agentes trabalham em paralelo). Regras por pessoa/equipe nos
  serviços, conferindo o `UsuarioAutenticado` (`eu.id()`, `eu.perfil()`); negar = lançar
  `org.springframework.security.access.AccessDeniedException` (vira página 403 sem dados).
- Usuário logado nos controllers: `@AuthenticationPrincipal UsuarioAutenticado eu`.
- **Auditoria (RNF-05, DA-08):** publique `EventoAuditoria.de("ACAO", "detalhe", "motivo")` com
  `ApplicationEventPublisher`; o `compartilhado` grava.
- **E-mail:** `EmailService.enviar(para, assunto, corpo, tipo)` (caixa de saída visível ao RH em /emails).
- **Migrações:** não edite as existentes. Se precisar mudar o esquema, crie a próxima da faixa do seu pacote
  (`V2_2__...` para competencias etc.) e diga no relatório.

## Telas

- Templates em `templates/<pacote>/`. Copie o esqueleto de `identidade/pessoas.html` (head, menu com a chave do item
  ativo, topo com a trilha, alertas, scripts). Chaves do menu: inicio, avaliacoes, resultados, planos, observacoes,
  andamento, competencias, ciclos, moderacao, pessoas, solicitacoes, auditoria, emails, meus-dados.
- Formulários: POST com `th:action` (o CSRF entra sozinho). Padrão Post/Redirect/Get com `RedirectAttributes` e flash
  `sucesso`, `erro` ou `aviso` (o layout já mostra). Erro de validação: volte à tela do formulário com as mensagens.
- Responsivo (RNF-07): use o grid do Bootstrap, `table-responsive` e formulários que funcionem no celular.
- Gráficos: Chart.js 4.4.1 (`https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js`).
- Página inicial: implemente `ContribuicaoPainel` (`@Component`) no seu pacote com os cartões relevantes por perfil
  (ex.: "3 avaliações pendentes").

## Dados de exemplo

Crie `DadosExemplo<Pacote>` (`ApplicationRunner`, `@Order` indicado no seu pedido,
`@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")`, idempotente: só roda com as suas tabelas
vazias). Use os serviços do próprio pacote sempre que possível. Pessoas (senha `senha123`, e-mails `@exemplo.com`):
rh (Ana Souza, RH), gestor (Bruno Lima, gestor do Suporte), colaborador (Carla Dias), diego, elisa, fabio (equipe do
Bruno), helena (Helena Prado, gerente, chefe do Bruno, do Gustavo e da Ana), gustavo (Gustavo Reis, gestor do
Desenvolvimento), igor, julia, katia (equipe do Gustavo). **Não use `katia@exemplo.com` nos testes**: ela fica sem aceitar
o termo de consentimento para demonstrar o primeiro acesso.

## Testes

- Estenda `br.edu.pucgoias.sgc.TesteIntegracao` (sobe tudo contra o PostgreSQL, com os dados de exemplo, e desfaz cada
  teste). `como("rh@exemplo.com")` autentica; POST precisa de `.with(csrf())`.
- Cubra o cenário principal, as extensões importantes e a negação de acesso. Testes de unidade para regras de cálculo.
- **Compilar e testar:** `~/projects/sgc-worktrees/sgc-vm.sh <worktree>/prototipo` — copia o código para a VM de build
  e roda `mvn -B verify` contra um PostgreSQL novo (1 a 3 min). Mostra só os erros e a causa raiz. **Nunca compile nem
  rode Java/Maven na Raspberry Pi** (ela não tem Java e trava). Outro agente pode estar compilando: o script espera a
  vez sozinho. Não rode nenhum outro comando na VM.

## Entrega

- Atualize o `package-info.java` do pacote: responsabilidade, casos de uso, interface oferecida aos outros pacotes e
  dependências.
- Commit na sua branch quando o build estiver verde, mensagem `feat(<pacote>): ...` em português, terminando com a
  linha `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`. Pode fazer mais de um commit. **Não faça push nem
  merge.**
- Relatório final (curto): o que foi implementado por passo/extensão do caso de uso; a **API pública** do pacote
  (assinaturas dos métodos de serviço e records que outros pacotes vão usar); interfaces de inversão de dependência que
  você declarou e quem deve implementá-las; o que ficou de fora e por quê; a linha final do `Tests run`.
```
