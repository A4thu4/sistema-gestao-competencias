# Revisão cruzada do Marco 4

Revisão dos artefatos do Marco 4 antes da entrega de 19/11, conforme a etapa de validação do [processo de requisitos](../02-processo-requisitos/processo-requisitos.md#24-validação).

Revisor: Arthur Mamedes Borges (issue #35) · Data: 12/11/2026

O Arthur revisou os artefatos escritos pelos outros integrantes. Os artefatos escritos por ele foram revisados pelo revisor indicado na tabela, com o mesmo checklist.

## 1. Artefatos revisados

| Artefato | Autor | Revisor | Resultado | Data |
|----------|-------|---------|-----------|------|
| [Documento Visão](../10-documento-visao/documento-visao.md) e [diagrama de casos de uso](../11-casos-de-uso/diagrama-casos-de-uso.png) | Arthur Mamedes Borges | Lorenzo Oliveira Maciel | Aprovado | 12/11/2026 |
| [UC-01](../11-casos-de-uso/UC-01-gerenciar-competencias-questionarios.md) e [UC-02](../11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md) | José Eduardo | Arthur Mamedes Borges | Aprovado, com pendências P-07 e P-08 | 12/11/2026 |
| [UC-03](../11-casos-de-uso/UC-03-responder-avaliacao.md) e [casos resumidos UC-06 a UC-10](../11-casos-de-uso/README.md#casos-de-uso-resumidos) | Gabriel Araújo Caetano | Arthur Mamedes Borges | Aprovado, com pendência P-08 | 12/11/2026 |
| [UC-04](../11-casos-de-uso/UC-04-consultar-resultados.md) | Lorenzo Oliveira Maciel | Arthur Mamedes Borges | Aprovado, com pendência P-08 | 12/11/2026 |
| [UC-05](../11-casos-de-uso/UC-05-gerenciar-consentimento.md) | Arthur Mamedes Borges | José Eduardo | Aprovado | 12/11/2026 |
| [ERS (IEEE 830)](../13-especificacao-requisitos/ers-ieee830.md) | José Eduardo | Arthur Mamedes Borges | Aprovado, com pendência `Q-19` da ERS | 12/11/2026 |
| [Matriz vertical](../14-rastreabilidade/matriz-vertical.md) e [horizontal](../14-rastreabilidade/matriz-horizontal.md) | Arthur Mamedes Borges | Rikelmy Andrade | Aprovado | 12/11/2026 |
| [Protótipos das telas](../12-prototipos) | Rikelmy Andrade | Arthur Mamedes Borges | Aprovado, com pendências P-11 a P-14 (levadas à 2ª etapa) | 12/11/2026 |

Resultados possíveis: **Aprovado**, **Aprovado com pendência** (registrada em P-NN) ou **Ajustar** (ajuste registrado em A-NN depois de feito).

## 2. Checklist por tipo de artefato

**Casos de uso completos** (Larman, cap. 6)

- [x] Segue o [modelo](../11-casos-de-uso/modelo-caso-de-uso.md): interessados, pré e pós-condições, cenário principal, extensões, requisitos especiais, variações, frequência e questões em aberto.
- [x] Nível de objetivo do usuário (processo elementar de negócio: uma pessoa, uma sessão, resultado de valor).
- [x] Extensões numeradas pelo passo do cenário principal que as dispara.
- [x] Todo `RF`/`RNF` citado existe na [lista](../08-requisitos/lista-requisitos.md) e o texto do caso de uso não contradiz o requisito.
- [x] Referências a outros casos de uso batem com a [matriz horizontal](../14-rastreabilidade/matriz-horizontal.md#2-caso-de-uso--caso-de-uso).

**Documento Visão e diagrama**

- [x] Toda característica `CAR-NN` tem requisitos e todo `RF` está em uma característica.
- [x] Todo ator do diagrama aparece no Documento Visão e em pelo menos um caso de uso.
- [x] As associações ator–caso de uso batem com o ator principal de cada caso de uso.

**ERS**

- [x] Todos os 47 requisitos aparecem com o mesmo texto da lista geral.
- [x] Todo `RNF` tem critério de verificação.
- [x] Toda questão em aberto dos casos de uso está no Apêndice B.

**Matrizes de rastreabilidade**

- [x] Toda linha da matriz vertical bate com a matriz da 1ª etapa (fontes e dores), o Documento Visão (característica) e a ERS (caso de uso e seção).
- [x] As relações da matriz horizontal têm base no texto dos requisitos ou dos casos de uso.

**Protótipos**

- [x] Toda tela tem `TL-NN`, caso de uso e requisitos.
- [x] Todo caso de uso completo tem ao menos uma tela (`TL-01` a `TL-05`).
- [x] Telas de resultado respeitam o anonimato (`RNF-01`, `RNF-02`).
- [ ] Há versão para celular (`RNF-07`): não atendido nesta etapa, ver P-13 (levada à 2ª etapa).

## 3. Ajustes feitos durante a revisão

| ID | Artefato | Ajuste | Quem fez |
|----|----------|--------|----------|
| A-01 | Matriz vertical, ERS (tabela 3.1.1-a e `Q-18`) e README dos protótipos | Telas `TL-01` a `TL-05` registradas após o merge dos protótipos; README dos protótipos com link do Figma, links das telas e requisitos de cada tela (resolve a antiga pendência P-09). | Arthur Mamedes Borges |
| A-02 | UC-01 a UC-05 | Campo "Telas relacionadas" ligado às telas `TL-01` a `TL-05`; no UC-04, a variação de tipo de gráfico atualizada com o que o `TL-04` mostra. | Arthur Mamedes Borges |

## 4. Pendências

Identificadas na revisão dos PRs e na revisão cruzada. As justificadas seguem para a 2ª etapa pelas questões em aberto da [ERS](../13-especificacao-requisitos/ers-ieee830.md#apêndice-b--questões-em-aberto-e-alterações-propostas).

| ID | Pendência | Artefato | Ação sugerida | Situação |
|----|-----------|----------|---------------|----------|
| P-06 | O enunciado pede ao menos 1 caso de uso completo por integrante; o Rikelmy Andrade não tem caso completo (José Eduardo escreveu o UC-01 e o UC-02). | Casos de uso | Rikelmy transformar um caso resumido (ex.: UC-07 ou UC-09) em completo, ou registrar a justificativa da divisão. | Justificada: o grupo manteve a divisão; o José Eduardo ficou com o UC-01 e o UC-02, e o Rikelmy Andrade com os protótipos das telas. |
| P-07 | O `RF-10` diz "RH ou gestor" definem os avaliadores, e o UC-02 adota só o RH (`Q-02` da ERS). | UC-02, `RF-10` | Decidir: incluir o gestor no UC-02 e no diagrama, ou ajustar o texto do `RF-10`. | Justificada: adotado só o RH no UC-02; ajuste do `RF-10` levado à 2ª etapa (`Q-02` da ERS). |
| P-08 | A relação "externo" aparece no UC-02, UC-03 e UC-04, mas não no `RF-11`, `RF-19` e `RF-20`; o passo 10 do UC-01 não deixa o RH aplicar perguntas a externos; o externo não aceita o termo (`RF-29`) (`Q-03` da ERS). | UC-01 a UC-05, ERS | Decidir a regra do externo e ajustar os requisitos e o passo 10 do UC-01. | Justificada: regra adotada nos casos de uso (grupo próprio, mínimo de 3 respostas); ajuste dos requisitos levado à 2ª etapa (`Q-03` da ERS). |
| P-10 | O diagrama de casos de uso não mostra o `<<extend>>` do UC-05 sobre o UC-10, registrado na matriz horizontal. | Diagrama | Opcional; decidir se entra no diagrama. | Justificada: não entra no diagrama; a relação fica registrada na matriz horizontal, como recomenda o Larman. |
| P-11 | `TL-04` mostra só as colunas "Autoavaliação" e "Gestor"; faltam pares, subordinados e externos (UC-04, `RF-20`). A nota final de "Adaptabilidade" (4,5) não bate com as notas 4,0 e 4,0. | Protótipos | Incluir os grupos de avaliadores (com o aviso de menos de 3 respostas, `RNF-02`) e corrigir a nota. | Justificada: ajuste no Figma levado à 2ª etapa (`Q-20` da ERS). |
| P-12 | `TL-05` não mostra os direitos do titular nem o canal do encarregado (passo 2 do UC-05), e não há tela para a área "Meus dados". | Protótipos | Acrescentar ao termo e, se der tempo, prototipar "Meus dados". | Justificada: ajuste no Figma levado à 2ª etapa (`Q-20` da ERS). |
| P-13 | Os protótipos estão só na versão para computador (`RNF-07` pede celular e tablet). | Protótipos | Exportar ao menos uma tela na versão para celular, ou registrar como pendência da próxima etapa. | Justificada: versão para celular e tablet levada à 2ª etapa (`Q-20` da ERS). |
| P-14 | `TL-03` mostra o usuário "Equipe de RH / Administrador" respondendo a avaliação; o UC-03 é do gestor, colaborador ou avaliador externo. | Protótipos | Trocar o usuário logado no cabeçalho da tela. | Justificada: ajuste no Figma levado à 2ª etapa (`Q-20` da ERS). |

As pendências `P-03` e `P-05` da [revisão da 1ª etapa](revisao-cruzada.md#4-pendências-registradas) continuam valendo (`Q-17` da ERS).

Responsável pela consolidação: Arthur Mamedes Borges (issue #35)
