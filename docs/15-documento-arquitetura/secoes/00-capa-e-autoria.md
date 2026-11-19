# Sistema de Gestão de Competências — Documento de Arquitetura e Desenho Detalhado

| | |
|--------------------|--------------------------------------------------------|
| **Disciplina** | Desenho e Arquitetura de Software — PUC Goiás — Semestre 2026/2 |
| **Tarefa** | Atividade Prática Supervisionada (APS), Parte III — Arquitetura e Desenho Detalhado |
| **Autoria** (ordem alfabética) | Arthur Mamedes Borges; Gabriel Araújo Caetano; José Eduardo _sobrenome_; Lorenzo Oliveira Maciel; Rikelmy Andrade |
| **Repositório** | <https://github.com/A4thu4/sistema-gestao-competencias> |
| **Data de entrega** | 03/12/2026 |

> Dica: só entra na autoria quem contribuiu (regra 1 do enunciado). Confirme o nome completo de cada integrante antes do PDF final.

Autor: Arthur Mamedes Borges · Revisor: Gabriel Araújo Caetano · Issue: #65

## Autoria e revisão dos itens de informação

**Tabela 0.1 — Itens de informação, autores e revisores**

| ID | Item de informação | Seção | Autor | Revisor |
|--------|--------------------------------------|----------|----------------------|----------------------|
| IT-01 | Cabeçalho, tabela de autoria e revisão, controle de versões | Início | Arthur Mamedes Borges | Gabriel Araújo Caetano |
| IT-02 | Introdução | 1 | Gabriel Araújo Caetano | Arthur Mamedes Borges |
| IT-03 | Stakeholders, preocupações e matriz de rastreabilidade | 2 | Gabriel Araújo Caetano | Lorenzo Oliveira Maciel |
| IT-04 | Requisitos não funcionais (ISO/IEC 25010) | 3.1 | Lorenzo Oliveira Maciel | Gabriel Araújo Caetano |
| IT-05 | Racional dos mecanismos arquiteturais | 3.2 | Lorenzo Oliveira Maciel | Arthur Mamedes Borges |
| IT-06 | Papéis, protocolo HTTP e ciclo da requisição | 4.1 a 4.3 | Gabriel Araújo Caetano | Rikelmy Andrade |
| IT-07 | Sessão, estado e formas de invocação | 4.4 e 4.5 | Lorenzo Oliveira Maciel | Rikelmy Andrade |
| IT-08 | Diagrama de Implantação (DImp) | 5.1.1 | Arthur Mamedes Borges | Gabriel Araújo Caetano |
| IT-09 | Processos de negócio (DPN) | 5.1.2 | Rikelmy Andrade | José Eduardo |
| IT-10 | Diagrama de Casos de Uso (DCdu) | 5.1.3 | Arthur Mamedes Borges | Lorenzo Oliveira Maciel |
| IT-11 | Diagrama de Pacotes (DPac) | 5.2.1 | Arthur Mamedes Borges | José Eduardo |
| IT-12 | UC-01 e UC-02 | 5.2.2 | José Eduardo | Gabriel Araújo Caetano |
| IT-13 | UC-03 | 5.2.2 | Gabriel Araújo Caetano | José Eduardo |
| IT-14 | UC-04 | 5.2.2 | Lorenzo Oliveira Maciel | Arthur Mamedes Borges |
| IT-15 | UC-05 | 5.2.2 | Arthur Mamedes Borges | Lorenzo Oliveira Maciel |
| IT-16 | UC-06 e TL-06 | 5.2.2 | Lorenzo Oliveira Maciel | Rikelmy Andrade |
| IT-17 | UC-07 e TL-07 | 5.2.2 | José Eduardo | Lorenzo Oliveira Maciel |
| IT-18 | UC-08 e TL-08 | 5.2.2 | Gabriel Araújo Caetano | Arthur Mamedes Borges |
| IT-19 | UC-09 e TL-09 | 5.2.2 | Arthur Mamedes Borges | José Eduardo |
| IT-20 | UC-10 e TL-10 | 5.2.2 | Rikelmy Andrade | Gabriel Araújo Caetano |
| IT-21 | Protótipos TL-01 a TL-05 | 5.2.2 | Rikelmy Andrade | Arthur Mamedes Borges |
| IT-22 | Pacote `identidade` (DCmp, DSeq, DCla) | 5.3.1, 5.4.2 | Rikelmy Andrade | Lorenzo Oliveira Maciel |
| IT-23 | Pacote `competencias` (DCmp, DSeq, DCla) | 5.3.1, 5.4.2 | José Eduardo | Rikelmy Andrade |
| IT-24 | Pacote `ciclos` (DCmp, DSeq, DCla) | 5.3.1, 5.4.2 | José Eduardo | Gabriel Araújo Caetano |
| IT-25 | Pacote `avaliacoes` (DCmp, DSeq, DCla) | 5.3.1, 5.4.2 | Gabriel Araújo Caetano | José Eduardo |
| IT-26 | Pacote `resultados` (DCmp, DSeq, DCla) | 5.3.1, 5.4.2 | Lorenzo Oliveira Maciel | Arthur Mamedes Borges |
| IT-27 | Pacotes `privacidade` e `compartilhado` (DCmp, DSeq, DCla) | 5.3.1, 5.4.2 | Arthur Mamedes Borges | Rikelmy Andrade |
| IT-28 | Diagrama de Classes de Dados (DCD) | 5.3.2 | José Eduardo | Rikelmy Andrade |
| IT-29 | Dicionário de Dados Conceituais (DDC) | 5.3.2 | Rikelmy Andrade | José Eduardo |
| IT-30 | Esquema Operacional de Banco de Dados (EOBD) | 5.4.1 | Rikelmy Andrade | José Eduardo |
| IT-31 | DSeq e contrato CO-01 (`publicarCiclo`) | 5.4.3, 5.4.4 | José Eduardo | Lorenzo Oliveira Maciel |
| IT-32 | DSeq e contrato CO-02 (`enviarAvaliacao`) | 5.4.3, 5.4.4 | Gabriel Araújo Caetano | Rikelmy Andrade |
| IT-33 | DSeq e contrato CO-03 (`consolidarResultados`) | 5.4.3, 5.4.4 | Lorenzo Oliveira Maciel | Gabriel Araújo Caetano |
| IT-34 | Referências | — | Arthur Mamedes Borges | Lorenzo Oliveira Maciel |
| IT-35 | Apêndices A e B | — | Arthur Mamedes Borges | Gabriel Araújo Caetano |
| IT-36 | Apêndice C (rastreabilidade) | — | Arthur Mamedes Borges | Rikelmy Andrade |

Fonte: elaborado pelos autores (2026).

## Controle de versões

| Versão | Data | Autor | Alteração |
|--------|------------|------------------------|--------------------------------------------------------|
| 0.1 | 16/11/2026 | Arthur Mamedes Borges | Estrutura inicial |
| 0.2 | 19/11/2026 | Arthur Mamedes Borges | Estrutura alinhada ao enunciado da Parte III e divisão de autoria e revisão |
