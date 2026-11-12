# Protótipos das telas

Protótipos das telas feitos no Figma, organizados por caso de uso.

**Arquivo no Figma:** [Protótipo – Sistema de Gestão de Competências](https://www.figma.com/design/ZgNDOcDb6Y31QR62EEJkWR/Prot%C3%B3tipo-Sistema-de-Gest%C3%A3o-de-Compet%C3%AAncias?node-id=0-1)

| Arquivo | Descrição | Status |
|---------|-----------|--------|
| [telas/TL-01-gerenciar-competencias.png](telas/TL-01-gerenciar-competencias.png) | Tela de dashboard e listagem de competências cadastradas | Concluído |
| [telas/TL-02-criar-ciclo.png](telas/TL-02-criar-ciclo.png) | Formulário estruturado para criação de um novo ciclo de avaliação | Concluído |
| [telas/TL-03-responder-avaliacao.png](telas/TL-03-responder-avaliacao.png) | Wizard de perguntas da avaliação com escala Likert (1 a 5) | Concluído |
| [telas/TL-04-consultar-resultados.png](telas/TL-04-consultar-resultados.png) | Painel analítico com KPIs, gráfico radar e detalhamento de notas | Concluído |
| [telas/TL-05-gerenciar-consentimento.png](telas/TL-05-gerenciar-consentimento.png) | Modal do Termo de Consentimento e Privacidade (LGPD) | Concluído |

## Telas

| ID | Tela | Caso de uso | Perfil | Requisitos | Responsável |
|----|------|-------------|--------|------------|-------------|
| TL-01 | Gerenciar competências e questionários | [UC-01](../11-casos-de-uso/UC-01-gerenciar-competencias-questionarios.md) | RH | `RF-01`, `RNF-13` | Rikelmy Andrade |
| TL-02 | Criar ciclo de avaliação | [UC-02](../11-casos-de-uso/UC-02-criar-ciclo-avaliacao.md) | RH | `RF-08`, `RF-09` | Rikelmy Andrade |
| TL-03 | Responder avaliação | [UC-03](../11-casos-de-uso/UC-03-responder-avaliacao.md) | Gestor, colaborador, avaliador externo | `RF-04`, `RF-14`, `RF-16`, `RF-25` | Rikelmy Andrade |
| TL-04 | Consultar resultados | [UC-04](../11-casos-de-uso/UC-04-consultar-resultados.md) | Colaborador, gestor, RH | `RF-20`, `RF-23`, `RNF-01`, `RNF-02` | Rikelmy Andrade |
| TL-05 | Gerenciar consentimento | [UC-05](../11-casos-de-uso/UC-05-gerenciar-consentimento.md) | Todos os perfis | `RF-29`, `RNF-09` | Rikelmy Andrade |

Os casos de uso resumidos (UC-06 a UC-10) não têm protótipo nesta etapa. Os ajustes apontados na [revisão cruzada do Marco 4](../09-revisao/revisao-cruzada-marco4.md#4-pendências) (`P-11` a `P-14`, incluindo a versão para celular e tablet) ficam para a 2ª etapa (`Q-20` da [ERS](../13-especificacao-requisitos/ers-ieee830.md#apêndice-b--questões-em-aberto-e-alterações-propostas)). A ligação completa entre requisitos, casos de uso e telas está na [matriz vertical](../14-rastreabilidade/matriz-vertical.md).

## Convenções

- Nome do quadro no Figma = ID da tela (`TL-NN Nome`).
- Considerar a versão para celular (`RNF-07`).
- Telas que exibem resultados devem respeitar o anonimato (`RNF-01`, `RNF-02`).
- Após exportar, registrar a tela na [matriz vertical](../14-rastreabilidade/matriz-vertical.md).

Responsável: Rikelmy Andrade (issue #32)
