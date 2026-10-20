# Revisão cruzada da 1ª etapa

Revisão dos artefatos da 1ª etapa antes da entrega de 21/10, conforme a etapa de validação do [processo de requisitos](../02-processo-requisitos/processo-requisitos.md#24-validação). Cada artefato foi revisado por um integrante diferente do autor.

Revisor: Arthur Mamedes Borges (issue #16) · Data: 20/10/2026

## 1. Artefatos revisados

| Artefato | Autor | Resultado |
|----------|-------|-----------|
| Kanban e estrutura do repositório | Arthur Mamedes Borges | Aprovado |
| Processo de requisitos | Gabriel Araújo Caetano | Aprovado |
| Pesquisa documental (LGPD e ISO/IEC 25010) | Lorenzo Oliveira Maciel | Aprovado |
| Termo de consentimento | José Eduardo | Aprovado |
| Roteiro de entrevista e modelo de questionário | Arthur Mamedes Borges | Aprovado |
| Entrevistas ENT-1 e ENT-2 e síntese | Gabriel Araújo Caetano | Aprovado, com pendências P-01 e P-03 |
| Questionários QUE-1 e QUE-2 e síntese | Gabriel Araújo Caetano | Aprovado, com pendência P-01 |
| Sistema análogo (funcionalidades e síntese) | Lorenzo Oliveira Maciel | Aprovado |
| BPMN do processo atual e dores | Rikelmy Andrade | Aprovado |
| Lista geral de requisitos | Rikelmy Andrade | Aprovado, com pendência P-04 |
| Matriz de rastreabilidade | José Eduardo | Aprovado |

## 2. Checklist de validação

| Critério | Situação |
|----------|----------|
| Todo requisito tem identificador único (`RF-NN` / `RNF-NN`) | Atendido (46 requisitos) |
| Todo requisito está escrito como frase verificável ("O sistema deve...") | Atendido |
| Todo requisito está ligado a pelo menos uma fonte na matriz | Atendido (46 de 46) |
| Toda fonte gerou pelo menos um requisito | Atendido (7 de 7) |
| Todo item das sínteses foi aproveitado na lista | Atendido (61 de 61) |
| Toda dor do BPMN tem requisito correspondente | Atendido (12 de 12) |
| Pontos da LGPD e da ISO/IEC 25010 cobertos | Atendido (DOC-1 §2.1 a §2.4; DOC-2 §2.1 a §2.5) |
| Requisitos sem contradição entre si | Atendido |
| Dados pessoais só nos arquivos de transcrição e respostas | Atendido |
| READMEs de `docs/` com responsável e status atualizados | Atendido |

## 3. Ajustes feitos durante a revisão

| ID | Ajuste |
|----|--------|
| A-01 | Requisitos amplos divididos para ficarem verificáveis isoladamente (ex.: login individual `RF-31` e login corporativo `RF-32`). |
| A-02 | Requisitos repetidos entre fontes unidos em um só, com todas as origens listadas (ex.: lembretes por e-mail `RF-12`). |
| A-03 | Metas numéricas incluídas onde as fontes deram números: mínimo de 3 respondentes (`RNF-02`), 10 e 15 minutos de preenchimento (`RNF-08`). |
| A-04 | Removidos do README da pesquisa documental o item `DOC-3` sem conteúdo e, do sistema análogo, a pasta `capturas/` não criada; as evidências do análogo são os links oficiais com data de consulta. |
| A-05 | Corrigido o nome do arquivo do termo de consentimento no README de anexos. |
| A-06 | Nomes completos dos integrantes padronizados nos READMEs. |
| A-07 | Respostas `QUE-1` e `QUE-2` registradas no modelo oficial de `questionario.md` (seções 0 a 6), mantendo o conteúdo já respondido; síntese dos questionários atualizada com as novas referências e com a tabela de importância das funcionalidades (resolve a antiga pendência P-02). |
| A-08 | Captura final do Kanban adicionada em `docs/01-planejamento/kanban-final.png`, com as 17 issues concluídas (resolve a antiga pendência P-06). |
| A-09 | Aviso de rascunho das transcrições `ENT-1` e `ENT-2` substituído por nota de persona fictícia, após conferência das perguntas com o roteiro. |

## 4. Pendências registradas

| ID | Pendência | Ação sugerida |
|----|-----------|---------------|
| P-01 | Entrevistados e respondentes são personas fictícias (decisão do grupo), com telefones fictícios e sem termo assinado. O enunciado pede identificação real (nome, função e telefone). | Confirmar com o professor se é aceito; se não, aplicar o mesmo roteiro e o mesmo questionário a pessoas reais, com termo assinado. |
| P-03 | O roteiro previa o `ENT-2` com um colaborador sem cargo de gestão; a entrevista foi feita com um gestor. A visão do colaborador ficou coberta pelo `QUE-1`. | Registrado; considerar uma entrevista com colaborador na 2ª etapa. |
| P-04 | Partes interessadas externas, citadas no escopo do enunciado, não apareceram em nenhuma fonte, por isso não há requisito para elas. | Levantar com o RH na validação da 2ª etapa. |
| P-05 | 13 requisitos têm uma única fonte (lista na [matriz](../08-requisitos/matriz-rastreabilidade.md#4-verificação)). | Confirmar com usuários na 2ª etapa. |
