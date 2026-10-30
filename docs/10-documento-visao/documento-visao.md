# Documento Visão — Sistema de Gestão de Competências

> Modelo baseado no capítulo 7 do Larman. Substitua os trechos em _itálico_ e remova as dicas (`>`) ao concluir.

| Versão | Data | Autor | Alteração |
|--------|------|-------|-----------|
| 0.1 | 22/10/2026 | Arthur Mamedes Borges | Estrutura inicial |

## 1. Introdução

O Sistema de Gestão de Competências é uma aplicação web para planejar, aplicar e analisar avaliações de competências (chefe–colaborador, autoavaliação, pares, 360º e avaliadores externos) e acompanhar o desenvolvimento das pessoas ao longo do tempo. Ele substitui o processo atual, feito em planilhas enviadas por e-mail, e atende o RH, os gestores e os colaboradores da organização.

Este documento reúne o problema, as partes interessadas e as características de alto nível do produto, a partir dos artefatos da 1ª etapa: [escopo](../00-escopo/escopo.md), [dores do processo atual](../07-bpmn/dores.md) e [lista geral de requisitos](../08-requisitos/lista-requisitos.md).

## 2. Posicionamento

### 2.1 Oportunidade de negócio

Hoje a avaliação de competências é anual, feita em planilhas que circulam por e-mail, e somente o chefe avalia o colaborador (`D-06`). O RH leva cerca de três semanas para consolidar os resultados à mão (`D-01`), as planilhas voltam com erros e campos em branco (`D-02`) e não há controle de acesso sobre dados pessoais (`D-03`), o que conflita com a LGPD.

Para os gestores, o preenchimento é longo (`D-12`) e baseado na memória dos últimos meses (`D-08`). Para os colaboradores, os critérios são desconhecidos e o resultado chega semanas depois (`D-11`). Depois da conversa de feedback não há acompanhamento de metas (`D-10`), e a avaliação vira um evento pontual em vez de um processo contínuo.

Um sistema próprio elimina o retrabalho de consolidação, amplia as fontes de feedback com anonimato e transforma o resultado em plano de desenvolvimento acompanhado.

### 2.2 Enunciado do problema

| | |
|---|---|
| **O problema de** | avaliar competências com planilhas por e-mail, só com a visão do chefe e sem acompanhamento depois do feedback |
| **afeta** | o RH, que organiza e consolida o ciclo; os gestores, que avaliam suas equipes; e os colaboradores, que são avaliados |
| **cujo impacto é** | cerca de três semanas de consolidação manual, erros nos dados, exposição de dados pessoais, avaliações pouco confiáveis e nenhuma evolução registrada das pessoas |
| **uma boa solução seria** | um sistema web que aplique questionários por cargo com várias fontes de feedback anônimas, consolide os resultados automaticamente, guarde o histórico e acompanhe as metas de desenvolvimento, em conformidade com a LGPD |

### 2.3 Enunciado da posição do produto

| | |
|---|---|
| **Para** | organizações que avaliam as competências dos seus colaboradores |
| **que** | precisam de avaliações confiáveis, com várias fontes de feedback, e de acompanhar o desenvolvimento das pessoas |
| **o Sistema de Gestão de Competências é** | uma aplicação web de avaliação de competências e planejamento de desenvolvimento |
| **que** | organiza ciclos de avaliação por cargo, consolida os resultados automaticamente e mantém o histórico e as metas de cada pessoa |
| **diferente de** | planilhas por e-mail e ferramentas genéricas de pesquisa, como o SurveyMonkey |
| **nosso produto** | tem catálogo próprio de competências por cargo, anonimato garantido pelo sistema, visibilidade por perfil, plano de desenvolvimento com lembretes e termo de consentimento conforme a LGPD |

### 2.4 Alternativas e concorrência

| Alternativa | Pontos fortes | Pontos fracos |
|-------------|---------------|---------------|
| Processo atual (planilhas e e-mail) | Sem custo de ferramenta; conhecido por todos | Consolidação manual de cerca de três semanas, erros de preenchimento, sem anonimato, sem controle de acesso, sem histórico (`D-01` a `D-12`) |
| SurveyMonkey (sistema análogo) | Modelos 360º prontos, perguntas por relação com o avaliado, anonimato com mínimo de respostas, convites e lembretes, painéis e exportação ([síntese](../06-sistema-analogo/sintese.md)) | Ferramenta de pesquisa genérica: sem catálogo de competências por cargo, sem acompanhamento de metas com prazos, sem controle de acesso por perfil descrito e com parte das funções em planos pagos ([limitações](../06-sistema-analogo/funcionalidades.md#3-limitações-em-relação-ao-nosso-sistema)) |

## 3. Descrição das partes interessadas

### 3.1 Partes interessadas que não são usuários

| Parte interessada | Interesse |
|-------------------|-----------|
| _..._ | _..._ |

### 3.2 Usuários

| Usuário | Descrição | Responsabilidades no sistema |
|---------|-----------|------------------------------|
| _..._ | _..._ | _..._ |

### 3.3 Ambiente do usuário

_Onde e como os usuários acessam o sistema (dispositivos, frequência, restrições)._

### 3.4 Objetivos de alto nível e problemas das partes interessadas

| Objetivo de alto nível | Prioridade | Problemas e preocupações | Soluções atuais |
|------------------------|------------|--------------------------|-----------------|
| _..._ | Alta | _..._ | _..._ |

### 3.5 Objetivos no nível do usuário

> Estes objetivos são a base para identificar os casos de uso da [pasta 11](../11-casos-de-uso).

- **_Usuário_:** _..._

## 4. Visão geral do produto

### 4.1 Perspectiva do produto

_Como o sistema se relaciona com outros sistemas e serviços._

### 4.2 Resumo dos benefícios

| Característica de suporte | Benefício para a parte interessada |
|---------------------------|------------------------------------|
| _..._ | _..._ |

### 4.3 Custo e preço / licenciamento e instalação

_Fora do escopo acadêmico ou descrever premissas (SaaS, hospedagem em nuvem)._

## 5. Resumo das características do sistema

| ID | Característica | Requisitos relacionados |
|----|----------------|-------------------------|
| CAR-01 | _..._ | _..._ |

> Agrupar os requisitos da [lista geral](../08-requisitos/lista-requisitos.md) em características de alto nível.

## 6. Outros requisitos e restrições

- `R-01` Plataforma web, com perspectiva de aplicativo móvel.
- `R-02` Conformidade com a LGPD (Lei nº 13.709/2018).
- Qualidade (ISO/IEC 25010): _..._

## 7. Documentação e referências

- LARMAN, C. *Utilizando UML e Padrões*. Cap. 6 e 7.
- Artefatos da 1ª etapa deste repositório.
