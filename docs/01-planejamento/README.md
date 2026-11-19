# Planejamento

Planejamento das atividades da 1ª etapa e do Marco 4 em formato Kanban, mantido no GitHub Projects.

**Kanban:** [GitHub Project](https://github.com/users/A4thu4/projects/6)

Colunas: Backlog, A fazer, Em andamento, Em revisão, Concluído. Cada cartão é uma issue com responsável e marco (milestone).

| Arquivo            | Descrição                                          | Status    |
|--------------------|----------------------------------------------------|-----------|
| kanban-marco-1.png | Captura do quadro em 07/10                         | Concluído |
| kanban-marco-2.png | Captura do quadro em 13/10                         | Concluído |
| kanban-final.png   | Captura do quadro em 20/10 (versão para impressão) | Concluído |

Responsável: Arthur Mamedes Borges (issues #1 e #2)

## Quadro final (20/10)

Todas as 17 issues da 1ª etapa na coluna Concluído.

![Kanban em 20/10](kanban-final.png)

## Marco 4 (22/10 a 19/11)

Mesmo quadro e mesmas colunas. As issues #23 a #36 estão divididas em quatro milestones semanais, para o grupo ter pontos de controle como na 1ª etapa.

| Milestone | Prazo | Issues | Entrega |
|-----------|-------|--------|---------|
| Marco 4.1 | 29/10 | #23, #24, #25 | Estrutura, Documento Visão e diagrama de casos de uso |
| Marco 4.2 | 05/11 | #26 a #31 | Casos de uso completos (1 por integrante) e resumidos |
| Marco 4.3 | 12/11 | #32, #33, #34 | Protótipos, ERS IEEE 830 e matriz vertical/horizontal |
| Marco 4.4 | 19/11 | #35, #36 | Revisão cruzada, impressão e entrega |

Ordem de dependência: Visão → atores e diagrama → casos de uso → protótipos → ERS e matriz → revisão.

Responsáveis por issue: ver a tabela de artefatos no [README principal](../../README.md#artefatos-de-entrega).

O andamento das issues #23 a #36 pode ser conferido no [quadro do GitHub Project](https://github.com/users/A4thu4/projects/6).

## Marco 5 (16/11 a 07/12)

Terceira parte do trabalho: Documento de Arquitetura (ISO/IEC/IEEE 42010 simplificada e C4), Projeto do Banco de Dados e protótipo do sistema. Mesmo quadro e mesmas colunas. As issues #50 a #65 estão em três milestones semanais.

| Milestone | Prazo | Issues | Entrega |
|-----------|-------|--------|---------|
| Marco 5.1 | 23/11 | #50 a #55 | Estrutura; introdução, stakeholders, RNF, C1 e C2; modelo relacional; pacote identidade (login e layout) |
| Marco 5.2 | 30/11 | #56 a #62 | Pacotes UC-01 a UC-05 com C3 e C4; coesão, acoplamento e padrões; dicionário de dados |
| Marco 5.3 | 07/12 | #63, #64, #65 | Teste ponta a ponta, revisão cruzada, impressão e entrega |

**Divisão:** cada integrante implementa o caso de uso que escreveu no Marco 4 e documenta o C3 e o C4 do pacote correspondente.

| Integrante | Documentação | Protótipo |
|------------|--------------|-----------|
| Arthur Mamedes Borges | C1 e C2, estilo e decisões (#53); revisão e impressão (#64, #65) | `privacidade` e auditoria, UC-05 (#60) |
| Gabriel Araújo Caetano | Introdução e stakeholders (#51); coesão, acoplamento e padrões (#61) | `avaliacoes`, UC-03 (#58) |
| Lorenzo Oliveira Maciel | Requisitos não funcionais (#52); roteiro de demonstração (#63) | `resultados`, UC-04 (#59) |
| Rikelmy Andrade | Dicionário de dados (#62) | `identidade`: login, perfis e layout a partir do Figma (#55) |
| José Eduardo | Projeto do Banco de Dados: modelo relacional (#54) | `competencias` e `ciclos`, UC-01 e UC-02 (#56, #57) |

Ordem de dependência: modelo relacional (#54) e pacote identidade (#55) → competencias → ciclos → avaliacoes → resultados; privacidade em paralelo depois do login. A documentação de C3 e C4 acompanha o código de cada pacote.
