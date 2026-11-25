# Protótipo do sistema

Protótipo funcional com as telas dos casos de uso completos (UC-01 a UC-05). O código fica em [`prototipo/`](../../prototipo), e as instruções para rodar estão no [README do protótipo](../../prototipo/README.md). Quem implementar o protótipo fica dispensado da última prova.

**Stack:** Java 21, Spring Boot 3, Thymeleaf, Bootstrap 5, Chart.js, PostgreSQL 16 (Docker Compose) e Flyway. Os motivos estão nas decisões `DA-NN` do [Documento de Arquitetura](../15-documento-arquitetura/documento-arquitetura.md#439-decisões-tomadas).

## Pacotes e telas

| Pacote | Caso de uso | Tela do Figma | Migração | Responsável pela issue | Implementação | Revisão do código | Issue |
|--------|-------------|---------------|----------|------------------------|---------------|-------------------|-------|
| `identidade` | Login, perfis e layout ([UC-10](../11-casos-de-uso/UC-10-autenticar-se.md)) | TL-10 | `V1_x` | Rikelmy Andrade | Gabriel Araújo Caetano, com IA | _a definir_ | #55 |
| `competencias` | [UC-01](../11-casos-de-uso/UC-01-gerenciar-competencias-questionarios.md) | [TL-01](../12-prototipos/telas/TL-01-gerenciar-competencias.png) | `V2_x` | José Eduardo | Gabriel Araújo Caetano, com IA | _a definir_ | #56 |
| `ciclos` | [UC-02](../11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md), UC-07 | [TL-02](../12-prototipos/telas/TL-02-criar-ciclo.png) | `V3_x` | José Eduardo | Gabriel Araújo Caetano, com IA | _a definir_ | #57 |
| `avaliacoes` | [UC-03](../11-casos-de-uso/UC-03-responder-avaliacao.md), UC-08, UC-09 | [TL-03](../12-prototipos/telas/TL-03-responder-avaliacao.png) | `V4_x` | Gabriel Araújo Caetano | Gabriel Araújo Caetano, com IA | _a definir_ | #58 |
| `resultados` | [UC-04](../11-casos-de-uso/UC-04-consultar-resultados.md), UC-06 | [TL-04](../12-prototipos/telas/TL-04-consultar-resultados.png) | `V5_x` | Lorenzo Oliveira Maciel | Gabriel Araújo Caetano, com IA | _a definir_ | #59 |
| `privacidade` e `compartilhado` | [UC-05](../11-casos-de-uso/UC-05-gerenciar-consentimento.md) e log de auditoria | [TL-05](../12-prototipos/telas/TL-05-gerenciar-consentimento.png) | `V6_x` | Arthur Mamedes Borges | Gabriel Araújo Caetano, com IA | _a definir_ | #60 |

Todos os pacotes estão prontos. O uso de IA na implementação está declarado em [uso-de-ia.md](uso-de-ia.md). Pela
regra do enunciado, quem revisa não pode ser quem escreveu: a coluna "Revisão do código" deve ser preenchida por
integrantes que revisarem o pull request do pacote no GitHub.

Ajustes das telas apontados na revisão do Marco 4 que entram no protótipo: `P-11` (colunas de pares, subordinados e externos na TL-04), `P-12` (direitos do titular e "Meus dados" na TL-05), `P-13` (layout responsivo) e `P-14` (usuário correto no cabeçalho da TL-03).

## Demonstração

> Issue #63. Responsável: Lorenzo Oliveira Maciel.

Além dos UC-01 a UC-05, o protótipo implementa os UC-06 a UC-10: plano de desenvolvimento (`resultados`), andamento
do ciclo (`ciclos`), observações do gestor, reabertura e moderação (`avaliacoes`) e autenticação (`identidade`).
São 135 testes automatizados (`mvn verify`), e o roteiro abaixo também foi percorrido no navegador.

A empresa de exemplo é a Vértice Software, com três ciclos encerrados ou em andamento: 2025.2 e 2026.1 encerrados
(resultados liberados), 2026.2 aberto e 2027.1 em rascunho.

### Usuários de exemplo

Senha de todos: `senha123`. A lista completa está no [README do protótipo](../../prototipo/README.md#empresa-de-exemplo).

| Perfil | Usuário | Pessoa |
|--------|---------|--------|
| RH | `rh@exemplo.com` | Ana Souza |
| Gestor | `gestor@exemplo.com` | Bruno Lima (equipe: Carla, Diego, Elisa e Fábio) |
| Colaborador | `colaborador@exemplo.com` | Carla Dias |
| Primeiro acesso | `katia@exemplo.com` | Kátia Freitas (ainda não aceitou o termo) |
| Avaliador externo | link no log ao subir e em "E-mails enviados" | Marcos Tavares (cliente, avalia a Carla) |

### Roteiro (cerca de 15 minutos)

1. **Primeiro acesso (UC-10, UC-05):** entrar como `katia@` → o termo aparece antes de tudo → aceitar → página inicial.
2. **Competências (UC-01):** como `rh@`, abrir Competências → o rascunho "Questionário 2027" mostra as pendências que
   impedem a publicação → ver a pré-visualização "como o avaliador vê".
3. **Ciclo (UC-02, UC-07):** abrir o "Ciclo 2027.1" (rascunho) e mostrar as etapas e as sugestões de avaliadores →
   em Andamento do ciclo, o 2026.2 aberto, com quem já respondeu → "Enviar lembrete" → ver em E-mails enviados.
4. **Responder (UC-03, UC-08):** como `gestor@`, avaliar a Carla → as observações do período e o resultado do ciclo
   anterior aparecem ao lado → o salvamento é automático → enviar. Como `colaborador@`, avaliar um par → aviso de
   resposta anônima. Abrir o link do avaliador externo numa janela anônima e responder sem login.
5. **Reabrir e moderar (UC-09):** como `rh@`, em Reabertura e moderação, reabrir uma avaliação com motivo; na aba de
   comentários do Ciclo 2026.1, ocultar o comentário ofensivo sobre o Bruno → ver o registro em Log de auditoria.
6. **Resultados (UC-04):** como `rh@`, Ciclos → 2026.2 → "Encerrar agora" (consolida as médias) → Resultados →
   "Liberar resultado". Como `colaborador@`, abrir o resultado: radar, notas por grupo, grupos com menos de 3 respostas
   escondidos, comentários sem autor, evolução entre ciclos, exportar PDF e Excel. Trocar o número da pessoa na URL →
   acesso negado. Sem encerrar o 2026.2, o resultado do 2026.1 já mostra tudo isso: `/resultados/2/pessoas/4`
   (Carla), com a evolução em relação ao 2025.2.
7. **Plano (UC-06):** como `gestor@`, Plano de desenvolvimento da Carla → nova meta numa competência sugerida; como
   `colaborador@`, registrar o progresso.
8. **LGPD (UC-05):** Meus dados → pedir a cópia dos dados (PDF) → como `rh@`, atender a correção de nome da Carla em
   Solicitações LGPD.

### Revisões

| Data | O que foi revisado | Registro |
|------|--------------------|----------|
| 25/11/2026 | Protótipo inteiro, antes de mostrar à docente: casos de uso, telas, arquitetura, textos e dados de exemplo | [revisao-2026-11-25.md](revisao-2026-11-25.md) |

### Capturas

Capturas das telas implementadas em [`telas/`](telas), com o mesmo ID das telas do Figma (`TL-NN`), tiradas em
25/11/2026 com a empresa de exemplo.

| Tela | Caso de uso | Perfil | Captura |
|------|-------------|--------|---------|
| TL-01 | UC-01 Gerenciar competências e questionários | RH | [TL-01](telas/TL-01-gerenciar-competencias.png) |
| TL-02 | UC-02 Criar ciclo de avaliação | RH | [TL-02](telas/TL-02-criar-ciclo.png) |
| TL-03 | UC-03 Responder avaliação | Gestor | [TL-03](telas/TL-03-responder-avaliacao.png) |
| TL-04 | UC-04 Consultar resultados | Colaborador | [TL-04](telas/TL-04-consultar-resultados.png) |
| TL-05 | UC-05 Gerenciar consentimento | Colaborador | [TL-05](telas/TL-05-gerenciar-consentimento.png) |
| TL-06 | UC-06 Plano de desenvolvimento | Gestor | [TL-06](telas/TL-06-plano-desenvolvimento.png) |
| TL-07 | UC-07 Acompanhar andamento do ciclo | RH | [TL-07](telas/TL-07-andamento-ciclo.png) |
| TL-08 | UC-08 Registrar observações | Gestor | [TL-08](telas/TL-08-observacoes.png) |
| TL-09 | UC-09 Reabrir avaliação e moderar comentários | RH | [TL-09](telas/TL-09-reabrir-moderar.png) |
| TL-10 | UC-10 Autenticar-se | Todos | [TL-10](telas/TL-10-autenticar-se.png) |
