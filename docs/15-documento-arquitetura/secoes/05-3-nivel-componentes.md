### 5.3 Nível 3: modelos de componentes e comportamentos

#### 5.3.1 Diagramas de Componentes (DCmp) e de Sequência entre componentes (DSeq)

> Dica: um bloco por pacote do DPac que tem componentes. Em cada bloco: (a) o DCmp com os componentes do pacote (controller, serviço, repositório e as interfaces que oferece e usa de outros pacotes); (b) uma tabela curta de responsabilidades; (c) **um DSeq para cada interação entre componentes** do DCmp, com mensagens entre componentes (não entre classes; isso é o nível 4). Fontes em `diagramas/dcmp-<pacote>.puml` e `diagramas/dseq-cmp-<pacote>-<interacao>.puml`. Os nomes são os do código.

##### 5.3.1.1 Pacote `identidade`

Autor: Rikelmy Andrade · Revisor: Lorenzo Oliveira Maciel · Issue: #55 · Referências: _..._

**Figura 5.16 — DCmp do pacote `identidade`**

![DCmp identidade](../diagramas/dcmp-identidade.png)

Fonte: elaborado pelos autores (2026).

**Tabela 5.4 — Componentes do pacote `identidade`**

| Componente | Responsabilidade | Interface oferecida | Usa |
|----------------------|------------------------------------|----------------------|--------------------|
| _..._ | _..._ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).

**Figura 5.17 — DSeq entre componentes: _autenticar usuário_**

![DSeq identidade](../diagramas/dseq-cmp-identidade-autenticar.png)

Fonte: elaborado pelos autores (2026).

##### 5.3.1.2 Pacote `competencias`

Autor: José Eduardo · Revisor: Rikelmy Andrade · Issue: #56 · Referências: _..._

**Figura 5.NN — DCmp do pacote `competencias`**

![DCmp competencias](../diagramas/dcmp-competencias.png)

Fonte: elaborado pelos autores (2026).

_Tabela de componentes e um DSeq por interação entre componentes._

##### 5.3.1.3 Pacote `ciclos`

Autor: José Eduardo · Revisor: Gabriel Araújo Caetano · Issue: #57 · Referências: _..._

**Figura 5.NN — DCmp do pacote `ciclos`**

![DCmp ciclos](../diagramas/dcmp-ciclos.png)

Fonte: elaborado pelos autores (2026).

_Tabela de componentes e um DSeq por interação entre componentes._

##### 5.3.1.4 Pacote `avaliacoes`

Autor: Gabriel Araújo Caetano · Revisor: José Eduardo · Issue: #58 · Referências: _..._

**Figura 5.NN — DCmp do pacote `avaliacoes`**

![DCmp avaliacoes](../diagramas/dcmp-avaliacoes.png)

Fonte: elaborado pelos autores (2026).

_Tabela de componentes e um DSeq por interação entre componentes._

##### 5.3.1.5 Pacote `resultados`

Autor: Lorenzo Oliveira Maciel · Revisor: Arthur Mamedes Borges · Issue: #59 · Referências: _..._

**Figura 5.NN — DCmp do pacote `resultados`**

![DCmp resultados](../diagramas/dcmp-resultados.png)

Fonte: elaborado pelos autores (2026).

_Tabela de componentes e um DSeq por interação entre componentes._

##### 5.3.1.6 Pacotes `privacidade` e `compartilhado`

Autor: Arthur Mamedes Borges · Revisor: Rikelmy Andrade · Issue: #60 · Referências: _..._

**Figura 5.NN — DCmp dos pacotes `privacidade` e `compartilhado`**

![DCmp privacidade](../diagramas/dcmp-privacidade.png)

Fonte: elaborado pelos autores (2026).

_Tabela de componentes e um DSeq por interação entre componentes (inclui a publicação de eventos para o log de auditoria, `DA-08`)._

#### 5.3.2 Esquema Conceitual de Banco de Dados (ECBD)

> Dica: o enunciado pede um ECBD para cada pacote com dados persistentes. Fazemos **um** DCD com as classes agrupadas por pacote (cada pacote em um `package` do PlantUML) e o DDC com uma tabela por classe. Ponto de partida: as entidades e regras de dados da [seção 3.4 da ERS](../../13-especificacao-requisitos/ers-ieee830.md#34-requisitos-lógicos-de-banco-de-dados). Atenção à regra 4: a resposta de pares, subordinados e externos não guarda quem respondeu (`RNF-01`, `DA-06`).

##### 5.3.2.1 Diagrama de Classes de Dados (DCD)

Autor: José Eduardo · Revisor: Rikelmy Andrade · Issue: #54 · Referências: _..._

> Dica: classes, atributos (sem tipos de banco) e associações com multiplicidade. **Sem operações.** Fonte em `diagramas/dcd-dados.puml`.

**Figura 5.NN — Diagrama de Classes de Dados**

![DCD](../diagramas/dcd-dados.png)

Fonte: elaborado pelos autores (2026).

##### 5.3.2.2 Dicionário de Dados Conceituais (DDC)

Autor: Rikelmy Andrade · Revisor: José Eduardo · Issue: #62 · Referências: _..._

> Dica: uma tabela por classe do DCD, na ordem dos pacotes. Descreve o significado de cada atributo e a regra ou requisito de origem; tipos físicos e tamanhos ficam no EOBD (5.4.1).

**Tabela 5.NN — Classe _Nome_ (pacote `_pacote_`)**

_Descrição da classe._

| Atributo | Descrição | Domínio de valores | Obrigatório | Regra ou requisito |
|------------------|--------------------------------|------------------|------------|--------------------|
| _..._ | _..._ | _..._ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).
