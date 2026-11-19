# Projeto do Banco de Dados

Na Parte III, o projeto do banco faz parte do [Documento de Arquitetura](../15-documento-arquitetura/README.md), nos itens que o enunciado pede:

| Artefato do enunciado | Onde está | Autor | Revisor | Issue |
|-----------------------|-----------|-------|---------|-------|
| ECBD: Diagrama de Classes de Dados (DCD, sem operações) | [Seção 5.3.2.1](../15-documento-arquitetura/secoes/05-3-nivel-componentes.md#5321-diagrama-de-classes-de-dados-dcd) | José Eduardo | Rikelmy Andrade | #54 |
| ECBD: Dicionário de Dados Conceituais (DDC) | [Seção 5.3.2.2](../15-documento-arquitetura/secoes/05-3-nivel-componentes.md#5322-dicionário-de-dados-conceituais-ddc) | Rikelmy Andrade | José Eduardo | #62 |
| Esquema Operacional de Banco de Dados (EOBD) | [Seção 5.4.1](../15-documento-arquitetura/secoes/05-4-nivel-codigo.md#541-esquema-operacional-de-banco-de-dados-eobd) | Rikelmy Andrade | José Eduardo | #62 |

O antigo "Projeto do Banco de Dados" (stakeholders do banco, normalização forma a forma) saiu porque não é pedido pelo enunciado da Parte III (critério de essencialidade). A justificativa da normalização cabe em uma frase no EOBD. As fontes dos diagramas ficam em [`docs/15-documento-arquitetura/diagramas`](../15-documento-arquitetura/diagramas).

**Entradas:** [ERS, seção 3.4](../13-especificacao-requisitos/ers-ieee830.md#34-requisitos-lógicos-de-banco-de-dados) (entidades e regras de dados), [casos de uso](../11-casos-de-uso).

**Ordem:** DCD (#54, Marco 5.1) → migrações de cada pacote no protótipo → DDC e EOBD conferidos contra as migrações (#62, Marco 5.2).

## Convenções

- Tabelas e colunas em `snake_case`, no singular (`competencia`, `ciclo_avaliacao`).
- Chave primária `id bigint generated always as identity`; chave estrangeira `<tabela>_id`.
- Datas em `timestamp with time zone`; situações como `varchar` com `check` dos valores permitidos.
- O EOBD informa tipo **e tamanho** de cada coluna (por exemplo, `varchar(120)`).
