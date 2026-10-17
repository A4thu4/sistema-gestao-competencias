# Síntese dos requisitos dos questionários

Requisitos identificados nos questionários respondidos [QUE-1](resposta-1.md) (Analista de Marketing, colaboradora) e [QUE-2](resposta-2.md) (Supervisor de Operações, gestor). Cada item recebe um identificador `SQ-NN`, usado na [lista geral de requisitos](../08-requisitos/lista-requisitos.md).

Responsável: Gabriel Araújo Caetano

## 1. Respostas fechadas

| Pergunta | QUE-1 (colaboradora) | QUE-2 (gestor) |
|----------|----------------------|----------------|
| 2. Como a avaliação é feita hoje | Planilha por e-mail | Planilha por e-mail |
| 3. Nota do processo atual (1 a 5) | 2 | 2 |
| 5. Tipos de avaliação desejados | Chefe, autoavaliação, pares | Chefe, autoavaliação, pares, equipe avalia o líder |
| 7. Importância do anonimato (1 a 5) | 5 | 4 |
| 8. Dispositivos | Computador e celular | Computador, celular e tablet |
| 10. Acompanhar metas no sistema | Sim, com prazos e lembretes | Sim, revisão trimestral |
| 11. Tempo aceitável | Até 15 min (avaliação completa) | Até 10 min por pessoa |
| 12. Lembretes por e-mail | Sim | Sim, e ver quem não respondeu |
| 13. Aceitaria termo de consentimento | Sim, se for claro e simples | Sim |

Os dois respondentes dão nota 2 ao processo atual e usam a mesma ferramenta (planilha por e-mail), o que confirma o cenário descrito nas entrevistas.

## 2. Requisitos identificados

| ID | Tipo | Requisito identificado | Fonte | Evidência |
|----|------|------------------------|-------|-----------|
| SQ-01 | RF | Permitir avaliação pelo chefe, autoavaliação e avaliação pelos colegas; QUE-2 pede também a equipe avaliando o líder. | `QUE-1` `QUE-2` | Pergunta 5. |
| SQ-02 | RNF | Garantir o anonimato de quem avalia colegas. | `QUE-1` `QUE-2` | Pergunta 7: 5/5 e 4/5. |
| SQ-03 | RNF | Funcionar em computador, celular e tablet. | `QUE-1` `QUE-2` | Pergunta 8. |
| SQ-04 | RF | Mostrar ao colaborador o resultado detalhado por competência, com gráfico e comentários. | `QUE-1` | Pergunta 9. |
| SQ-05 | RF | Mostrar ao gestor o resultado consolidado da equipe, com média por competência e comparação entre pessoas, sem expor quem respondeu. | `QUE-2` | Pergunta 9. |
| SQ-06 | RF | Acompanhar metas de desenvolvimento com prazos e lembretes, revisadas a cada trimestre. | `QUE-1` `QUE-2` | Pergunta 10. |
| SQ-07 | RNF | Permitir responder a avaliação completa em até 15 minutos e avaliar cada pessoa da equipe em até 10 minutos. | `QUE-1` `QUE-2` | Pergunta 11. |
| SQ-08 | RF | Enviar lembretes de prazo por e-mail. | `QUE-1` `QUE-2` | Pergunta 12. |
| SQ-09 | RF | Mostrar ao gestor quem da equipe ainda não respondeu. | `QUE-2` | Pergunta 12. |
| SQ-10 | RF | Apresentar termo de consentimento no primeiro acesso. | `QUE-1` `QUE-2` | Pergunta 13. |
| SQ-11 | RNF | Redigir o termo de consentimento em linguagem clara e simples. | `QUE-1` | Pergunta 13. |
| SQ-12 | RF | Salvar a avaliação e continuar depois. | `QUE-1` | Pergunta 14. |
| SQ-13 | RF | Consultar o histórico de resultados de ciclos anteriores e comparar com o ano anterior. | `QUE-1` `QUE-2` | Perguntas 4 e 14. |
| SQ-14 | RF | Avisar o colaborador quando o resultado for liberado. | `QUE-1` | Pergunta 14. |
| SQ-15 | RF | Descrever cada nível da escala para que todos avaliem do mesmo jeito. | `QUE-2` | Perguntas 4 e 14. |
| SQ-16 | RF | Exportar resultados em Excel e PDF. | `QUE-2` | Pergunta 14. |
| SQ-17 | RF | Oferecer a opção "não tenho como avaliar" em cada item. | `QUE-2` | Pergunta 14. |
| SQ-18 | RF | Dar ao colaborador acesso aos critérios em que será avaliado. | `QUE-1` | Pergunta 4. |

## 3. Observações

- QUE-1 traz a visão de quem é avaliado: transparência dos critérios (`SQ-18`), aviso de resultado liberado (`SQ-14`) e histórico próprio (`SQ-13`).
- QUE-2 traz a visão do gestor: padronização da escala (`SQ-15`), visão da equipe (`SQ-05`) e opção "não tenho como avaliar" (`SQ-17`).

Total: 18 itens (14 funcionais e 4 não funcionais).
