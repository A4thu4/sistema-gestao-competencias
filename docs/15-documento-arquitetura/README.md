# Documento de Arquitetura

Documento de Arquitetura do Sistema de Gestão de Competências no modelo simplificado da ISO/IEC/IEEE 42010, com as visões no modelo C4 (níveis conforme o enunciado: C2 como diagrama de pacotes e C4 como diagrama de classes).

| Arquivo | Descrição | Status |
|---------|-----------|--------|
| [documento-arquitetura.md](documento-arquitetura.md) | Documento completo | A fazer |
| diagramas/*.puml | Fontes dos diagramas (PlantUML e C4-PlantUML) | A fazer |
| diagramas/*.png | Diagramas exportados | A fazer |

## Seções e responsáveis

| Seção | Conteúdo | Responsável | Issue | Milestone |
|-------|----------|-------------|-------|-----------|
| 1 e 2 | Introdução, stakeholders e preocupações (`PRE-NN`) | Gabriel Araújo Caetano | #51 | Marco 5.1 |
| 3 | Requisitos não funcionais e como são atendidos | Lorenzo Oliveira Maciel | #52 | Marco 5.1 |
| 4.1 a 4.3 | Estrutura do C4, C1 contexto, C2 pacotes, estilo e decisões (`DA-NN`) | Arthur Mamedes Borges | #53 | Marco 5.1 |
| 4.4 e 4.5 | C3 e C4 de cada pacote, com diagramas de sequência | Responsável de cada pacote | #55 a #60 | Marco 5.1 e 5.2 |
| 4.6 | Coesão, acoplamento e padrões de projeto | Gabriel Araújo Caetano | #61 | Marco 5.2 |

**Entradas:** [Documento Visão](../10-documento-visao/documento-visao.md), [ERS IEEE 830](../13-especificacao-requisitos/ers-ieee830.md), [casos de uso](../11-casos-de-uso), [lista de requisitos](../08-requisitos/lista-requisitos.md), [ISO/IEC 25010](../03-pesquisa-documental/iso-25010.md).

## Convenções

- Diagramas em PlantUML; C1 e C2 com a biblioteca [C4-PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML). Nome do arquivo: `c1-contexto`, `c2-pacotes`, `c3-<pacote>`, `c4-<pacote>`, `seq-<pacote>-<funcao>`.
- Exportar o PNG com o mesmo nome do `.puml` e versionar os dois.
- Os nomes de pacotes, componentes e classes dos diagramas devem ser os mesmos do código em [`prototipo/`](../../prototipo).
- Preocupações dos stakeholders: `PRE-NN`. Decisões de arquitetura: `DA-NN`.
