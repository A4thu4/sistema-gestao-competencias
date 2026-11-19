# Sistema de Gestão de Competências

Sistema web para avaliação de competências (chefe–colaborador, por pares e 360º), com questionários personalizáveis, múltiplas fontes de feedback, análise de resultados, anonimato dos respondentes e acompanhamento contínuo.

Projeto da disciplina Arquitetura e Desenho de Software, PUC Goiás, 2026/2.

- **Etapa atual:** Parte III, Arquitetura e Desenho Detalhado (16/11 a 03/12; entrega do PDF e apresentação em 03 e 04/12). [Resumo do enunciado](docs/15-documento-arquitetura/enunciado-parte-iii.md)
- **Marco 4:** Modelagem e especificação, concluído; envio do link até 19/11
- **1ª etapa:** Levantamento de Requisitos, entregue em 21/10 com 47 requisitos rastreados
- **Sistema análogo escolhido:** SurveyMonkey
- **Kanban:** [GitHub Project](https://github.com/users/A4thu4/projects/6)

## Equipe

| Nome                    | Papel                  | Contribuições (issues)                                                                                   |
|-------------------------|------------------------|----------------------------------------------------------------------------------------------------------|
| Arthur Mamedes Borges   | Gerente de Projeto     | Estrutura do repositório (#1), Kanban (#2), roteiro e questionário (#6), revisão cruzada (#16), documentação final (#17); Marco 4: Documento Visão (#24), diagrama de casos de uso (#25), UC-05 (#30), matrizes de rastreabilidade (#34), revisão cruzada (#35), organização final (#36); Parte III: estrutura (#50), DImp, DCdu e DPac (#53), pacotes privacidade e compartilhado (#60), UC-09 e TL-09 (#72), revisão cruzada e Apêndice C (#64), cabeçalho, referências, apêndices e PDF (#65) |
| Gabriel Araújo Caetano  | Analista de Requisitos | Processo de requisitos (#3), entrevistas (#7), aplicação dos questionários (#9), sínteses das entrevistas e dos questionários (#11, #12); Marco 4: UC-03 (#28), casos de uso resumidos (#31); Parte III: introdução e stakeholders (#51), ambiente Web 4.1 a 4.3 (#61), UC-08 e TL-08 (#71), pacote avaliacoes e CO-02 (#58) |
| Lorenzo Oliveira Maciel | Analista de Requisitos | Pesquisa documental LGPD e ISO/IEC 25010 (#4), funcionalidades do SurveyMonkey (#10), síntese do sistema análogo (#13); Marco 4: UC-04 (#29); Parte III: requisitos não funcionais (#52), ambiente Web 4.4 e 4.5 (#67), UC-06 e TL-06 (#69), pacote resultados e CO-03 (#59), teste do protótipo (#63) |
| Rikelmy Andrade         | Analista de Requisitos | Modelagem BPMN e dores (#8), lista geral de requisitos (#14); Marco 4: protótipos das telas (#32); Parte III: BPMN proposto (#68), UC-10 e TL-10 (#73), ajustes das telas (#74), pacote identidade (#55), DDC e EOBD (#62) |
| José Eduardo            | Analista de Requisitos | Termo de consentimento (#5), matriz de rastreabilidade (#15); Marco 4: UC-01 e UC-02 (#26, #27), ERS IEEE 830 (#33); Parte III: DCD (#54), UC-07 e TL-07 (#70), pacotes competencias e ciclos, CO-01 (#56, #57) |

## Artefatos de entrega

| Entregável | Local | Responsável | Issue |
|------------|-------|-------------|-------|
| Escopo do produto (funcionalidades mínimas e restrições) | [docs/00-escopo](docs/00-escopo) | Grupo | — |
| Planejamento das atividades (Kanban) | [docs/01-planejamento](docs/01-planejamento) | Arthur Mamedes Borges | #2 |
| Processo de requisitos adotado | [docs/02-processo-requisitos](docs/02-processo-requisitos) | Gabriel Araújo Caetano | #3 |
| Pesquisa documental (leis e normas) | [docs/03-pesquisa-documental](docs/03-pesquisa-documental) | Lorenzo Oliveira Maciel | #4 |
| Entrevistas e síntese dos requisitos | [docs/04-entrevistas](docs/04-entrevistas) | Gabriel Araújo Caetano | #7, #11 |
| Questionários preenchidos e síntese dos requisitos | [docs/05-questionarios](docs/05-questionarios) | Gabriel Araújo Caetano | #9, #12 |
| Síntese dos requisitos do sistema análogo | [docs/06-sistema-analogo](docs/06-sistema-analogo) | Lorenzo Oliveira Maciel | #10, #13 |
| BPMN do processo atual e dores | [docs/07-bpmn](docs/07-bpmn) | Rikelmy Andrade | #8 |
| Lista geral de requisitos e matriz de rastreabilidade | [docs/08-requisitos](docs/08-requisitos) | Rikelmy Andrade, José Eduardo | #14, #15 |
| Revisão cruzada (validação) | [docs/09-revisao](docs/09-revisao) | Arthur Mamedes Borges | #16 |
| Termos de consentimento | [docs/anexos](docs/anexos) | José Eduardo | #5 |
| Documento Visão (Larman, cap. 7) | [docs/10-documento-visao](docs/10-documento-visao) | Arthur Mamedes Borges | #24 |
| Atores e diagrama de casos de uso (Larman, cap. 6) | [docs/11-casos-de-uso](docs/11-casos-de-uso) | Arthur Mamedes Borges | #25 |
| Casos de uso completos, 1 por integrante | [docs/11-casos-de-uso](docs/11-casos-de-uso) | José Eduardo (UC-01, UC-02), Gabriel Araújo Caetano (UC-03), Lorenzo Oliveira Maciel (UC-04), Arthur Mamedes Borges (UC-05) | #26 a #30 |
| Casos de uso resumidos | [docs/11-casos-de-uso](docs/11-casos-de-uso) | Gabriel Araújo Caetano | #31 |
| Protótipos das telas (Figma) | [docs/12-prototipos](docs/12-prototipos) | Rikelmy Andrade | #32 |
| Especificação de Requisitos (IEEE 830) | [docs/13-especificacao-requisitos](docs/13-especificacao-requisitos) | José Eduardo | #33 |
| Matriz de rastreabilidade vertical e horizontal | [docs/14-rastreabilidade](docs/14-rastreabilidade) | Arthur Mamedes Borges | #34 |
| Revisão cruzada do Marco 4 | [docs/09-revisao](docs/09-revisao) | Arthur Mamedes Borges | #35 |
| Versão para impressão do Marco 4 | [docs/impressao](docs/impressao) | Arthur Mamedes Borges | #36 |
| Documento de Arquitetura e Desenho Detalhado (Parte III, seções 1 a 5 e C4 adaptado) | [docs/15-documento-arquitetura](docs/15-documento-arquitetura) | Autor e revisor por item, na [tabela de autoria](docs/15-documento-arquitetura/README.md#autoria-e-revisão-dos-itens-de-informação) | #50 a #62, #64, #65, #67, #68 |
| Casos de uso completos UC-06 a UC-10 e telas TL-06 a TL-10 | [docs/11-casos-de-uso](docs/11-casos-de-uso), [docs/12-prototipos](docs/12-prototipos) | Lorenzo Oliveira Maciel, José Eduardo, Gabriel Araújo Caetano, Arthur Mamedes Borges, Rikelmy Andrade | #69 a #74 |
| ECBD (DCD e DDC) e EOBD | [seções 5.3.2 e 5.4.1](docs/16-projeto-banco-dados) | José Eduardo, Rikelmy Andrade | #54, #62 |
| PDF único da Parte III | [docs/impressao](docs/impressao) | Arthur Mamedes Borges | #65 |
| Protótipo do sistema (código, fora do PDF) | [prototipo](prototipo), [docs/17-prototipo](docs/17-prototipo) | Todos, um pacote por integrante | #55 a #60, #63 |

### Atalhos para impressão

- **Entrega completa (1ª etapa + Marco 4):** [versão para impressão em PDF](docs/impressao/entrega-completa.pdf)
- **Marco 4:** [Documento Visão](docs/10-documento-visao/documento-visao.md) · [casos de uso](docs/11-casos-de-uso/README.md) · [protótipos](docs/12-prototipos/README.md) · [ERS IEEE 830](docs/13-especificacao-requisitos/ers-ieee830.md) · [matriz vertical](docs/14-rastreabilidade/matriz-vertical.md) e [horizontal](docs/14-rastreabilidade/matriz-horizontal.md) · [revisão cruzada](docs/09-revisao/revisao-cruzada-marco4.md)
- **1ª etapa:** [versão entregue em 21/10](docs/impressao/entrega-etapa1.pdf)
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
| 1ª entrega    | 21/10 | Envio do link do repositório e artefatos impressos                            | Concluído |
| Marco 4.1     | 29/10 | Documento Visão e diagrama de casos de uso                                    | Concluído |
| Marco 4.2     | 05/11 | Casos de uso completos (1 por integrante) e resumidos                         | Concluído |
| Marco 4.3     | 12/11 | Protótipos no Figma, ERS IEEE 830 e matriz vertical/horizontal                | Concluído |
| Marco 4.4     | 19/11 | Revisão cruzada, versão para impressão e entrega do Marco 4                   | Concluído; envio do link até 19/11 |
| Marco 5.1     | 23/11 | Seções 1 a 4; DImp, BPMN proposto, DCdu, DPac; UC-06 a UC-10 com telas; DCD | Em andamento |
| Marco 5.2     | 29/11 | DCmp, DSeq e DCla por pacote; DDC e EOBD; DSeq de operação e contratos CO-01 a CO-03 | A fazer   |
| Marco 5.3     | 02/12 | Revisão cruzada (30/11 e 01/12), Apêndice C, referências, apêndices e PDF | A fazer   |
| Parte III     | 03/12 | Entrega do PDF impresso; apresentação em 03 e 04/12                            | A fazer   |
| Protótipo     | a confirmar | Implementação em Spring Boot e roteiro de demonstração                   | A fazer   |

## Convenções

**Commits:** `tipo: descrição`, por exemplo `docs: adiciona transcrição da entrevista 1` ou `feat: cadastro de competências`. Referencie a issue quando houver (`closes #7`).

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
| Preocupação de stakeholder (Arquitetura) | `PRE-NN` | `PRE-03` |
| Decisão de arquitetura  | `DA-NN`  | `DA-01`  |
| Item de informação (Parte III) | `IT-NN` | `IT-06` |
| Contrato de operação    | `CO-NN`  | `CO-01`  |

**Dados pessoais:** nome, função e telefone de entrevistados e respondentes ficam apenas nos arquivos de transcrição e de respostas, nunca em READMEs, issues ou mensagens de commit.
