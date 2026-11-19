### 5.4 Nível 4: modelos de código

#### 5.4.1 Esquema Operacional de Banco de Dados (EOBD)

Autor: Rikelmy Andrade · Revisor: José Eduardo · Issue: #62 · Referências: _documentação do PostgreSQL 16; ..._

> Dica: uma tabela por tabela do banco, com os tipos **e tamanhos** do PostgreSQL, iguais aos das migrações em `prototipo/src/main/resources/db/migration/`. Convenções: `snake_case`, singular, `id bigint generated always as identity`, chave estrangeira `<tabela>_id`, datas em `timestamp with time zone`, situações em `varchar` com `check`. Diga em uma frase como o DCD foi mapeado (associações N:N viram tabelas associativas) e que o esquema está na 3ª forma normal.

**Tabela 5.NN — Tabela `_nome_`**

_Descrição da tabela. Pacote: `_pacote_`. Migração: `V_N_N`._

| Coluna | Tipo | Tamanho | Nulo | Chave | Padrão | Descrição |
|--------------|------------|----------|-------|-------|------------|--------------------------------------|
| id | `bigint` | 8 bytes | Não | PK | identity | _..._ |
| _..._ | `varchar` | _120_ | _..._ | _..._ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).

#### 5.4.2 Diagramas de Classes (DCla)

> Dica: um DCla para cada componente dos DCmp da seção 5.3.1, com atributos, operações (nome, parâmetros e retorno) e relações. Os nomes são os das classes Java do protótipo. Fonte em `diagramas/dcla-<pacote>.puml` (um arquivo por pacote, com um `package` por componente, se ficar legível).

##### 5.4.2.1 Pacote `identidade`

Autor: Rikelmy Andrade · Revisor: Lorenzo Oliveira Maciel · Issue: #55

![DCla identidade](../diagramas/dcla-identidade.png)

##### 5.4.2.2 Pacote `competencias`

Autor: José Eduardo · Revisor: Rikelmy Andrade · Issue: #56

![DCla competencias](../diagramas/dcla-competencias.png)

##### 5.4.2.3 Pacote `ciclos`

Autor: José Eduardo · Revisor: Gabriel Araújo Caetano · Issue: #57

![DCla ciclos](../diagramas/dcla-ciclos.png)

##### 5.4.2.4 Pacote `avaliacoes`

Autor: Gabriel Araújo Caetano · Revisor: José Eduardo · Issue: #58

![DCla avaliacoes](../diagramas/dcla-avaliacoes.png)

##### 5.4.2.5 Pacote `resultados`

Autor: Lorenzo Oliveira Maciel · Revisor: Arthur Mamedes Borges · Issue: #59

![DCla resultados](../diagramas/dcla-resultados.png)

##### 5.4.2.6 Pacotes `privacidade` e `compartilhado`

Autor: Arthur Mamedes Borges · Revisor: Rikelmy Andrade · Issue: #60

![DCla privacidade](../diagramas/dcla-privacidade.png)

> Dica: em cada figura acima, troque o texto alternativo pelo título no padrão "**Figura 5.NN — DCla do pacote …**" e coloque a fonte abaixo.

#### 5.4.3 Diagramas de Sequência de operações de classe (DSeq)

> Dica: o enunciado pede 3 exemplos. Escolhemos uma operação central de cada um destes casos de uso. Cada DSeq mostra as classes do próprio componente e as mensagens a componentes externos. A mesma operação ganha o contrato em 5.4.4. Fonte em `diagramas/dseq-op-<operacao>.puml`.

**Tabela 5.NN — Operações escolhidas para DSeq e contrato**

| Contrato | Operação | Classe | Caso de uso | Autor | Revisor |
|----------|------------------------------|------------------------|--------|----------------|----------------|
| CO-01 | `publicarCiclo` | `CicloService` | UC-02 | José Eduardo | Lorenzo Oliveira Maciel |
| CO-02 | `enviarAvaliacao` | `AvaliacaoService` | UC-03 | Gabriel Araújo Caetano | Rikelmy Andrade |
| CO-03 | `consolidarResultados` | `ResultadoService` | UC-04 | Lorenzo Oliveira Maciel | Gabriel Araújo Caetano |

Fonte: elaborado pelos autores (2026).

> Dica: os nomes de classe e de operação acima são sugestões; usem os do código e atualizem a tabela. A assinatura completa (parâmetros e retorno) vai no contrato.

##### 5.4.3.1 `publicarCiclo`

Autor: José Eduardo · Revisor: Lorenzo Oliveira Maciel · Issue: #57

**Figura 5.NN — DSeq da operação `publicarCiclo`**

![DSeq publicarCiclo](../diagramas/dseq-op-publicar-ciclo.png)

Fonte: elaborado pelos autores (2026).

##### 5.4.3.2 `enviarAvaliacao`

Autor: Gabriel Araújo Caetano · Revisor: Rikelmy Andrade · Issue: #58

**Figura 5.NN — DSeq da operação `enviarAvaliacao`**

![DSeq enviarAvaliacao](../diagramas/dseq-op-enviar-avaliacao.png)

Fonte: elaborado pelos autores (2026).

##### 5.4.3.3 `consolidarResultados`

Autor: Lorenzo Oliveira Maciel · Revisor: Gabriel Araújo Caetano · Issue: #59

**Figura 5.NN — DSeq da operação `consolidarResultados`**

![DSeq consolidarResultados](../diagramas/dseq-op-consolidar-resultados.png)

Fonte: elaborado pelos autores (2026).

#### 5.4.4 Contratos de operação

> Dica: formato do Larman (cap. 11). Pós-condições descrevem **mudanças de estado** dos objetos do domínio, no passado: instância criada ou excluída, associação formada ou desfeita, atributo modificado. Não descrevem a tela nem as ações do algoritmo.

##### CO-01 `publicarCiclo`

Autor: José Eduardo · Revisor: Lorenzo Oliveira Maciel · Issue: #57 · Referências: Larman (cap. 11)

| Campo | Conteúdo |
|----------------------|------------------------------------------------------------------------------|
| **Operação** | `publicarCiclo(cicloId: Long)` |
| **Referências cruzadas** | UC-02, passo _N_; `RF-08`, `RF-12` |
| **Pré-condições** | _Ex.: existe um ciclo em rascunho com questionário e designações._ |
| **Pós-condições** | _Ex.: o atributo `situacao` do ciclo foi alterado para ABERTO; uma instância de Convite foi criada para cada designação…_ |

##### CO-02 `enviarAvaliacao`

Autor: Gabriel Araújo Caetano · Revisor: Rikelmy Andrade · Issue: #58 · Referências: Larman (cap. 11)

| Campo | Conteúdo |
|----------------------|------------------------------------------------------------------------------|
| **Operação** | `enviarAvaliacao(avaliacaoId: Long, respostas: List<RespostaDTO>)` |
| **Referências cruzadas** | UC-03, passo _N_; `RF-14`, `RF-15`, `RF-16`, `RNF-01` |
| **Pré-condições** | _..._ |
| **Pós-condições** | _..._ |

##### CO-03 `consolidarResultados`

Autor: Lorenzo Oliveira Maciel · Revisor: Gabriel Araújo Caetano · Issue: #59 · Referências: Larman (cap. 11)

| Campo | Conteúdo |
|----------------------|------------------------------------------------------------------------------|
| **Operação** | `consolidarResultados(cicloId: Long)` |
| **Referências cruzadas** | UC-04, passo _N_; `RF-19`, `RNF-02`, `RNF-12` |
| **Pré-condições** | _..._ |
| **Pós-condições** | _..._ |
