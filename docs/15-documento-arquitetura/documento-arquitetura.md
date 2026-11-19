# Documento de Arquitetura — Sistema de Gestão de Competências

> Modelo simplificado da ISO/IEC/IEEE 42010, com as visões organizadas pelo modelo C4, no formato pedido no enunciado da terceira parte. Substitua os trechos em _itálico_ e remova as dicas (`>`) ao concluir.

| Versão | Data | Autor | Alteração |
|--------|------|-------|-----------|
| 0.1 | 16/11/2026 | Arthur Mamedes Borges | Estrutura inicial |

## 1. Introdução

> Issue #51. Responsável: Gabriel Araújo Caetano.

### 1.1 Visão geral do documento

_O que este documento descreve, para quem e como se relaciona com o [Documento Visão](../10-documento-visao/documento-visao.md) e a [ERS](../13-especificacao-requisitos/ers-ieee830.md)._

### 1.2 Interesses do sistema e informações suplementares

_Interesses do sistema (propósito, LGPD, perspectiva de aplicativo móvel) e informações definidas pelo projeto ou pela organização: disciplina, equipe, prazos, stack adotada no protótipo, ferramentas de modelagem (PlantUML e C4-PlantUML)._

### 1.3 Estrutura do documento

_Uma linha por seção deste documento._

## 2. Stakeholders e preocupações

> Issue #51. Base: seção 3 do [Documento Visão](../10-documento-visao/documento-visao.md) e seção 2.3 da [ERS](../13-especificacao-requisitos/ers-ieee830.md). Cada preocupação recebe um ID `PRE-NN`, citado nas seções 3 e 4.

| Stakeholder | Tipo | Preocupações sobre o sistema | ID |
|-------------|------|------------------------------|----|
| RH | Usuário | _..._ | `PRE-01` |
| Gestor | Usuário | _..._ | `PRE-NN` |
| Colaborador | Usuário | _..._ | `PRE-NN` |
| Avaliador externo | Usuário | _..._ | `PRE-NN` |
| _Equipe de TI da empresa_ | Operador | _..._ | `PRE-NN` |
| _Diretoria da empresa_ | Adquirente / proprietário | _..._ | `PRE-NN` |
| _Provedor de e-mail e de SSO_ | Fornecedor | _..._ | `PRE-NN` |
| Equipe do projeto | Desenvolvedor | _..._ | `PRE-NN` |
| Encarregado de dados (LGPD) | Regulatório | _..._ | `PRE-NN` |

## 3. Requisitos não funcionais e como são atendidos

> Issue #52. Responsável: Lorenzo Oliveira Maciel. Base: [lista de requisitos](../08-requisitos/lista-requisitos.md), [ISO/IEC 25010](../03-pesquisa-documental/iso-25010.md) e seção 3.6 da [ERS](../13-especificacao-requisitos/ers-ieee830.md).

| RNF | Descrição | Característica ISO/IEC 25010 | Como a arquitetura atende | Pacote | Preocupações | Situação no protótipo |
|-----|-----------|------------------------------|---------------------------|--------|--------------|------------------------|
| RNF-01 | _..._ | _Segurança (confidencialidade)_ | _decisão `DA-NN`_ | _..._ | `PRE-NN` | _Atendido / parcial / fora do protótipo, com motivo_ |

## 4. Modelo C4

### 4.1 Estrutura geral

> Issue #53. Responsável: Arthur Mamedes Borges.

_Como os quatro níveis foram usados neste trabalho. Seguimos a numeração do enunciado:_

| Nível | Neste documento | Seção |
|-------|-----------------|-------|
| C1 | Contexto do sistema | 4.2 |
| C2 | Diagrama de pacotes e estilo arquitetural | 4.3 |
| C3 | Diagrama de componentes de cada pacote | 4.4 |
| C4 | Diagrama de classes com métodos | 4.5 |

### 4.2 C1 — Contexto do sistema

> Fonte em `diagramas/c1-contexto.puml` (C4-PlantUML). Atores do [diagrama de casos de uso](../11-casos-de-uso/README.md#atores) e sistemas externos (serviço de e-mail, provedor de identidade).

![C1 — contexto](diagramas/c1-contexto.png)

_Descrição do diagrama._

### 4.3 C2 — Diagrama de pacotes e estilo arquitetural

> Fonte em `diagramas/c2-pacotes.puml`. Os pacotes do diagrama são os mesmos do código em `prototipo/src/main/java/br/edu/pucgoias/sgc/`.

![C2 — pacotes](diagramas/c2-pacotes.png)

#### 4.3.1 Estilo arquitetural

_Monolito modular em camadas: o que é, por que foi escolhido e como aparece no código (pacote por módulo de negócio; dentro de cada um, `web`, `servico`, `dominio` e `repositorio`)._

#### 4.3.2 Preocupações dos stakeholders atendidas

_Quais `PRE-NN` cada pacote ou decisão atende._

#### 4.3.3 Proposta do sistema

#### 4.3.4 Adequação da arquitetura para alcançar os objetivos

#### 4.3.5 Características de construção e funcionamento

_Stack, execução local (Docker Compose para o PostgreSQL), migrações Flyway, renderização no servidor com Thymeleaf._

#### 4.3.6 Riscos e impactos potenciais

| Risco | Impacto | Mitigação |
|-------|---------|-----------|
| _..._ | _..._ | _..._ |

#### 4.3.7 Manutenibilidade e evolução

_Inclui o caminho para o aplicativo móvel (`RNF-07`): expor a camada de serviço por uma API REST sem mudar o domínio._

#### 4.3.8 Raciocínio da arquitetura

#### 4.3.9 Decisões tomadas

| ID | Decisão | Alternativas consideradas | Motivo | RNF / preocupações |
|----|---------|---------------------------|--------|--------------------|
| DA-01 | Monolito modular em camadas | Microsserviços; API REST + SPA | _..._ | _..._ |
| DA-02 | Java 21 + Spring Boot 3 | FastAPI; Node.js | _..._ | _..._ |
| DA-03 | Renderização no servidor (Thymeleaf + Bootstrap) | React/Angular | _..._ | `RNF-07` |
| DA-04 | PostgreSQL com migrações Flyway | MySQL; geração automática pelo JPA | _..._ | `RNF-12` |
| DA-NN | _..._ | _..._ | _..._ | _..._ |

### 4.4 C3 — Diagrama de componentes de cada pacote

> Cada responsável documenta o pacote que implementa (issues #55 a #60). Fontes em `diagramas/c3-<pacote>.puml` e `diagramas/seq-<pacote>-<funcao>.puml`.

#### 4.4.1 Pacote `identidade` (Rikelmy Andrade, #55)

![C3 — identidade](diagramas/c3-identidade.png)

_Componentes e responsabilidades._

**Diagrama de sequência — _comunicação importante_**

![Sequência](diagramas/seq-identidade-login.png)

#### 4.4.2 Pacote `competencias` (José Eduardo, #56)

#### 4.4.3 Pacote `ciclos` (José Eduardo, #57)

#### 4.4.4 Pacote `avaliacoes` (Gabriel Araújo Caetano, #58)

#### 4.4.5 Pacote `resultados` (Lorenzo Oliveira Maciel, #59)

#### 4.4.6 Pacote `privacidade` (Arthur Mamedes Borges, #60)

### 4.5 C4 — Diagrama de classes com métodos

> Um diagrama por pacote (`diagramas/c4-<pacote>.puml`), com o nome dos métodos, e um diagrama de sequência para cada função principal do sistema.

#### 4.5.1 Pacote `identidade`

#### 4.5.2 Pacote `competencias`

#### 4.5.3 Pacote `ciclos`

#### 4.5.4 Pacote `avaliacoes`

#### 4.5.5 Pacote `resultados`

#### 4.5.6 Pacote `privacidade`

#### 4.5.7 Diagramas de sequência das principais funções

| Função | Caso de uso | Diagrama |
|--------|-------------|----------|
| Criar e publicar questionário | UC-01 | _..._ |
| Criar ciclo de avaliação | UC-02 | _..._ |
| Responder e enviar avaliação | UC-03 | _..._ |
| Consolidar e consultar resultados | UC-04 | _..._ |
| Aceitar termo de consentimento | UC-05 | _..._ |

### 4.6 Coesão, acoplamento e padrões de projeto

> Issue #61. Responsável: Gabriel Araújo Caetano.

#### 4.6.1 Coesão e acoplamento

_Tipo de coesão adotado (por exemplo, funcional dentro de cada pacote) e tipo de acoplamento (por dados e por interfaces), com exemplos do código._

#### 4.6.2 Regras de dependência entre pacotes

| Pacote | Pode depender de | Não pode depender de |
|--------|------------------|----------------------|
| _..._ | _..._ | _..._ |

#### 4.6.3 Padrões de projeto

| Padrão | Onde está | Problema que resolve |
|--------|-----------|----------------------|
| _Strategy_ | _`resultados`: cálculo por grupo de avaliadores_ | _..._ |
| _Observer_ | _`compartilhado`: log de auditoria por eventos_ | _..._ |
| _Repository_ | _todos os pacotes_ | _..._ |

## 5. Referências

- ISO/IEC/IEEE 42010:2022, *Software, systems and enterprise — Architecture description*.
- Simon Brown, *The C4 model for visualising software architecture* (c4model.com).
- Craig Larman, *Utilizando UML e Padrões*.
- _..._
