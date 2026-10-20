# Síntese dos requisitos dos questionários

Requisitos identificados nos questionários respondidos [QUE-1](resposta-1.md) (Analista de Marketing, colaboradora) e [QUE-2](resposta-2.md) (Supervisor de Operações, gestor). Cada item recebe um identificador `SQ-NN`, usado na [lista geral de requisitos](../08-requisitos/lista-requisitos.md).

Responsável: Gabriel Araújo Caetano

## 1. Respostas fechadas

As respostas seguem o modelo de [questionario.md](questionario.md); os números entre parênteses indicam a pergunta.

| Pergunta | QUE-1 (colaboradora) | QUE-2 (gestor) |
|----------|----------------------|----------------|
| 1.1 Papel nas avaliações | É avaliada | Avalia e é avaliado |
| 2.1 Frequência atual | Uma vez por ano | Uma vez por ano |
| 2.2 Tipos de avaliação hoje | Só a chefia avalia | Só a chefia avalia |
| 2.3 Como é feita hoje | Planilha | Planilha |
| 2.4 Satisfação com o processo (1 a 5) | 2 | 2 |
| 2.5 Principais problemas | Falta de anonimato; resultado não devolvido; não conhece os critérios | Sem histórico; mera formalidade; escala sem padrão |
| 4.1 À vontade para avaliação negativa (1 a 5) | 2 | 3 |
| 4.2 Seria mais sincero com anonimato | Sim | Sim |
| 4.3 Quem vê respostas individuais | Ninguém | Ninguém; RH só para auditoria |
| 4.4 Mínimo de respondentes | 3 | 3 |
| 4.5 Consultar e excluir os próprios dados | Sim | Sim |
| 5.1 Dispositivo preferido | Tanto faz | Celular |
| 5.2 Frequência desejada | Contínua | Duas vezes por ano |
| 5.3 Tempo por pessoa avaliada | 5 a 15 min | 5 a 15 min |

### Importância das funcionalidades (3.1, de 1 a 5)

| Item | Funcionalidade | QUE-1 | QUE-2 | Média |
|------|----------------|:-----:|:-----:|:-----:|
| a | Perguntas e competências conforme o cargo | 4 | 4 | 4,0 |
| b | Avaliação 360º | 5 | 5 | 5,0 |
| c | Autoavaliação | 5 | 5 | 5,0 |
| d | Anonimato de quem avalia | 5 | 4 | 4,5 |
| e | Gráficos por competência | 5 | 4 | 4,5 |
| f | Comparação com avaliações anteriores | 5 | 5 | 5,0 |
| g | Comparação com a média da equipe | 3 | 5 | 4,0 |
| h | Metas de desenvolvimento | 5 | 5 | 5,0 |
| i | Lembretes automáticos | 4 | 5 | 4,5 |
| j | Responder pelo celular | 4 | 5 | 4,5 |
| k | Comentário junto com a nota | 5 | 4 | 4,5 |
| l | Exportar relatórios | 2 | 5 | 3,5 |
| m | Feedback contínuo | 4 | 4 | 4,0 |

Os dois respondentes dão nota 2 ao processo atual e usam a mesma ferramenta (planilha), o que confirma o cenário das entrevistas. Itens com média 4,5 ou mais reforçam a prioridade alta dos requisitos correspondentes na lista geral; a exportação (3,5) teve a menor média, coerente com a prioridade média de `RF-23`.

## 2. Requisitos identificados

| ID | Tipo | Requisito identificado | Fonte | Evidência |
|----|------|------------------------|-------|-----------|
| SQ-01 | RF | Permitir avaliação pelo chefe, autoavaliação e avaliação pelos colegas; QUE-2 pede também a equipe avaliando o líder. | `QUE-1` `QUE-2` | 3.1b e 3.1c: nota 5 nos dois; QUE-2 pede a equipe avaliando o líder (6.3). |
| SQ-02 | RNF | Garantir o anonimato de quem avalia colegas. | `QUE-1` `QUE-2` | 3.1d: 5 e 4; 4.2: ambos dariam avaliações mais sinceras com anonimato; 4.3: respostas individuais visíveis a ninguém (QUE-2 aceita RH só para auditoria). |
| SQ-03 | RNF | Funcionar em computador, celular e tablet. | `QUE-1` `QUE-2` | 3.1j: 4 e 5; 5.1: tanto faz e celular; QUE-2 cita também o tablet (6.3). |
| SQ-04 | RF | Mostrar ao colaborador o resultado detalhado por competência, com gráfico e comentários. | `QUE-1` | 3.1e: 5; 3.1k: 5; 6.2. |
| SQ-05 | RF | Mostrar ao gestor o resultado consolidado da equipe, com média por competência e comparação entre pessoas, sem expor quem respondeu. | `QUE-2` | 3.1g: 5; 6.2. |
| SQ-06 | RF | Acompanhar metas de desenvolvimento com prazos e lembretes, revisadas a cada trimestre. | `QUE-1` `QUE-2` | 3.1h: nota 5 nos dois; 6.2 (QUE-2 pede revisão trimestral). |
| SQ-07 | RNF | Permitir responder a avaliação completa em até 15 minutos e avaliar cada pessoa da equipe em até 10 minutos. | `QUE-1` `QUE-2` | 5.3: de 5 a 15 minutos por pessoa nos dois; QUE-1 aceita até 15 minutos no total (6.2) e QUE-2 até 10 minutos por pessoa (6.3). |
| SQ-08 | RF | Enviar lembretes de prazo por e-mail. | `QUE-1` `QUE-2` | 3.1i: 4 e 5; 6.2. |
| SQ-09 | RF | Mostrar ao gestor quem da equipe ainda não respondeu. | `QUE-2` | 6.2. |
| SQ-10 | RF | Apresentar termo de consentimento no primeiro acesso. | `QUE-1` `QUE-2` | 6.3 nos dois. |
| SQ-11 | RNF | Redigir o termo de consentimento em linguagem clara e simples. | `QUE-1` | 6.3. |
| SQ-12 | RF | Salvar a avaliação e continuar depois. | `QUE-1` | 6.3. |
| SQ-13 | RF | Consultar o histórico de resultados de ciclos anteriores e comparar com o ano anterior. | `QUE-1` `QUE-2` | 3.1f: nota 5 nos dois; 2.5 (QUE-2: não há histórico); 6.1 e 6.3. |
| SQ-14 | RF | Avisar o colaborador quando o resultado for liberado. | `QUE-1` | 6.3. |
| SQ-15 | RF | Descrever cada nível da escala para que todos avaliem do mesmo jeito. | `QUE-2` | 2.5 e 6.1 (escala interpretada de forma diferente); 6.3. |
| SQ-16 | RF | Exportar resultados em Excel e PDF. | `QUE-2` | 3.1l: 5; 6.3. |
| SQ-17 | RF | Oferecer a opção "não tenho como avaliar" em cada item. | `QUE-2` | 6.3. |
| SQ-18 | RF | Dar ao colaborador acesso aos critérios em que será avaliado. | `QUE-1` | 2.5 (Outro) e 6.1. |

## 3. Observações

- QUE-1 traz a visão de quem é avaliado: transparência dos critérios (`SQ-18`), aviso de resultado liberado (`SQ-14`) e histórico próprio (`SQ-13`).
- QUE-2 traz a visão do gestor: padronização da escala (`SQ-15`), visão da equipe (`SQ-05`) e opção "não tenho como avaliar" (`SQ-17`).
- As perguntas 4.4 (mínimo de 3 respondentes) e 4.5 (consultar e excluir os próprios dados) confirmam, pelo lado do usuário, o que já vinha das entrevistas (`RNF-02`) e da LGPD (`RF-30`).

Total: 18 itens (14 funcionais e 4 não funcionais).
