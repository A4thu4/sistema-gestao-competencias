# Matriz de rastreabilidade (requisitos × fontes)

Liga cada requisito da [lista geral](lista-requisitos.md) às fontes que o originaram e às dores do [processo atual](../07-bpmn/dores.md) que ele resolve.

Responsável: José Eduardo

## Fontes

| ID | Fonte | Documento |
|----|-------|-----------|
| ENT-1 | Entrevista com Gerente de RH | [entrevista-1.md](../04-entrevistas/entrevista-1.md) |
| ENT-2 | Entrevista com Coordenador de TI (gestor) | [entrevista-2.md](../04-entrevistas/entrevista-2.md) |
| QUE-1 | Questionário: Analista de Marketing (colaboradora) | [resposta-1.md](../05-questionarios/resposta-1.md) |
| QUE-2 | Questionário: Supervisor de Operações (gestor) | [resposta-2.md](../05-questionarios/resposta-2.md) |
| ANA | Sistema análogo: SurveyMonkey | [funcionalidades.md](../06-sistema-analogo/funcionalidades.md) |
| DOC-1 | LGPD (Lei nº 13.709/2018) | [lgpd.md](../03-pesquisa-documental/lgpd.md) |
| DOC-2 | ISO/IEC 25010 | [iso-25010.md](../03-pesquisa-documental/iso-25010.md) |
| ESC | Escopo do produto definido no enunciado do projeto | [escopo.md](../00-escopo/escopo.md) |

## 1. Matriz requisitos × fontes

| Requisito | ENT-1 | ENT-2 | QUE-1 | QUE-2 | ANA | DOC-1 | DOC-2 | ESC | Dores do BPMN |
|-----------|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|---------------|
| RF-01 | X |  |  |  | X |  |  |  | D-05 |
| RF-02 | X |  |  |  | X |  |  |  | D-05 |
| RF-03 | X |  |  |  | X |  | X |  | - |
| RF-04 | X |  |  | X | X |  |  |  | D-07 |
| RF-05 | X |  | X |  | X |  |  |  | - |
| RF-06 |  |  |  | X |  |  |  |  | - |
| RF-07 | X |  |  |  | X |  |  |  | - |
| RF-08 | X | X | X | X |  |  |  |  | D-06 |
| RF-09 | X |  |  |  |  |  |  |  | D-04 |
| RF-10 |  | X |  |  |  |  |  |  | D-06 |
| RF-11 |  |  |  |  | X |  |  |  | - |
| RF-12 | X |  | X | X | X |  |  |  | D-04 |
| RF-13 | X |  |  | X |  |  |  |  | D-04 |
| RF-14 |  | X | X |  |  |  |  |  | - |
| RF-15 |  | X |  |  |  |  | X |  | D-02 |
| RF-16 |  | X |  |  |  |  | X |  | - |
| RF-17 |  | X |  |  |  |  |  |  | D-08 |
| RF-18 |  | X | X | X |  |  |  |  | D-08 |
| RF-19 | X |  |  |  | X |  | X |  | D-01, D-09 |
| RF-20 | X | X | X |  | X |  |  |  | D-09 |
| RF-21 | X | X |  | X | X |  |  |  | D-09 |
| RF-22 |  | X | X |  |  |  |  |  | - |
| RF-23 | X |  |  | X | X |  |  |  | - |
| RF-24 |  |  | X |  |  |  |  |  | D-11 |
| RF-25 |  |  | X | X |  |  |  |  | D-11 |
| RF-26 | X |  | X | X | X |  |  |  | D-08 |
| RF-27 | X | X | X | X | X |  |  |  | D-10 |
| RF-28 | X | X | X | X |  |  |  |  | D-10 |
| RF-29 | X |  | X | X |  | X |  |  | - |
| RF-30 |  |  |  |  |  | X |  |  | - |
| RF-31 |  | X |  |  |  |  |  |  | - |
| RF-32 |  | X |  |  |  |  |  |  | - |
| RF-33 | X | X |  |  | X | X |  |  | D-03 |
| RF-34 |  |  |  |  |  |  |  | X | D-06 |
| RNF-01 | X | X | X | X | X | X |  |  | D-06 |
| RNF-02 | X |  |  |  | X |  |  |  | - |
| RNF-03 | X | X |  |  |  | X | X |  | D-03 |
| RNF-04 | X |  |  |  |  | X |  |  | D-03 |
| RNF-05 | X | X |  |  |  | X |  |  | - |
| RNF-06 |  |  |  |  |  | X |  |  | - |
| RNF-07 | X | X | X | X |  |  | X |  | - |
| RNF-08 | X | X | X | X |  |  |  |  | D-12 |
| RNF-09 |  |  | X |  |  |  |  |  | - |
| RNF-10 |  |  |  |  | X |  |  |  | - |
| RNF-11 | X |  |  |  |  |  | X |  | - |
| RNF-12 |  |  |  |  |  |  | X |  | D-02 |
| RNF-13 | X |  |  |  |  |  | X |  | D-05 |
| **Total** | **28** | **20** | **17** | **16** | **18** | **8** | **9** | **1** | |

## 2. Cobertura das dores do processo atual

Todas as dores do BPMN têm ao menos um requisito correspondente.

| Dor | Requisitos que a resolvem |
|-----|---------------------------|
| D-01 | RF-19 |
| D-02 | RF-15, RNF-12 |
| D-03 | RF-33, RNF-03, RNF-04 |
| D-04 | RF-09, RF-12, RF-13 |
| D-05 | RF-01, RF-02, RNF-13 |
| D-06 | RF-08, RF-10, RF-34, RNF-01 |
| D-07 | RF-04 |
| D-08 | RF-17, RF-18, RF-26 |
| D-09 | RF-19, RF-20, RF-21 |
| D-10 | RF-27, RF-28 |
| D-11 | RF-24, RF-25 |
| D-12 | RNF-08 |

## 3. Origem detalhada

Itens das sínteses que deram origem a cada requisito, para rastrear até o trecho da fonte.

| Requisito | Itens de origem | Fontes |
|-----------|-----------------|--------|
| RF-01 | `SE-02`, `SA-02` | ENT-1, ANA |
| RF-02 | `SE-03`, `SA-02` | ENT-1, ANA |
| RF-03 | `SE-07`, `SA-01`, `SA-02`, `DOC-2 §2.5` | ENT-1, ANA, DOC-2 |
| RF-04 | `SE-07`, `SQ-15`, `SA-03` | ENT-1, QUE-2, ANA |
| RF-05 | `SE-07`, `SQ-04`, `SA-03` | ENT-1, QUE-1, ANA |
| RF-06 | `SQ-17` | QUE-2 |
| RF-07 | `SE-08`, `SA-01` | ENT-1, ANA |
| RF-08 | `SE-04`, `SQ-01` | ENT-1, ENT-2, QUE-1, QUE-2 |
| RF-09 | `SE-14` | ENT-1 |
| RF-10 | `SE-22` | ENT-2 |
| RF-11 | `SA-04` | ANA |
| RF-12 | `SE-14`, `SQ-08`, `SA-07` | ENT-1, QUE-1, QUE-2, ANA |
| RF-13 | `SE-14`, `SQ-09` | ENT-1, QUE-2 |
| RF-14 | `SE-26`, `SQ-12` | ENT-2, QUE-1 |
| RF-15 | `SE-27`, `DOC-2 §2.2` | ENT-2, DOC-2 |
| RF-16 | `SE-24`, `DOC-2 §2.4` | ENT-2, DOC-2 |
| RF-17 | `SE-20` | ENT-2 |
| RF-18 | `SE-21`, `SQ-13` | ENT-2, QUE-1, QUE-2 |
| RF-19 | `SE-01`, `SA-08`, `DOC-2 §2.1` | ENT-1, ANA, DOC-2 |
| RF-20 | `SE-09`, `SQ-04`, `SA-08`, `SA-10` | ENT-1, ENT-2, QUE-1, ANA |
| RF-21 | `SE-10`, `SQ-05`, `SA-09` | ENT-1, ENT-2, QUE-2, ANA |
| RF-22 | `SE-23`, `SQ-04` | ENT-2, QUE-1 |
| RF-23 | `SE-11`, `SQ-16`, `SA-11` | ENT-1, QUE-2, ANA |
| RF-24 | `SQ-14` | QUE-1 |
| RF-25 | `SQ-18`, `SQ-15` | QUE-1, QUE-2 |
| RF-26 | `SE-28`, `SQ-13`, `SA-13` | ENT-1, QUE-1, QUE-2, ANA |
| RF-27 | `SE-12`, `SQ-06`, `SA-12` | ENT-1, ENT-2, QUE-1, QUE-2, ANA |
| RF-28 | `SE-12`, `SQ-06` | ENT-1, ENT-2, QUE-1, QUE-2 |
| RF-29 | `SE-15`, `SQ-10`, `DOC-1 §2.1` | ENT-1, QUE-1, QUE-2, DOC-1 |
| RF-30 | `DOC-1 §2.1` | DOC-1 |
| RF-31 | `SE-25` | ENT-2 |
| RF-32 | `SE-25` | ENT-2 |
| RF-33 | `SE-13`, `SA-15`, `DOC-1 §2.3` | ENT-1, ENT-2, ANA, DOC-1 |
| RF-34 | `ESC §2.2` | ESC |
| RNF-01 | `SE-05`, `SQ-02`, `SA-05`, `DOC-1 §2.2` | ENT-1, ENT-2, QUE-1, QUE-2, ANA, DOC-1 |
| RNF-02 | `SE-06`, `SA-06` | ENT-1, ANA |
| RNF-03 | `SE-13`, `DOC-1 §2.3`, `DOC-2 §2.4` | ENT-1, ENT-2, DOC-1, DOC-2 |
| RNF-04 | `SE-16`, `DOC-1 §2.4` | ENT-1, DOC-1 |
| RNF-05 | `SE-16`, `SE-24`, `DOC-1 §2.4` | ENT-1, ENT-2, DOC-1 |
| RNF-06 | `DOC-1 §2.2` | DOC-1 |
| RNF-07 | `SE-17`, `SQ-03`, `DOC-2 §2.2` | ENT-1, ENT-2, QUE-1, QUE-2, DOC-2 |
| RNF-08 | `SE-19`, `SQ-07` | ENT-1, ENT-2, QUE-1, QUE-2 |
| RNF-09 | `SQ-11` | QUE-1 |
| RNF-10 | `SA-14` | ANA |
| RNF-11 | `SE-18`, `DOC-2 §2.3` | ENT-1, DOC-2 |
| RNF-12 | `DOC-2 §2.1` | DOC-2 |
| RNF-13 | `SE-02`, `DOC-2 §2.5` | ENT-1, DOC-2 |

## 4. Cobertura do escopo do enunciado

Confere se cada funcionalidade mínima e cada restrição do [escopo](../00-escopo/escopo.md) tem requisito. A coluna `ESC` da matriz só foi marcada no `RF-34`, que veio apenas do escopo; os outros itens do escopo apareceram também nas entrevistas, nos questionários ou nas outras fontes.

| Item do escopo | Requisitos que o atendem |
|----------------|--------------------------|
| `ESC §2.1` Questionários personalizáveis | RF-01, RF-02, RF-03, RF-04, RF-05, RF-06, RF-07, RNF-13 |
| `ESC §2.2` Múltiplas fontes de feedback | RF-08, RF-10, RF-11, RF-34 |
| `ESC §2.3` Análise robusta | RF-19, RF-20, RF-21, RF-22, RF-23, RF-26 |
| `ESC §2.4` Confidencialidade, anonimato e LGPD | RF-22, RF-29, RF-30, RNF-01, RNF-02, RNF-03, RNF-04, RNF-05, RNF-06 |
| `ESC §2.5` Acompanhamento contínuo | RF-17, RF-18, RF-26 |
| `ESC §2.6` Planejamento de desenvolvimento | RF-27, RF-28 |
| `ESC §3.1` Plataforma web, com perspectiva de uso em dispositivos móveis | R-01, RNF-07 |
| `ESC §3.2` Conformidade com a LGPD | R-02, RF-29, RF-30, RNF-01, RNF-04, RNF-05, RNF-06 |

## 5. Verificação

- [x] Todo requisito está ligado a pelo menos uma fonte (47 de 47).
- [x] Toda fonte originou pelo menos um requisito (8 de 8).
- [x] Todo item das sínteses (`SE`, `SQ`, `SA`) foi usado na lista (61 de 61).
- [x] Toda dor do BPMN está coberta (12 de 12).
- [x] Toda funcionalidade mínima e restrição do escopo está coberta (8 de 8).
- Requisitos com uma única fonte (14): RF-06, RF-09, RF-10, RF-11, RF-17, RF-24, RF-30, RF-31, RF-32, RF-34, RNF-06, RNF-09, RNF-10, RNF-12. Devem ser confirmados com os usuários na validação da 2ª etapa.
