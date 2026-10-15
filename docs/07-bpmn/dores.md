# Dores do processo atual

Dores do processo atual de avaliação de competências, ligadas às atividades do diagrama [processo-atual.png](processo-atual.png) (arquivo-fonte: [processo-atual.bpmn](processo-atual.bpmn)).

O processo as-is foi modelado a partir de `ENT-1`, `ENT-2`, `QUE-1` e `QUE-2`. Hoje a avaliação é anual, feita em planilhas enviadas por e-mail, e somente o gestor avalia o colaborador.

| ID   | Dor                                                                                       | Atividade no diagrama                                      | Fontes                    |
|------|-------------------------------------------------------------------------------------------|------------------------------------------------------------|---------------------------|
| D-01 | Consolidação manual das planilhas leva cerca de 3 semanas                                 | Consolidar respostas manualmente em planilha geral         | `ENT-1`                   |
| D-02 | Planilhas voltam com fórmulas quebradas e campos em branco                                | Conferir erros e campos em branco                          | `ENT-1`                   |
| D-03 | Planilhas circulam por e-mail e podem ser encaminhadas, sem controle de acesso            | Enviar planilha por e-mail aos gestores; Devolver planilha | `ENT-1`                   |
| D-04 | Atrasos dos gestores sem controle de prazo; cobrança feita à mão por e-mail               | Cobrar gestores atrasados por e-mail                       | `ENT-1`, `QUE-2`          |
| D-05 | Catálogo de competências mantido em documento Word, difícil de atualizar                  | Definir competências por cargo                             | `ENT-1`                   |
| D-06 | Só o chefe avalia: não há autoavaliação, avaliação por colegas, 360º nem anonimato        | Raia do colaborador (sem participação antes do feedback)   | `ENT-1`, `ENT-2`, `QUE-1` |
| D-07 | Cada gestor interpreta a escala de um jeito, sem descrição dos níveis                     | Preencher planilha de cada colaborador                     | `QUE-2`                   |
| D-08 | Avaliação baseada na memória dos últimos meses, sem registro ao longo do ano nem histórico | Preencher planilha de cada colaborador                     | `ENT-2`, `QUE-2`          |
| D-09 | Resultados limitados: sem painel por pessoa ou equipe, sem comparação entre avaliações    | Gerar resultado final por pessoa                           | `ENT-1`, `ENT-2`          |
| D-10 | Sem acompanhamento depois do feedback (metas, prazos, lembretes)                          | Conduzir conversa de feedback                              | `ENT-1`, `ENT-2`, `QUE-1` |
| D-11 | Colaborador não conhece os critérios e recebe o resultado semanas depois                  | Receber feedback                                           | `QUE-1`                   |
| D-12 | Preenchimento longo e cansativo (mais de 30 competências por pessoa)                      | Preencher planilha de cada colaborador                     | `ENT-2`                   |

## Observações

- No diagrama, as dores aparecem como caixas tracejadas vermelhas ligadas à atividade correspondente.
- Cada dor é uma oportunidade de requisito e deve ser coberta na [lista de requisitos](../08-requisitos).
