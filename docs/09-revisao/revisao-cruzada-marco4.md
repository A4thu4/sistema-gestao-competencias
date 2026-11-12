# Revisão cruzada do Marco 4

Revisão dos artefatos do Marco 4 antes da entrega de 19/11, conforme a etapa de validação do [processo de requisitos](../02-processo-requisitos/processo-requisitos.md#24-validação). Cada integrante revisa um artefato que não escreveu e registra o resultado neste arquivo (issue #35).

## 1. Distribuição das revisões

Proposta de distribuição: ninguém revisa o próprio artefato, e a carga fica equilibrada. Ajustar se o grupo preferir outra divisão.

| Artefato | Autor | Revisor | Resultado | Data |
|----------|-------|---------|-----------|------|
| [Documento Visão](../10-documento-visao/documento-visao.md) e [diagrama de casos de uso](../11-casos-de-uso/diagrama-casos-de-uso.png) | Arthur Mamedes Borges | Lorenzo Oliveira Maciel | A fazer | |
| [UC-01](../11-casos-de-uso/UC-01-gerenciar-competencias-questionarios.md) e [UC-02](../11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md) | José Eduardo | Gabriel Araújo Caetano | A fazer | |
| [UC-03](../11-casos-de-uso/UC-03-responder-avaliacao.md) e [casos resumidos UC-06 a UC-10](../11-casos-de-uso/README.md#casos-de-uso-resumidos) | Gabriel Araújo Caetano | José Eduardo | A fazer | |
| [UC-04](../11-casos-de-uso/UC-04-consultar-resultados.md) | Lorenzo Oliveira Maciel | Arthur Mamedes Borges | A fazer | |
| [UC-05](../11-casos-de-uso/UC-05-gerenciar-consentimento.md) | Arthur Mamedes Borges | José Eduardo | A fazer | |
| [ERS (IEEE 830)](../13-especificacao-requisitos/ers-ieee830.md) | José Eduardo | Arthur Mamedes Borges | A fazer | |
| [Matriz vertical](../14-rastreabilidade/matriz-vertical.md) e [horizontal](../14-rastreabilidade/matriz-horizontal.md) | Arthur Mamedes Borges | Rikelmy Andrade | A fazer | |
| [Protótipos das telas](../12-prototipos) | A definir (issue #32) | Rikelmy Andrade, se não for o autor | A fazer | |

Resultado: **Aprovado**, **Aprovado com pendência** (registrar em P-NN) ou **Ajustar** (registrar o ajuste em A-NN depois de feito).

## 2. Checklist por tipo de artefato

**Casos de uso completos** (Larman, cap. 6)

- [ ] Segue o [modelo](../11-casos-de-uso/modelo-caso-de-uso.md): interessados, pré e pós-condições, cenário principal, extensões, requisitos especiais, variações, frequência e questões em aberto.
- [ ] Nível de objetivo do usuário (processo elementar de negócio: uma pessoa, uma sessão, resultado de valor).
- [ ] Extensões numeradas pelo passo do cenário principal que as dispara.
- [ ] Todo `RF`/`RNF` citado existe na [lista](../08-requisitos/lista-requisitos.md) e o texto do caso de uso não contradiz o requisito.
- [ ] Referências a outros casos de uso batem com a [matriz horizontal](../14-rastreabilidade/matriz-horizontal.md#2-caso-de-uso--caso-de-uso).

**Documento Visão e diagrama**

- [ ] Toda característica `CAR-NN` tem requisitos e todo `RF` está em uma característica.
- [ ] Todo ator do diagrama aparece no Documento Visão e em pelo menos um caso de uso.
- [ ] As associações ator–caso de uso batem com o ator principal de cada caso de uso.

**ERS**

- [ ] Todos os 47 requisitos aparecem com o mesmo texto da lista geral.
- [ ] Todo `RNF` tem critério de verificação.
- [ ] Toda questão em aberto dos casos de uso está no Apêndice B.

**Matrizes de rastreabilidade**

- [ ] Toda linha da matriz vertical bate com a matriz da 1ª etapa (fontes e dores), o Documento Visão (característica) e a ERS (caso de uso e seção).
- [ ] As relações da matriz horizontal têm base no texto dos requisitos ou dos casos de uso.

**Protótipos**

- [ ] Toda tela tem `TL-NN`, caso de uso e requisitos.
- [ ] Todo caso de uso completo tem ao menos uma tela.
- [ ] Telas de resultado respeitam o anonimato (`RNF-01`, `RNF-02`) e há versão para celular (`RNF-07`).

## 3. Ajustes feitos durante a revisão

| ID | Artefato | Ajuste | Quem fez |
|----|----------|--------|----------|
| | | | |

## 4. Pendências

Já identificadas na revisão dos PRs do Marco 4, antes da revisão cruzada. Cada revisor acrescenta as suas.

| ID | Pendência | Artefato | Ação sugerida |
|----|-----------|----------|---------------|
| P-06 | O enunciado pede ao menos 1 caso de uso completo por integrante; o Rikelmy Andrade não tem caso completo (José Eduardo escreveu o UC-01 e o UC-02). | Casos de uso | Rikelmy transformar um caso resumido (ex.: UC-07 ou UC-09) em completo, ou registrar a justificativa da divisão. |
| P-07 | O `RF-10` diz "RH ou gestor" definem os avaliadores, e o UC-02 adota só o RH (`Q-02` da ERS). | UC-02, `RF-10` | Decidir: incluir o gestor no UC-02 e no diagrama, ou ajustar o texto do `RF-10`. |
| P-08 | A relação "externo" aparece no UC-02, UC-03 e UC-04, mas não no `RF-11`, `RF-19` e `RF-20`; o passo 10 do UC-01 não deixa o RH aplicar perguntas a externos; o externo não aceita o termo (`RF-29`) (`Q-03` da ERS). | UC-01 a UC-05, ERS | Decidir a regra do externo e ajustar os requisitos e o passo 10 do UC-01. |
| P-09 | A coluna de telas `TL-NN` da matriz vertical e a seção 3.1.1 da ERS dependem dos protótipos. | Matriz vertical, ERS | Preencher quando a issue #32 for concluída. |
| P-10 | O diagrama de casos de uso não mostra o `<<extend>>` do UC-05 sobre o UC-10, registrado na matriz horizontal. | Diagrama | Opcional; decidir se entra no diagrama. |

As pendências `P-03` e `P-05` da [revisão da 1ª etapa](revisao-cruzada.md#4-pendências-registradas) continuam valendo (`Q-17` da ERS).

Responsável pela consolidação: A definir (issue #35)
