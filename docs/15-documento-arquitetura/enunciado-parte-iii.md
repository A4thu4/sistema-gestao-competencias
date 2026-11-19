# Parte III — resumo do enunciado

Resumo do enunciado "Parte III — Arquitetura e Desenho Detalhado" (APS de Desenho e Arquitetura de Software, PUC Goiás, 2026/2), para consulta rápida. Em caso de dúvida, vale o PDF do enunciado.

## Entrega

| Atividade | Data |
|-----------|------|
| Kickoff | 16/11/2026 |
| Execução | 16/11 a 03/12/2026 |
| Entrega do documento (PDF único, também impresso) | 03/12/2026 |
| Apresentação | 03 e 04/12/2026 |

## O que o documento precisa ter

| Seção | Conteúdo obrigatório |
|-------|----------------------|
| 1 | Introdução: visão geral do documento, objetivo do sistema, estrutura do documento |
| 2 | Stakeholders (usuários, operadores, adquirentes, proprietários, fornecedores, desenvolvedores e outros), preocupações de cada um e matriz stakeholder × preocupação × requisito |
| 3 | Requisitos não funcionais pelas características da ISO/IEC 25010 (adequação funcional, eficiência de desempenho, compatibilidade, usabilidade, confiabilidade, segurança, manutenibilidade, portabilidade e *safety*) e o racional dos mecanismos arquiteturais adotados |
| 4 | Ambiente operacional Web: papel do browser, do servidor web e do servidor de aplicação; HTTP (requisição, resposta, métodos, cabeçalhos, códigos de status); ciclo completo da requisição (resolução de nome, conexão, envio, processamento, resposta, renderização); sessão e manutenção de estado; formas de invocação e resultados de cada etapa |
| 5.1 Nível 1 | Diagrama de Implantação (DImp) com os atores; diagramas de atividades ou BPMN de cada processo de negócio (DPN); Diagrama de Casos de Uso (DCdu) |
| 5.2 Nível 2 | Diagrama de Pacotes (DPac); **para cada caso de uso do DCdu**, a descrição completa (Larman, cap. 6) e o protótipo de IHC (PIhc) |
| 5.3 Nível 3 | Para cada pacote com componentes, Diagrama de Componentes (DCmp); para cada pacote com dados persistentes, Esquema Conceitual de Banco de Dados (ECBD: DCD sem operações ou MER, e Dicionário de Dados Conceituais); para cada interação entre componentes, Diagrama de Sequência (DSeq) |
| 5.4 Nível 4 | Para cada ECBD, Esquema Operacional de Banco de Dados (EOBD: tabelas, atributos, tipos e tamanhos); para cada componente, Diagrama de Classes (DCla); 3 DSeq de operações de classe; 3 contratos de operação (Larman, cap. 11) |
| Final | Referências bibliográficas (depois da última seção e antes dos apêndices) e apêndices |

## Regras de entrega (valem nota)

1. Um único PDF. Cabeçalho com disciplina, tarefa e autoria em ordem alfabética; só entra quem contribuiu.
2. Cada item de informação identifica autor e revisor.
3. Todo integrante é autor e revisor de algum item; ninguém revisa o que escreveu.
4. Tabela artefato × autor × revisor.
5. Referências citadas no texto de cada item e listadas depois da última seção, antes dos apêndices.
6. Tudo sob gerência de configuração no GitHub (Kanban recomendado).
7. A docente entra na equipe do repositório (Adriana.silveira.souza@gmail.com).

**Uso de IA:** citado como referência, com organização e nome da ferramenta, URL, data de acesso e o prompt completo. Prompt com mais de um parágrafo vai para um apêndice, citado na referência (por exemplo, "Questão: Apêndice B, Seção B.2").

**Avaliação individual:** autoria e revisão de artefatos, interação com a equipe (registros de reuniões e comunicações como apêndice; o enunciado recomenda Discord com a docente incluída) e atitude. Pode haver nota diferente entre integrantes.

**Riscos da equipe:** registrar fatos e comunicações que mostrem descumprimento de compromisso por algum integrante.

## Critérios de avaliação

Conformidade com as regras, ética, referências (inclusive IA), completude do processo Web, completude do domínio, corretude dos conceitos Web, consistência entre seções (sem redundância nem conflito), navegabilidade, essencialidade de cada item, comunicação efetiva (português, figuras e tabelas com título, legenda e citação no texto), apresentação (todos sabem explicar o trabalho) e avaliação 360º entre os integrantes.

## Pontos a confirmar com a docente

- Os critérios "Completude de domínio" e "Essencialidade" falam em "autenticação de usuário"; o domínio do enunciado é o sistema de feedback. Confirmar que é o nosso domínio que será avaliado.
- O protótipo em código (combinado em aula, dispensa da última prova) não aparece neste enunciado. Confirmar se continua valendo e o prazo.
