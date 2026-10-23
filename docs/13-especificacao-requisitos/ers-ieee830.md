# Especificação de Requisitos de Software — Sistema de Gestão de Competências

> Estrutura da IEEE 830 (organização dos requisitos específicos por característica do sistema). Substitua os trechos em _itálico_.

| Versão | Data | Autor | Alteração |
|--------|------|-------|-----------|
| 0.1 | 22/10/2026 | Arthur Mamedes Borges | Estrutura inicial |

## 1. Introdução

### 1.1 Propósito
_Objetivo deste documento e público-alvo (equipe, professor, futuros desenvolvedores)._

### 1.2 Escopo
_Nome do produto, o que faz e o que não faz, benefícios. Resumir do [Documento Visão](../10-documento-visao/documento-visao.md)._

### 1.3 Definições, acrônimos e abreviações

| Termo | Definição |
|-------|-----------|
| Ciclo de avaliação | _..._ |
| Avaliação 360º | _..._ |
| Competência | _..._ |
| LGPD | Lei Geral de Proteção de Dados (Lei nº 13.709/2018) |
| RF / RNF | Requisito funcional / não funcional |

### 1.4 Referências
_LGPD, ISO/IEC 25010, Larman, artefatos da 1ª etapa._

### 1.5 Visão geral do documento
_Como o restante está organizado._

## 2. Descrição geral

### 2.1 Perspectiva do produto
_Contexto do sistema e interfaces com outros sistemas._

### 2.2 Funções do produto
_Resumo das funções por característica (`CAR-NN`) e diagrama de casos de uso._

### 2.3 Características dos usuários
_Perfis de usuário: nível técnico e frequência de uso._

### 2.4 Restrições
_`R-01` (web, futuro móvel) e `R-02` (LGPD)._

### 2.5 Suposições e dependências
_..._

## 3. Requisitos específicos

### 3.1 Interfaces externas
- **3.1.1 Interfaces de usuário:** _ver [protótipos](../12-prototipos)._
- **3.1.2 Interfaces de hardware:** _..._
- **3.1.3 Interfaces de software:** _..._
- **3.1.4 Interfaces de comunicação:** _HTTPS/TLS 1.2+ (`RNF-04`)._

### 3.2 Requisitos funcionais

> Um bloco por característica do Documento Visão. Para cada requisito: ID, descrição, prioridade, entradas, processamento, saídas e caso de uso.

#### 3.2.1 CAR-NN _Nome da característica_
| ID | Descrição | Prioridade | Caso de uso |
|----|-----------|------------|-------------|
| RF-NN | _..._ | _..._ | _..._ |

### 3.3 Requisitos de desempenho
_..._

### 3.4 Requisitos lógicos de banco de dados
_Entidades principais e regras de retenção de dados (LGPD)._

### 3.5 Restrições de projeto
_Padrões e conformidade: LGPD, ISO/IEC 25010._

### 3.6 Atributos do sistema
_Requisitos não funcionais agrupados por atributo de qualidade (segurança, usabilidade, confiabilidade etc.)._

## 4. Apêndices
- [Matriz de rastreabilidade vertical](../14-rastreabilidade/matriz-vertical.md) e [horizontal](../14-rastreabilidade/matriz-horizontal.md)
