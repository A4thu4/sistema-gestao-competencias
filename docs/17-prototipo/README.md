# Protótipo do sistema

Protótipo funcional com as telas dos casos de uso completos (UC-01 a UC-05). O código fica em [`prototipo/`](../../prototipo), e as instruções para rodar estão no [README do protótipo](../../prototipo/README.md). Quem implementar o protótipo fica dispensado da última prova. O protótipo não faz parte do enunciado da Parte III (entrega do documento em 03/12); está no milestone **Protótipo**, com prazo a confirmar com a docente.

**Stack:** Java 21, Spring Boot 3, Thymeleaf, Bootstrap 5, Chart.js, PostgreSQL 16 (Docker Compose) e Flyway. Os motivos estão nas decisões `DA-NN` do [Documento de Arquitetura](../15-documento-arquitetura/secoes/03-requisitos-nao-funcionais.md#32-racional-dos-mecanismos-arquiteturais-adotados).

## Pacotes e telas

| Pacote | Caso de uso | Tela do Figma | Migração | Responsável | Issue | Status |
|--------|-------------|---------------|----------|-------------|-------|--------|
| `identidade` | Login, perfis e layout | — | `V1_x` | Rikelmy Andrade | #55 | A fazer |
| `competencias` | [UC-01](../11-casos-de-uso/UC-01-gerenciar-competencias-questionarios.md) | [TL-01](../12-prototipos/telas/TL-01-gerenciar-competencias.png) | `V2_x` | José Eduardo | #56 | A fazer |
| `ciclos` | [UC-02](../11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md) | [TL-02](../12-prototipos/telas/TL-02-criar-ciclo.png) | `V3_x` | José Eduardo | #57 | A fazer |
| `avaliacoes` | [UC-03](../11-casos-de-uso/UC-03-responder-avaliacao.md) | [TL-03](../12-prototipos/telas/TL-03-responder-avaliacao.png) | `V4_x` | Gabriel Araújo Caetano | #58 | A fazer |
| `resultados` | [UC-04](../11-casos-de-uso/UC-04-consultar-resultados.md) | [TL-04](../12-prototipos/telas/TL-04-consultar-resultados.png) | `V5_x` | Lorenzo Oliveira Maciel | #59 | A fazer |
| `privacidade` e `compartilhado` | [UC-05](../11-casos-de-uso/UC-05-gerenciar-consentimento.md) e log de auditoria | [TL-05](../12-prototipos/telas/TL-05-gerenciar-consentimento.png) | `V6_x` | Arthur Mamedes Borges | #60 | A fazer |

Ajustes das telas apontados na revisão do Marco 4 que entram no protótipo: `P-11` (colunas de pares, subordinados e externos na TL-04), `P-12` (direitos do titular e "Meus dados" na TL-05), `P-13` (layout responsivo) e `P-14` (usuário correto no cabeçalho da TL-03).

## Demonstração

> Issue #63. Responsável: Lorenzo Oliveira Maciel. Milestone Protótipo.

### Usuários de exemplo

| Perfil | Usuário | Senha |
|--------|---------|-------|
| RH | _..._ | _..._ |
| Gestor | _..._ | _..._ |
| Colaborador | _..._ | _..._ |

### Roteiro

1. _RH entra, cria uma competência e publica um questionário (UC-01)._
2. _RH cria um ciclo e define avaliados e avaliadores (UC-02)._
3. _Gestor e colaboradores respondem; avaliador externo responde pelo link (UC-03)._
4. _RH encerra o ciclo; colaborador e gestor consultam os resultados (UC-04)._
5. _Usuário consulta o termo e "Meus dados" (UC-05)._

### Capturas

Capturas das telas implementadas em [`telas/`](telas), com o mesmo ID das telas do Figma (`TL-NN`).
