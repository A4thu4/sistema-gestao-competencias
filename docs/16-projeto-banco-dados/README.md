# Projeto do Banco de Dados

Mapeamento do modelo conceitual da fase de requisitos para o modelo relacional normalizado (até a 3ª forma normal), estrutura das tabelas e dicionário de dados. O modelo é a base das migrações Flyway do [protótipo](../../prototipo).

| Arquivo | Descrição | Responsável | Issue | Status |
|---------|-----------|-------------|-------|--------|
| [projeto-banco-dados.md](projeto-banco-dados.md) | Introdução, stakeholders, modelo relacional, normalização e estrutura das tabelas | José Eduardo | #54 | A fazer |
| diagramas/der.puml e der.png | Diagrama entidade-relacionamento | José Eduardo | #54 | A fazer |
| [dicionario-dados.md](dicionario-dados.md) | Anexo: dicionário de dados | Rikelmy Andrade | #62 | A fazer |

**Entradas:** [ERS, seção 3.4](../13-especificacao-requisitos/ers-ieee830.md#34-requisitos-lógicos-de-banco-de-dados) (entidades e regras de dados), [casos de uso](../11-casos-de-uso).

**Ordem:** modelo relacional (#54, Marco 5.1) → migrações de cada pacote (#55 a #60) → dicionário de dados conferido contra as migrações (#62, Marco 5.2).

## Convenções

- Tabelas e colunas em `snake_case`, no singular (`competencia`, `ciclo_avaliacao`).
- Chave primária `id bigint generated always as identity`; chave estrangeira `<tabela>_id`.
- Datas em `timestamp with time zone`; situações como `varchar` com `check` dos valores permitidos.
