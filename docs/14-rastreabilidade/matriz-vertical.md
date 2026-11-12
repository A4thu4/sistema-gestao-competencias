# Matriz de rastreabilidade vertical

Rastreia cada requisito entre **níveis de abstração**: da fonte (necessidade) até a característica do Documento Visão, o caso de uso, a tela do protótipo e a seção da ERS. Atualiza a [matriz da 1ª etapa](../08-requisitos/matriz-rastreabilidade.md), que continua sendo a referência para as fontes (`ENT`, `QUE`, `ANA`, `DOC`, `ESC`) e para as dores (`D-NN`).

Fonte → Dor → Característica (`CAR`) → Requisito → Caso de uso (`UC`) → Tela (`TL`) → Seção da ERS

**Origem de cada coluna**

| Coluna | De onde vem |
|--------|-------------|
| Fontes e dores | [Matriz da 1ª etapa](../08-requisitos/matriz-rastreabilidade.md), seções 1 e 3 |
| Característica | [Documento Visão](../10-documento-visao/documento-visao.md), seção 5 |
| Caso de uso | [ERS](../13-especificacao-requisitos/ers-ieee830.md), seções 3.2, 3.3 e 3.6, e [casos de uso](../11-casos-de-uso) |
| Tela | [Protótipos](../12-prototipos) da issue #32 (`TL-01` a `TL-05`, um por caso de uso completo). Os casos resumidos (UC-06 a UC-10) não têm protótipo nesta etapa |
| ERS | Seção da [ERS](../13-especificacao-requisitos/ers-ieee830.md) em que o requisito é especificado |

## 1. Matriz vertical

| Requisito | Prioridade | Fontes | Dores | Característica | Caso de uso | Tela | ERS |
|-----------|------------|--------|-------|----------------|-------------|----------------------|-----|
| RF-01 | Alta | ENT-1, ANA | D-05 | CAR-01 | UC-01 | TL-01 | 3.2.1 |
| RF-02 | Alta | ENT-1, ANA | D-05 | CAR-01 | UC-01 | TL-01 | 3.2.1 |
| RF-03 | Alta | ENT-1, ANA, DOC-2 | - | CAR-01 | UC-01 | TL-01 | 3.2.1 |
| RF-04 | Alta | ENT-1, QUE-2, ANA | D-07 | CAR-01 | UC-01 | TL-01 | 3.2.1 |
| RF-05 | Alta | ENT-1, QUE-1, ANA | - | CAR-01 | UC-01 | TL-01 | 3.2.1 |
| RF-06 | Média | QUE-2 | - | CAR-01 | UC-01, UC-03 | TL-01, TL-03 | 3.2.1 |
| RF-07 | Média | ENT-1, ANA | - | CAR-01 | UC-01 | TL-01 | 3.2.1 |
| RF-08 | Alta | ENT-1, ENT-2, QUE-1, QUE-2 | D-06 | CAR-02 | UC-02 | TL-02 | 3.2.2 |
| RF-09 | Alta | ENT-1 | D-04 | CAR-02 | UC-02, UC-03 | TL-02, TL-03 | 3.2.2 |
| RF-10 | Alta | ENT-2 | D-06 | CAR-02 | UC-02 | TL-02 | 3.2.2 |
| RF-11 | Média | ANA | - | CAR-02 | UC-03 | TL-03 | 3.2.2 |
| RF-12 | Alta | ENT-1, QUE-1, QUE-2, ANA | D-04 | CAR-02 | UC-02, UC-07 | TL-02 | 3.2.2 |
| RF-13 | Alta | ENT-1, QUE-2 | D-04 | CAR-02 | UC-07 | Sem protótipo (caso resumido) | 3.2.2 |
| RF-14 | Alta | ENT-2, QUE-1 | - | CAR-03 | UC-03 | TL-03 | 3.2.3 |
| RF-15 | Alta | ENT-2, DOC-2 | D-02 | CAR-03 | UC-03 | TL-03 | 3.2.3 |
| RF-16 | Alta | ENT-2, DOC-2 | - | CAR-03 | UC-03, UC-09 | TL-03 | 3.2.3 |
| RF-17 | Média | ENT-2 | D-08 | CAR-03 | UC-03, UC-08 | TL-03 | 3.2.3 |
| RF-18 | Média | ENT-2, QUE-1, QUE-2 | D-08 | CAR-03 | UC-03 | TL-03 | 3.2.3 |
| RF-19 | Alta | ENT-1, ANA, DOC-2 | D-01, D-09 | CAR-04 | UC-04 | TL-04 | 3.2.4 |
| RF-20 | Alta | ENT-1, ENT-2, QUE-1, ANA | D-09 | CAR-04 | UC-04 | TL-04 | 3.2.4 |
| RF-21 | Alta | ENT-1, ENT-2, QUE-2, ANA | D-09 | CAR-04 | UC-04 | TL-04 | 3.2.4 |
| RF-22 | Média | ENT-2, QUE-1 | - | CAR-04 | UC-04, UC-09 | TL-04 | 3.2.4 |
| RF-23 | Média | ENT-1, QUE-2, ANA | - | CAR-04 | UC-04 | TL-04 | 3.2.4 |
| RF-24 | Média | QUE-1 | D-11 | CAR-04 | UC-04 | TL-04 | 3.2.4 |
| RF-25 | Média | QUE-1, QUE-2 | D-11 | CAR-04 | UC-03 | TL-03 | 3.2.4 |
| RF-26 | Alta | ENT-1, QUE-1, QUE-2, ANA | D-08 | CAR-05 | UC-04 | TL-04 | 3.2.5 |
| RF-27 | Alta | ENT-1, ENT-2, QUE-1, QUE-2, ANA | D-10 | CAR-05 | UC-06 | Sem protótipo (caso resumido) | 3.2.5 |
| RF-28 | Média | ENT-1, ENT-2, QUE-1, QUE-2 | D-10 | CAR-05 | UC-06 | Sem protótipo (caso resumido) | 3.2.5 |
| RF-29 | Alta | ENT-1, QUE-1, QUE-2, DOC-1 | - | CAR-06 | UC-05, UC-10 | TL-05 | 3.2.6 |
| RF-30 | Alta | DOC-1 | - | CAR-06 | UC-05 | TL-05 | 3.2.6 |
| RF-31 | Alta | ENT-2 | - | CAR-06 | UC-10 | Sem protótipo (caso resumido) | 3.2.6 |
| RF-32 | Baixa | ENT-2 | - | CAR-06 | UC-10 | Sem protótipo (caso resumido) | 3.2.6 |
| RF-33 | Alta | ENT-1, ENT-2, ANA, DOC-1 | D-03 | CAR-06 | UC-10 | Sem protótipo (caso resumido) | 3.2.6 |
| RF-34 | Média | ESC | D-06 | CAR-02 | UC-02, UC-03 | TL-02, TL-03 | 3.2.2 |
| RNF-01 | Alta | ENT-1, ENT-2, QUE-1, QUE-2, ANA, DOC-1 | D-06 | CAR-06 | UC-02, UC-03, UC-04, UC-05 | TL-02, TL-03, TL-04, TL-05 | 3.6.1 |
| RNF-02 | Alta | ENT-1, ANA | - | CAR-06 | UC-02, UC-04 | TL-02, TL-04 | 3.6.1 |
| RNF-03 | Alta | ENT-1, ENT-2, DOC-1, DOC-2 | D-03 | CAR-06 | UC-03, UC-04, UC-05 | TL-03, TL-04, TL-05 | 3.6.1 |
| RNF-04 | Alta | ENT-1, DOC-1 | D-03 | CAR-06 | Todos | TL-01 a TL-05 | 3.6.1 |
| RNF-05 | Alta | ENT-1, ENT-2, DOC-1 | - | CAR-06 | UC-04, UC-09 | TL-04 | 3.6.1 |
| RNF-06 | Alta | DOC-1 | - | CAR-06 | UC-05 | TL-05 | 3.6.1 |
| RNF-07 | Alta | ENT-1, ENT-2, QUE-1, QUE-2, DOC-2 | - | Transversal | Todos | TL-01 a TL-05 | 3.6.2 |
| RNF-08 | Alta | ENT-1, ENT-2, QUE-1, QUE-2 | D-12 | CAR-03 | UC-03 | TL-03 | 3.6.2 |
| RNF-09 | Média | QUE-1 | - | CAR-06 | UC-01, UC-02, UC-05 | TL-01, TL-02, TL-05 | 3.6.2 |
| RNF-10 | Baixa | ANA | - | Transversal | Todos | TL-01 a TL-05 | 3.6.2 |
| RNF-11 | Alta | ENT-1, DOC-2 | - | CAR-04 | UC-03, UC-04 (e todos) | TL-03, TL-04 | 3.3 |
| RNF-12 | Alta | DOC-2 | D-02 | CAR-04 | UC-04 | TL-04 | 3.6.4 |
| RNF-13 | Alta | ENT-1, DOC-2 | D-05 | CAR-01 | UC-01 | TL-01 | 3.6.5 |

## 2. Caso de uso × tela

Liga cada caso de uso à área da interface que o atende e à tela do protótipo.

| Caso de uso | Formato | Área da interface | Perfil | Tela |
|-------------|---------|-------------------|--------|------|
| [UC-01](../11-casos-de-uso/UC-01-gerenciar-competencias-questionarios.md) Gerenciar competências e questionários | Completo | Competências e questionários | RH | [TL-01](../12-prototipos/telas/TL-01-gerenciar-competencias.png) |
| [UC-02](../11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md) Criar ciclo de avaliação | Completo | Ciclos de avaliação | RH | [TL-02](../12-prototipos/telas/TL-02-criar-ciclo.png) |
| [UC-03](../11-casos-de-uso/UC-03-responder-avaliacao.md) Responder avaliação | Completo | Minhas avaliações e formulário de avaliação | Gestor, colaborador, avaliador externo | [TL-03](../12-prototipos/telas/TL-03-responder-avaliacao.png) |
| [UC-04](../11-casos-de-uso/UC-04-consultar-resultados.md) Consultar resultados da avaliação | Completo | Resultados: painel individual, relatórios e exportação | Colaborador, gestor, RH | [TL-04](../12-prototipos/telas/TL-04-consultar-resultados.png) |
| [UC-05](../11-casos-de-uso/UC-05-gerenciar-consentimento.md) Gerenciar consentimento e dados pessoais | Completo | Termo de consentimento e "Meus dados" | Todos | [TL-05](../12-prototipos/telas/TL-05-gerenciar-consentimento.png) |
| [UC-06](../11-casos-de-uso/README.md#uc-06-gerenciar-plano-de-desenvolvimento) Gerenciar plano de desenvolvimento | Resumido | Plano de desenvolvimento | Gestor, colaborador | Sem protótipo (caso resumido) |
| [UC-07](../11-casos-de-uso/README.md#uc-07-acompanhar-andamento-do-ciclo) Acompanhar andamento do ciclo | Resumido | Painel de andamento do ciclo | RH, gestor | Sem protótipo (caso resumido) |
| [UC-08](../11-casos-de-uso/README.md#uc-08-registrar-observações-sobre-a-equipe) Registrar observações sobre a equipe | Resumido | Observações sobre a equipe | Gestor | Sem protótipo (caso resumido) |
| [UC-09](../11-casos-de-uso/README.md#uc-09-reabrir-avaliação-e-moderar-comentários) Reabrir avaliação e moderar comentários | Resumido | Reabertura de avaliação e moderação de comentários | RH | Sem protótipo (caso resumido) |
| [UC-10](../11-casos-de-uso/README.md#uc-10-autenticar-se) Autenticar-se | Resumido | Entrada no sistema | Todos | Sem protótipo (caso resumido) |

## 3. Requisitos não funcionais transversais

Valem para todo o sistema, por isso não são ligados a um caso de uso específico na coluna "Caso de uso".

| Requisito | Por que é transversal | Característica |
|-----------|-----------------------|----------------|
| `RNF-04` | HTTPS e criptografia valem para toda a comunicação e todo o armazenamento, não para uma função. | CAR-06 |
| `RNF-07` | Responsividade vale para todas as telas; é restrição de plataforma (`R-01`). | Nenhuma: o Documento Visão o trata na seção 6 (outros requisitos) |
| `RNF-10` | Identidade visual (logotipo e cores) aparece em todas as telas. | Nenhuma: o Documento Visão o trata na seção 6 (outros requisitos) |
| `RNF-11` | O pico de acesso afeta o sistema todo; concentra-se em UC-03 (encerramento do ciclo) e UC-04 (liberação dos resultados), que aparecem na matriz. | CAR-04 |

## 4. Cobertura por característica

| Característica | Requisitos | Casos de uso | Seção da ERS |
|----------------|------------|--------------|--------------|
| CAR-01 Catálogo de competências e questionários | `RF-01` a `RF-07`, `RNF-13` | UC-01, UC-03 | 3.2.1, 3.6.5 |
| CAR-02 Ciclos de avaliação com várias fontes | `RF-08` a `RF-13`, `RF-34` | UC-02, UC-03, UC-07 | 3.2.2 |
| CAR-03 Preenchimento simples e apoio ao avaliador | `RF-14` a `RF-18`, `RNF-08` | UC-03, UC-08, UC-09 | 3.2.3, 3.6.2 |
| CAR-04 Resultados, painéis e relatórios | `RF-19` a `RF-25`, `RNF-11`, `RNF-12` | UC-03, UC-04, UC-09 | 3.2.4, 3.3, 3.6.4 |
| CAR-05 Histórico e plano de desenvolvimento | `RF-26` a `RF-28` | UC-04, UC-06 | 3.2.5 |
| CAR-06 Acesso por perfil, confidencialidade e LGPD | `RF-29` a `RF-33`, `RNF-01` a `RNF-06`, `RNF-09` | UC-02 a UC-05, UC-09, UC-10 | 3.2.6, 3.6.1, 3.6.2 |

## 5. Verificação

- [x] Todo `RF` ligado a pelo menos um caso de uso (34 de 34).
- [x] Todo `RF` ligado a uma característica e a uma seção da ERS (34 de 34).
- [x] Todo `RNF` ligado a uma seção da ERS (13 de 13) e a uma característica ou justificado como transversal (seção 3).
- [x] Toda característica `CAR` do Documento Visão tem requisito associado (6 de 6).
- [x] Todo caso de uso tem ao menos um requisito (10 de 10).
- [x] Todo caso de uso completo ligado a pelo menos uma tela (5 de 5: `TL-01` a `TL-05`).
- [x] Toda tela ligada a um caso de uso e a requisitos (5 de 5, ver [protótipos](../12-prototipos/README.md#telas)).

## 6. Observações

- **`RF-25`** está na característica CAR-04 (resultados) no Documento Visão, mas é atendido no UC-03, porque o colaborador consulta os critérios antes e durante o preenchimento.
- **`RNF-11`** está na CAR-04 no Documento Visão e é tratado como transversal na ERS (seção 3.3); a matriz mantém os dois.
- **`RF-10`**: o requisito fala em "RH ou gestor", e o UC-02 adota só o RH (`Q-02` da ERS). Se o gestor também definir avaliadores, esta linha passa a incluir a área do gestor.
- **`RNF-07`** aparece em todas as telas, mas os protótipos `TL-01` a `TL-05` estão só na versão para computador; a versão para celular fica registrada como pendência na [revisão do Marco 4](../09-revisao/revisao-cruzada-marco4.md#4-pendências).
- **Avaliador externo**: `RF-11`, `RF-19` e `RF-20` ainda não citam a relação "externo" (`Q-03` da ERS); as linhas continuam válidas, mas o texto dos requisitos pode mudar.

Responsável: Arthur Mamedes Borges (issue #34)
