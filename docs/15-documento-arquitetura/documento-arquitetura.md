# Documento de Arquitetura e Desenho Detalhado — Sumário

Sistema de Gestão de Competências · APS de Desenho e Arquitetura de Software, Parte III · Entrega em 03/12/2026

O documento está dividido em um arquivo por seção. Este sumário liga todas elas; o [`gerar-pdf.py`](gerar-pdf.py) junta tudo, nesta ordem, no PDF da entrega. A divisão de autoria e revisão está no [README](README.md#autoria-e-revisão-dos-itens-de-informação).

- [Cabeçalho, autoria e revisão, controle de versões](secoes/00-capa-e-autoria.md)
- [1. Introdução](secoes/01-introducao.md)
  - [1.1 Visão geral do documento](secoes/01-introducao.md#11-visão-geral-do-documento) · [1.2 Objetivo do sistema](secoes/01-introducao.md#12-objetivo-do-sistema) · [1.3 Estrutura do documento](secoes/01-introducao.md#13-estrutura-do-documento)
- [2. Identificação dos stakeholders e suas preocupações](secoes/02-stakeholders.md)
  - [2.1 Stakeholders](secoes/02-stakeholders.md#21-stakeholders) · [2.2 Preocupações](secoes/02-stakeholders.md#22-preocupações-de-cada-stakeholder) · [2.3 Matriz stakeholder × preocupação × requisito](secoes/02-stakeholders.md#23-matriz-de-rastreabilidade-entre-stakeholders-preocupações-e-requisitos)
- [3. Requisitos não funcionais e como são atendidos](secoes/03-requisitos-nao-funcionais.md)
  - [3.1 RNF pela ISO/IEC 25010](secoes/03-requisitos-nao-funcionais.md#31-requisitos-não-funcionais-pelas-características-da-isoiec-25010) · [3.2 Racional dos mecanismos e decisões `DA-NN`](secoes/03-requisitos-nao-funcionais.md#32-racional-dos-mecanismos-arquiteturais-adotados)
- [4. Descrição do ambiente operacional Web](secoes/04-ambiente-operacional-web.md)
  - [4.1 Browser, servidor web e servidor de aplicação](secoes/04-ambiente-operacional-web.md#41-papel-do-browser-do-servidor-web-e-do-servidor-de-aplicação) · [4.2 HTTP](secoes/04-ambiente-operacional-web.md#42-protocolo-http) · [4.3 Ciclo da requisição](secoes/04-ambiente-operacional-web.md#43-ciclo-completo-de-uma-requisição) · [4.4 Sessão e estado](secoes/04-ambiente-operacional-web.md#44-mecanismos-de-sessão-e-de-manutenção-de-estado) · [4.5 Formas de invocação](secoes/04-ambiente-operacional-web.md#45-formas-de-invocação-da-aplicação-e-resultados-de-cada-etapa)
- [5. Projeto da arquitetura e desenho detalhado de componentes](secoes/05-1-nivel-contexto.md)
  - [5.1 Nível 1: contexto](secoes/05-1-nivel-contexto.md#51-nível-1-modelos-de-contexto) — DImp, DPN, DCdu
  - [5.2 Nível 2: containers e cenários](secoes/05-2-nivel-containers-cenarios.md) — DPac, casos de uso UC-01 a UC-10, protótipos TL-01 a TL-10
  - [5.3 Nível 3: componentes e comportamentos](secoes/05-3-nivel-componentes.md) — DCmp e DSeq por pacote, ECBD (DCD e DDC)
  - [5.4 Nível 4: código](secoes/05-4-nivel-codigo.md) — EOBD, DCla, DSeq de operações, contratos CO-01 a CO-03
- [Referências](secoes/06-referencias.md)
- [Apêndices](secoes/07-apendices.md): A — reuniões e comunicações · B — prompts de IA · C — rastreabilidade até o código
