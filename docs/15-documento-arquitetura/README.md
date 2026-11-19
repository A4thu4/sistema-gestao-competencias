# Documento de Arquitetura e Desenho Detalhado (Parte III)

Documento único da Parte III da APS, na estrutura obrigatória do [enunciado](enunciado-parte-iii.md): seções 1 a 5 (a seção 5 no modelo C4 adaptado, níveis 1 a 4), referências e apêndices. **Entrega e apresentação: 03/12/2026**, em um único PDF, também impresso.

O documento está dividido em um arquivo por seção, em [`secoes/`](secoes), para que cada integrante edite a sua parte sem conflito. O [sumário navegável](documento-arquitetura.md) liga todas as seções, e o script [`gerar-pdf.py`](gerar-pdf.py) junta tudo (inclusive os casos de uso de [`docs/11-casos-de-uso`](../11-casos-de-uso)) no PDF da entrega.

## Autoria e revisão dos itens de informação

Regras do enunciado (seção 7): cada item identifica autor e revisor; **todo integrante é autor e revisor de algum item**; **ninguém revisa o próprio item**. Esta tabela é a mesma que vai para o início do PDF ([secoes/00-capa-e-autoria.md](secoes/00-capa-e-autoria.md)); ao trocar um nome aqui, troque lá também.

| ID | Item de informação | Seção | Autor | Revisor | Issue | Milestone |
|----|--------------------|-------|-------|---------|-------|-----------|
| IT-01 | Cabeçalho, tabela de autoria e revisão, controle de versões | Início | Arthur Mamedes Borges | Gabriel Araújo Caetano | #65 | 5.3 |
| IT-02 | Introdução | 1 | Gabriel Araújo Caetano | Arthur Mamedes Borges | #51 | 5.1 |
| IT-03 | Stakeholders, preocupações e matriz stakeholder × preocupação × requisito | 2 | Gabriel Araújo Caetano | Lorenzo Oliveira Maciel | #51 | 5.1 |
| IT-04 | Requisitos não funcionais pelas características da ISO/IEC 25010 | 3.1 | Lorenzo Oliveira Maciel | Gabriel Araújo Caetano | #52 | 5.1 |
| IT-05 | Racional dos mecanismos arquiteturais e decisões `DA-NN` | 3.2 | Lorenzo Oliveira Maciel | Arthur Mamedes Borges | #52 | 5.1 |
| IT-06 | Ambiente Web: papéis, HTTP e ciclo completo da requisição | 4.1 a 4.3 | Gabriel Araújo Caetano | Rikelmy Andrade | #61 | 5.1 |
| IT-07 | Ambiente Web: sessão e estado, formas de invocação e resultados | 4.4 e 4.5 | Lorenzo Oliveira Maciel | Rikelmy Andrade | #67 | 5.1 |
| IT-08 | Diagrama de Implantação (DImp) com atores | 5.1.1 | Arthur Mamedes Borges | Gabriel Araújo Caetano | #53 | 5.1 |
| IT-09 | Processos de negócio em BPMN (DPN): atual e proposto | 5.1.2 | Rikelmy Andrade | José Eduardo | #68 | 5.1 |
| IT-10 | Diagrama de Casos de Uso (DCdu) | 5.1.3 | Arthur Mamedes Borges | Lorenzo Oliveira Maciel | #53 | 5.1 |
| IT-11 | Diagrama de Pacotes (DPac) | 5.2.1 | Arthur Mamedes Borges | José Eduardo | #53 | 5.1 |
| IT-12 | UC-01 e UC-02 completos (já escritos) | 5.2.2 | José Eduardo | Gabriel Araújo Caetano | #26, #27 | 5.1 |
| IT-13 | UC-03 completo (já escrito) | 5.2.2 | Gabriel Araújo Caetano | José Eduardo | #28 | 5.1 |
| IT-14 | UC-04 completo (já escrito) | 5.2.2 | Lorenzo Oliveira Maciel | Arthur Mamedes Borges | #29 | 5.1 |
| IT-15 | UC-05 completo (já escrito) | 5.2.2 | Arthur Mamedes Borges | Lorenzo Oliveira Maciel | #30 | 5.1 |
| IT-16 | UC-06 completo e protótipo TL-06 | 5.2.2 | Lorenzo Oliveira Maciel | Rikelmy Andrade | #69 | 5.1 |
| IT-17 | UC-07 completo e protótipo TL-07 | 5.2.2 | José Eduardo | Lorenzo Oliveira Maciel | #70 | 5.1 |
| IT-18 | UC-08 completo e protótipo TL-08 | 5.2.2 | Gabriel Araújo Caetano | Arthur Mamedes Borges | #71 | 5.1 |
| IT-19 | UC-09 completo e protótipo TL-09 | 5.2.2 | Arthur Mamedes Borges | José Eduardo | #72 | 5.1 |
| IT-20 | UC-10 completo e protótipo TL-10 | 5.2.2 | Rikelmy Andrade | Gabriel Araújo Caetano | #73 | 5.1 |
| IT-21 | Protótipos TL-01 a TL-05 (já feitos; aplicar `P-11` a `P-14`) | 5.2.2 | Rikelmy Andrade | Arthur Mamedes Borges | #32, #74 | 5.1 |
| IT-22 | Pacote `identidade`: DCmp, DSeq entre componentes e DCla | 5.3.1, 5.4.2 | Rikelmy Andrade | Lorenzo Oliveira Maciel | #55 | 5.2 |
| IT-23 | Pacote `competencias`: DCmp, DSeq entre componentes e DCla | 5.3.1, 5.4.2 | José Eduardo | Rikelmy Andrade | #56 | 5.2 |
| IT-24 | Pacote `ciclos`: DCmp, DSeq entre componentes e DCla | 5.3.1, 5.4.2 | José Eduardo | Gabriel Araújo Caetano | #57 | 5.2 |
| IT-25 | Pacote `avaliacoes`: DCmp, DSeq entre componentes e DCla | 5.3.1, 5.4.2 | Gabriel Araújo Caetano | José Eduardo | #58 | 5.2 |
| IT-26 | Pacote `resultados`: DCmp, DSeq entre componentes e DCla | 5.3.1, 5.4.2 | Lorenzo Oliveira Maciel | Arthur Mamedes Borges | #59 | 5.2 |
| IT-27 | Pacotes `privacidade` e `compartilhado`: DCmp, DSeq entre componentes e DCla | 5.3.1, 5.4.2 | Arthur Mamedes Borges | Rikelmy Andrade | #60 | 5.2 |
| IT-28 | ECBD: Diagrama de Classes de Dados (DCD, sem operações) | 5.3.2 | José Eduardo | Rikelmy Andrade | #54 | 5.1 |
| IT-29 | ECBD: Dicionário de Dados Conceituais (DDC) | 5.3.2 | Rikelmy Andrade | José Eduardo | #62 | 5.2 |
| IT-30 | Esquema Operacional de Banco de Dados (EOBD) | 5.4.1 | Rikelmy Andrade | José Eduardo | #62 | 5.2 |
| IT-31 | DSeq de operação e contrato `CO-01` (`publicarCiclo`, UC-02) | 5.4.3, 5.4.4 | José Eduardo | Lorenzo Oliveira Maciel | #57 | 5.2 |
| IT-32 | DSeq de operação e contrato `CO-02` (`enviarAvaliacao`, UC-03) | 5.4.3, 5.4.4 | Gabriel Araújo Caetano | Rikelmy Andrade | #58 | 5.2 |
| IT-33 | DSeq de operação e contrato `CO-03` (`consolidarResultados`, UC-04) | 5.4.3, 5.4.4 | Lorenzo Oliveira Maciel | Gabriel Araújo Caetano | #59 | 5.2 |
| IT-34 | Referências, incluindo o uso de IA | Referências | Arthur Mamedes Borges | Lorenzo Oliveira Maciel | #65 | 5.3 |
| IT-35 | Apêndice A (reuniões e comunicações) e Apêndice B (prompts de IA) | Apêndices | Arthur Mamedes Borges | Gabriel Araújo Caetano | #65 | 5.3 |
| IT-36 | Apêndice C: rastreabilidade requisito × caso de uso × pacote × componente × classe × tabela | Apêndices | Arthur Mamedes Borges | Rikelmy Andrade | #64 | 5.3 |

**Conferência das regras:** cada integrante é autor de 6 a 10 itens e revisor de 6 a 8; nenhum item tem o autor como revisor. Os UC-01 a UC-05 e os protótipos TL-01 a TL-05 mantêm os autores do Marco 4, com novos revisores, porque o enunciado pede a revisão de cada item deste documento.

**Ordem de trabalho:** as seções 1 a 4, o nível 1 e o nível 2 (milestone 5.1, até 23/11) alimentam os níveis 3 e 4 (milestone 5.2, até 29/11). A revisão cruzada acontece de 30/11 a 01/12 e o PDF final sai em 02/12 (milestone 5.3). O revisor registra o resultado na [revisão cruzada](../09-revisao) e o autor corrige antes do PDF.

## Estrutura e arquivos

| Arquivo | Conteúdo |
|---------|----------|
| [documento-arquitetura.md](documento-arquitetura.md) | Sumário navegável com links para todas as seções |
| [secoes/00-capa-e-autoria.md](secoes/00-capa-e-autoria.md) | Cabeçalho, autoria em ordem alfabética, tabela de autoria e revisão, controle de versões |
| [secoes/01-introducao.md](secoes/01-introducao.md) | 1. Introdução |
| [secoes/02-stakeholders.md](secoes/02-stakeholders.md) | 2. Stakeholders e preocupações |
| [secoes/03-requisitos-nao-funcionais.md](secoes/03-requisitos-nao-funcionais.md) | 3. Requisitos não funcionais e como são atendidos |
| [secoes/04-ambiente-operacional-web.md](secoes/04-ambiente-operacional-web.md) | 4. Ambiente operacional Web |
| [secoes/05-1-nivel-contexto.md](secoes/05-1-nivel-contexto.md) | 5.1 Nível 1: DImp, DPN (BPMN), DCdu |
| [secoes/05-2-nivel-containers-cenarios.md](secoes/05-2-nivel-containers-cenarios.md) | 5.2 Nível 2: DPac, casos de uso completos e protótipos de IHC |
| [secoes/05-3-nivel-componentes.md](secoes/05-3-nivel-componentes.md) | 5.3 Nível 3: DCmp, DSeq entre componentes, ECBD (DCD e DDC) |
| [secoes/05-4-nivel-codigo.md](secoes/05-4-nivel-codigo.md) | 5.4 Nível 4: EOBD, DCla, DSeq de operações, contratos de operação |
| [secoes/06-referencias.md](secoes/06-referencias.md) | Referências bibliográficas, incluindo o uso de IA |
| [secoes/07-apendices.md](secoes/07-apendices.md) | Apêndices A (reuniões e comunicações), B (prompts de IA) e C (rastreabilidade) |
| `diagramas/` | Fontes (`.puml`, `.bpmn`) e imagens exportadas (`.png`) |
| [gerar-pdf.py](gerar-pdf.py) | Gera `docs/impressao/parte-3-arquitetura.pdf` |
| [enunciado-parte-iii.md](enunciado-parte-iii.md) | Resumo do enunciado e das regras de entrega |

**Entradas:** [Documento Visão](../10-documento-visao/documento-visao.md), [ERS IEEE 830](../13-especificacao-requisitos/ers-ieee830.md), [casos de uso](../11-casos-de-uso), [protótipos](../12-prototipos), [lista de requisitos](../08-requisitos/lista-requisitos.md), [BPMN do processo atual](../07-bpmn), [ISO/IEC 25010](../03-pesquisa-documental/iso-25010.md).

## Convenções

**Ficha do item.** Todo item começa com a linha `Autor: … · Revisor: … · Issue: … · Referências: …`, como já está nos modelos. O revisor só troca "_a definir_" pela data da revisão quando aprovar.

**Diagramas.** PlantUML (`.puml`) para UML; [bpmn.io](https://demo.bpmn.io) para BPMN. Exporte o PNG com o mesmo nome da fonte e versione os dois em `diagramas/`.

| Diagrama | Sigla | Nome do arquivo |
|----------|-------|-----------------|
| Implantação | DImp | `dimp-implantacao` |
| Processo de negócio | DPN | `dpn-<processo>` (`dpn-ciclo-avaliacao-proposto`) |
| Casos de uso | DCdu | reaproveita `../11-casos-de-uso/diagrama-casos-de-uso` |
| Pacotes | DPac | `dpac-pacotes` |
| Componentes | DCmp | `dcmp-<pacote>` |
| Sequência entre componentes | DSeq | `dseq-cmp-<pacote>-<interacao>` |
| Classes de dados (sem operações) | DCD | `dcd-dados` |
| Classes | DCla | `dcla-<pacote>` |
| Sequência de operação | DSeq | `dseq-op-<operacao>` |

**Figuras e tabelas.** Numeradas pela seção, com título acima e fonte abaixo, e citadas no texto antes de aparecer ("a Figura 5.3 mostra…"):

```markdown
**Figura 5.1 — Diagrama de Implantação do Sistema de Gestão de Competências**

![Diagrama de Implantação](../diagramas/dimp-implantacao.png)

Fonte: elaborado pelos autores (2026).
```

Para tabelas, o título vai em `**Tabela 4.1 — …**` antes da tabela e a fonte depois.

**Nomes iguais ao código.** Pacotes, componentes, classes, operações e tabelas dos diagramas têm o mesmo nome usado em [`prototipo/`](../../prototipo) (rastreabilidade com o código, objetivo da seção 2 do enunciado).

**Identificadores:** item de informação `IT-NN`, preocupação de stakeholder `PRE-NN`, decisão de arquitetura `DA-NN`, contrato de operação `CO-NN`, além dos já usados (`RF-NN`, `RNF-NN`, `UC-NN`, `TL-NN`).

**Dicas dos modelos.** Os blocos `> Dica:` são instruções para quem escreve e saem antes da entrega; o `gerar-pdf.py` avisa se sobrar algum. Texto em _itálico_ entre `_…_` também é marcador a substituir.

**Referências e IA.** Toda ideia de terceiros é citada no próprio item (autor, ano) e entra na lista de [referências](secoes/06-referencias.md). Uso de IA também é referência, com ferramenta, URL, data de acesso e o prompt completo; prompt com mais de um parágrafo vai para o [Apêndice B](secoes/07-apendices.md#apêndice-b--prompts-submetidos-a-ferramentas-de-ia).

## Gerar o PDF

Requer `pandoc` e uma instalação de LaTeX com `xelatex` (no Windows, MiKTeX).

```bash
python docs/15-documento-arquitetura/gerar-pdf.py
```

O PDF sai em `docs/impressao/parte-3-arquitetura.pdf`, com sumário, links internos entre seções e links para o GitHub nos demais documentos. O script lista as dicas e figuras que ainda faltam.
