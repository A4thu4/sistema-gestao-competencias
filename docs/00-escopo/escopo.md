# ESC: Escopo do produto

Escopo do **Sistema de Gestão de Competências** conforme o enunciado do projeto da disciplina (1ª etapa, seções 2 e 3). É a entrada da etapa de elicitação no [processo de requisitos](../02-processo-requisitos/processo-requisitos.md) e aparece como fonte `ESC` na [matriz de rastreabilidade](../08-requisitos/matriz-rastreabilidade.md).

Responsável: Gabriel Araújo Caetano

## 1. Objetivo do produto

Sistema web para atividades de avaliação de competências, contemplando diferentes tipos de avaliação: chefe–colaborador, por pares e 360º, entre outros.

## 2. Funcionalidades mínimas esperadas

| ID | Funcionalidade | Descrição no enunciado |
|----|----------------|------------------------|
| `ESC §2.1` | Questionários personalizáveis | Questões adaptáveis às metas e competências específicas, evitando uma abordagem única para todos. |
| `ESC §2.2` | Múltiplas fontes de feedback | Reúne percepções de gestores, colegas, subordinados diretos e partes interessadas externas. |
| `ESC §2.3` | Análise robusta | Insights claros e baseados em dados, com tendências, números e recursos visuais de fácil leitura para apoiar a tomada de decisão. |
| `ESC §2.4` | Confidencialidade e anonimato | Incentivam o feedback honesto, protegendo a identidade dos respondentes, além da conformidade com a LGPD. |
| `ESC §2.5` | Acompanhamento contínuo | Evolução do progresso ao longo do tempo, com histórico dos feedbacks (não apenas avaliação pontual). |
| `ESC §2.6` | Planejamento de desenvolvimento | Apoio à definição de metas e ao acompanhamento das melhorias. |

## 3. Restrições

| ID | Restrição |
|----|-----------|
| `ESC §3.1` | Implementação em plataforma web, com perspectiva futura de funcionamento em dispositivos móveis. |
| `ESC §3.2` | Conformidade com a Lei Geral de Proteção de Dados (LGPD). |

## 4. Uso na rastreabilidade

A cobertura de cada item acima pelos requisitos está na [seção 4 da matriz](../08-requisitos/matriz-rastreabilidade.md#4-cobertura-do-escopo-do-enunciado). As partes interessadas externas (`ESC §2.2`) não apareceram nas entrevistas, nos questionários nem no sistema análogo; por isso o requisito `RF-34` tem o escopo como única origem e deve ser validado com o RH na 2ª etapa.
