# Documento Visão — Sistema de Gestão de Competências

> Modelo baseado no capítulo 7 do Larman. Substitua os trechos em _itálico_ e remova as dicas (`>`) ao concluir.

| Versão | Data | Autor | Alteração |
|--------|------|-------|-----------|
| 0.1 | 22/10/2026 | Arthur Mamedes Borges | Estrutura inicial |

## 1. Introdução

_Uma ou duas frases sobre o que o sistema é e para quem. Ponto de partida: [escopo](../00-escopo/escopo.md)._

## 2. Posicionamento

### 2.1 Oportunidade de negócio

_Por que vale construir o sistema agora. Use as dores do [processo atual](../07-bpmn/dores.md) (`D-NN`)._

### 2.2 Enunciado do problema

| | |
|---|---|
| **O problema de** | _..._ |
| **afeta** | _..._ |
| **cujo impacto é** | _..._ |
| **uma boa solução seria** | _..._ |

### 2.3 Enunciado da posição do produto

| | |
|---|---|
| **Para** | _público-alvo_ |
| **que** | _necessidade_ |
| **o Sistema de Gestão de Competências é** | _categoria do produto_ |
| **que** | _benefício principal_ |
| **diferente de** | _alternativa atual (planilhas, formulários avulsos, SurveyMonkey)_ |
| **nosso produto** | _diferencial_ |

### 2.4 Alternativas e concorrência

_SurveyMonkey e processo manual atual: pontos fortes e fracos. Base: [síntese do sistema análogo](../06-sistema-analogo/sintese.md)._

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
