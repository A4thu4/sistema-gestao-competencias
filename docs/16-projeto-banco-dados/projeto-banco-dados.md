# Projeto do Banco de Dados — Sistema de Gestão de Competências

> Substitua os trechos em _itálico_ e remova as dicas (`>`) ao concluir. Issue #54. Responsável: José Eduardo.

| Versão | Data | Autor | Alteração |
|--------|------|-------|-----------|
| 0.1 | 16/11/2026 | Arthur Mamedes Borges | Estrutura inicial |

## 1. Introdução

### 1.1 Contextualização

_O sistema, o que ele precisa guardar e por que o banco é relacional (PostgreSQL)._

### 1.2 Objetivo

### 1.3 Estrutura do documento

## 2. Stakeholders e interesses

| Stakeholder | Papel | Responsabilidades |
|-------------|-------|-------------------|
| Administrador de banco de dados (DBA) | _..._ | _Instalação, desempenho, cópias de segurança, permissões de acesso…_ |
| Administrador de dados (AD) | _..._ | _Padrões de nomes, modelo de dados, dicionário, qualidade dos dados…_ |
| Desenvolvedores | _..._ | _Migrações, consultas, mapeamento objeto-relacional…_ |
| Encarregado de dados (LGPD) | _..._ | _Retenção, anonimização, atendimento aos titulares…_ |
| _..._ | _..._ | _..._ |

## 3. Modelo de dados relacional

### 3.1 Introdução e modelo conceitual

> Ponto de partida: entidades e regras de dados levantadas na fase de requisitos, na [seção 3.4 da ERS](../13-especificacao-requisitos/ers-ieee830.md#34-requisitos-lógicos-de-banco-de-dados).

_Resumo do modelo conceitual e do que mudou no mapeamento._

### 3.2 Diagrama entidade-relacionamento

> Fonte em `diagramas/der.puml`.

![Diagrama entidade-relacionamento](diagramas/der.png)

### 3.3 Mapeamento e normalização

> Para cada entidade e relacionamento, mostre como a tabela atende à 1ª, 2ª e 3ª formas normais. Relacionamentos N:N viram tabelas associativas.

#### 3.3.1 _Entidade ou relacionamento_

| Forma normal | Verificação |
|--------------|-------------|
| 1FN | _Atributos atômicos, sem grupos repetidos…_ |
| 2FN | _Todo atributo não chave depende da chave inteira…_ |
| 3FN | _Nenhum atributo não chave depende de outro não chave…_ |

### 3.4 Estrutura das tabelas

> Um bloco por tabela. Os tipos são os do PostgreSQL e devem ser iguais aos das migrações em `prototipo/src/main/resources/db/migration/`.

#### Tabela `_nome_`

_Descrição da tabela._

| Atributo | Tipo | Nulo | Chave | Descrição |
|----------|------|------|-------|-----------|
| id | `bigint` | Não | PK | _..._ |

### 3.5 Regras de dados

_Como as regras 1 a 10 da ERS (seção 3.4) aparecem no modelo. Atenção à regra 4: a resposta de pares, subordinados e externos não guarda quem respondeu (`RNF-01`)._

## Anexo

- [Dicionário de dados](dicionario-dados.md)
