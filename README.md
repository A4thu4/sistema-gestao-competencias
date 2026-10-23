# Sistema de Gestão de Competências

Sistema web para avaliação de competências (chefe–colaborador, por pares e 360º), com questionários personalizáveis, múltiplas fontes de feedback, análise de resultados, anonimato dos respondentes e acompanhamento contínuo.

Projeto da disciplina Arquitetura e Desenho de Software, PUC Goiás, 2026/2.

- **Etapa atual:** Marco 4, Modelagem e especificação (22/10 a 19/11)
- **1ª etapa:** Levantamento de Requisitos, entregue em 21/10 com 47 requisitos rastreados
- **Sistema análogo escolhido:** SurveyMonkey
- **Kanban:** [GitHub Project](https://github.com/users/A4thu4/projects/6)

## Equipe

| Nome                    | Papel                  | Contribuições (issues)                                                                                   |
|-------------------------|------------------------|----------------------------------------------------------------------------------------------------------|
| Arthur Mamedes Borges   | Gerente de Projeto     | Estrutura do repositório (#1), Kanban (#2), roteiro e questionário (#6), revisão cruzada (#16), documentação final (#17) |
| Gabriel Araújo Caetano  | Analista de Requisitos | Processo de requisitos (#3), entrevistas (#7), aplicação dos questionários (#9), sínteses das entrevistas e dos questionários (#11, #12) |
| Lorenzo Oliveira Maciel | Analista de Requisitos | Pesquisa documental LGPD e ISO/IEC 25010 (#4), funcionalidades do SurveyMonkey (#10), síntese do sistema análogo (#13) |
| Rikelmy Andrade         | Analista de Requisitos | Modelagem BPMN e dores (#8), lista geral de requisitos (#14)                                             |
| José Eduardo            | Analista de Requisitos | Termo de consentimento (#5), matriz de rastreabilidade (#15)                                             |

## Artefatos da 1ª etapa

| Entregável                                            | Local                                                      |
|-------------------------------------------------------|------------------------------------------------------------|
| Escopo do produto (funcionalidades mínimas e restrições) | [docs/00-escopo](docs/00-escopo)                         |
| Planejamento das atividades (Kanban)                  | [docs/01-planejamento](docs/01-planejamento)               |
| Processo de requisitos adotado                        | [docs/02-processo-requisitos](docs/02-processo-requisitos) |
| Pesquisa documental (leis e normas)                   | [docs/03-pesquisa-documental](docs/03-pesquisa-documental) |
| Entrevistas e síntese dos requisitos                  | [docs/04-entrevistas](docs/04-entrevistas)                 |
| Questionários preenchidos e síntese dos requisitos    | [docs/05-questionarios](docs/05-questionarios)             |
| Síntese dos requisitos do sistema análogo             | [docs/06-sistema-analogo](docs/06-sistema-analogo)         |
| BPMN do processo atual e dores                        | [docs/07-bpmn](docs/07-bpmn)                               |
| Lista geral de requisitos e matriz de rastreabilidade | [docs/08-requisitos](docs/08-requisitos)                   |
| Revisão cruzada (validação)                           | [docs/09-revisao](docs/09-revisao)                         |
| Termos de consentimento                               | [docs/anexos](docs/anexos)                                 |

## Artefatos do Marco 4 (entrega em 19/11)

| Entregável | Local | Responsável | Issue |
|------------|-------|-------------|-------|
| Documento Visão (Larman, cap. 7) | [docs/10-documento-visao](docs/10-documento-visao) | Arthur Mamedes Borges | #24 |
| Atores e diagrama de casos de uso (Larman, cap. 6) | [docs/11-casos-de-uso](docs/11-casos-de-uso) | Arthur Mamedes Borges | #25 |
| Casos de uso completos, 1 por integrante | [docs/11-casos-de-uso](docs/11-casos-de-uso) | A definir | #26 a #30 |
| Casos de uso resumidos | [docs/11-casos-de-uso](docs/11-casos-de-uso) | A definir | #31 |
| Protótipos das telas (Figma) | [docs/12-prototipos](docs/12-prototipos) | A definir | #32 |
| Especificação de Requisitos (IEEE 830) | [docs/13-especificacao-requisitos](docs/13-especificacao-requisitos) | A definir | #33 |
| Matriz de rastreabilidade vertical e horizontal | [docs/14-rastreabilidade](docs/14-rastreabilidade) | A definir | #34 |

### Atalhos para impressão (1ª etapa)

- [Versão para impressão em PDF](docs/impressao/entrega-etapa1.pdf) (todos os artefatos em um arquivo só)
- [Lista geral de requisitos](docs/08-requisitos/lista-requisitos.md)
- [Matriz de rastreabilidade](docs/08-requisitos/matriz-rastreabilidade.md)
- [Síntese das entrevistas](docs/04-entrevistas/sintese.md) · [Síntese dos questionários](docs/05-questionarios/sintese.md) · [Síntese do sistema análogo](docs/06-sistema-analogo/sintese.md)
- [BPMN do processo atual](docs/07-bpmn/processo-atual.png) e [dores](docs/07-bpmn/dores.md)
- [Revisão cruzada e pendências](docs/09-revisao/revisao-cruzada.md)

## Cronograma

| Marco         | Data  | Entrega                                                                       | Status    |
|---------------|-------|-------------------------------------------------------------------------------|-----------|
| Marco 1       | 07/10 | Kanban e processo de requisitos publicados; sistema análogo registrado        | Concluído |
| Marco 2       | 14/10 | Entrevistas e questionários concluídos (versão preliminar); BPMN em rascunho  | Concluído |
| Marco 3       | 20/10 | Todos os artefatos versionados e revisados                                    | Concluído |
| Entrega final | 21/10 | Envio do link do repositório e artefatos impressos                            | Concluído |
| Marco 4.1     | 29/10 | Documento Visão e diagrama de casos de uso                                    | Em andamento |
| Marco 4.2     | 05/11 | Casos de uso completos (1 por integrante) e resumidos                         | A fazer   |
| Marco 4.3     | 12/11 | Protótipos no Figma, ERS IEEE 830 e matriz vertical/horizontal                | A fazer   |
| Marco 4.4     | 19/11 | Revisão cruzada, versão para impressão e entrega do Marco 4                   | A fazer   |

## Convenções

**Commits:** `tipo: descrição`, por exemplo `docs: adiciona transcrição da entrevista 1`. Referencie a issue quando houver (`closes #7`).

**Identificadores:**

| Tipo                    | Padrão   | Exemplo  |
|-------------------------|----------|----------|
| Requisito funcional     | `RF-NN`  | `RF-01`  |
| Requisito não funcional | `RNF-NN` | `RNF-01` |
| Entrevista              | `ENT-N`  | `ENT-1`  |
| Questionário            | `QUE-N`  | `QUE-1`  |
| Sistema análogo         | `ANA`    | `ANA`    |
| Item de síntese         | `SE-NN`, `SQ-NN`, `SA-NN` | `SE-01` |
| Pesquisa documental     | `DOC-N`  | `DOC-1`  |
| Escopo do enunciado     | `ESC §N.N` | `ESC §2.2` |
| Característica (Visão)  | `CAR-NN` | `CAR-01` |
| Caso de uso             | `UC-NN`  | `UC-03`  |
| Tela do protótipo       | `TL-NN`  | `TL-08`  |

**Dados pessoais:** nome, função e telefone de entrevistados e respondentes ficam apenas nos arquivos de transcrição e de respostas, nunca em READMEs, issues ou mensagens de commit.
