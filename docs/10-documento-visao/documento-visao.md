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
| Diretoria da organização | Decisões de pessoas (promoção, treinamento, sucessão) baseadas em dados confiáveis e comparáveis entre equipes |
| Encarregado de dados (DPO) | Tratamento de dados pessoais conforme a LGPD: consentimento, necessidade, direitos do titular e registro de acessos |
| Equipe de TI | Sistema seguro, configurável pelo RH sem alteração de código e com login corporativo quando disponível |

### 3.2 Usuários

| Usuário | Descrição | Responsabilidades no sistema |
|---------|-----------|------------------------------|
| RH (administrador) | Equipe de Recursos Humanos que organiza o processo de avaliação (`ENT-1`) | Manter o catálogo de competências e os questionários; criar e acompanhar ciclos; liberar resultados; moderar comentários e reabrir avaliações |
| Gestor | Líder de equipe que avalia os subordinados e também é avaliado (`ENT-2`, `QUE-2`) | Avaliar a equipe; registrar observações ao longo do ano; acompanhar o ciclo e os resultados da equipe; conduzir o plano de desenvolvimento |
| Colaborador | Pessoa avaliada, que também avalia colegas e o próprio líder (`QUE-1`) | Fazer a autoavaliação; avaliar pares e o líder; consultar os próprios resultados; registrar o progresso das metas |
| Avaliador externo | Cliente, fornecedor ou parceiro convidado para um ciclo (`ESC §2.2`, `RF-34`) | Responder à avaliação por link individual, sem conta no sistema |

### 3.3 Ambiente do usuário

- Acesso pelo navegador, em computador, tablet ou celular (`RNF-07`); um aplicativo móvel é perspectiva futura (`R-01`).
- Ciclos de avaliação periódicos (hoje anuais), com picos de acesso de toda a organização perto do encerramento (`RNF-11`).
- Avaliadores sem treinamento: avaliar uma pessoa deve levar até 10 minutos e a autoavaliação até 15 minutos (`RNF-08`).
- O preenchimento pode ser interrompido e retomado depois (`RF-14`).

### 3.4 Objetivos de alto nível e problemas das partes interessadas

| Objetivo de alto nível | Prioridade | Problemas e preocupações | Soluções atuais |
|------------------------|------------|--------------------------|-----------------|
| Reduzir o tempo de fechamento do ciclo | Alta | Consolidação manual de cerca de três semanas e planilhas com erros (`D-01`, `D-02`) | RH junta as planilhas e confere uma a uma |
| Obter feedback de várias fontes com honestidade | Alta | Só o chefe avalia; sem anonimato as pessoas não são francas (`D-06`) | Nenhuma |
| Proteger os dados pessoais | Alta | Planilhas encaminhadas sem controle de acesso (`D-03`) | Envio por e-mail |
| Tornar a avaliação justa e compreensível | Média | Escala interpretada de formas diferentes; colaborador não conhece os critérios (`D-07`, `D-11`) | Orientação verbal dos gestores |
| Acompanhar a evolução das pessoas | Alta | Avaliação baseada na memória recente, sem histórico nem metas acompanhadas (`D-08`, `D-10`) | Anotações pessoais dos gestores |
| Apoiar decisões com dados | Média | Sem painel por pessoa ou equipe nem comparação entre avaliações (`D-09`) | Planilha geral montada à mão |

### 3.5 Objetivos no nível do usuário

Estes objetivos são a base para os [casos de uso](../11-casos-de-uso).

- **RH:** manter competências e questionários por cargo; criar ciclos de avaliação; acompanhar o andamento e cobrar pendências; liberar resultados; reabrir avaliações e moderar comentários; gerar relatórios.
- **Gestor:** avaliar a equipe; registrar observações ao longo do ano; acompanhar o andamento da equipe no ciclo; consultar e exportar resultados da equipe; definir e acompanhar planos de desenvolvimento.
- **Colaborador:** consultar critérios e escala; fazer a autoavaliação; avaliar pares e o líder; consultar os próprios resultados e a evolução; registrar o progresso das metas.
- **Avaliador externo:** responder à avaliação para a qual foi convidado.
- **Todos os usuários:** acessar o sistema com segurança; aceitar o termo de consentimento; consultar e exercer os direitos sobre os próprios dados.

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
