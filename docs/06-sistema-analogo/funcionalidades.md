# ANA: Funcionalidades do SurveyMonkey

Funcionalidades do SurveyMonkey relacionadas à avaliação de competências e ao feedback 360º. Fonte `ANA` na matriz de rastreabilidade.

**Fontes consultadas em 14/10/2026** (páginas oficiais do produto):

- [Modelo de avaliação 360º de funcionários](https://www.surveymonkey.com/mp/360-degree-employee-evaluation-survey-template/)
- [Gestão de desempenho de funcionários](https://www.surveymonkey.com/welcome/employee-performance-management)

Este levantamento é baseado na documentação pública do produto. As capturas de tela ficam em `capturas/` (ainda pendentes).

## 1. Visão geral

O SurveyMonkey é uma plataforma de criação e aplicação de pesquisas online. Para o contexto deste projeto, o mais relevante é o modelo pronto de **avaliação 360º**, que reúne feedback de gestores, colegas e subordinados diretos sobre uma pessoa.

## 2. Funcionalidades levantadas

| Código | Funcionalidade                       | Descrição                                                                                                                       |
|--------|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| F-01   | Modelos prontos de avaliação         | Modelos de avaliação de desempenho e de 360º que podem ser personalizados antes de usar.                                        |
| F-02   | Competências por categoria           | O modelo 360º agrupa as perguntas em áreas como comunicação, colaboração, liderança, solução de problemas e valores.            |
| F-03   | Escala de pontuação                  | Afirmações avaliadas em escala Likert de 5 pontos, que permite análise quantitativa.                                            |
| F-04   | Perguntas abertas                    | Campos de texto para pontos fortes, exemplos que justificam a nota e o formato começar, parar e continuar.                      |
| F-05   | Caminhos por tipo de avaliador       | Percursos separados para supervisores, colegas e subordinados diretos; a lógica de ramificação mostra a cada avaliador só as perguntas da sua relação com a pessoa avaliada. |
| F-06   | Respostas anônimas                   | Opção de resposta anônima; o modelo recomenda anonimato para colegas e subordinados e exibir resultados de grupo só com 3 ou mais respostas. |
| F-07   | Personalização das competências      | As competências podem ser adaptadas aos valores da empresa, às expectativas do departamento e ao nível do cargo.               |
| F-08   | Envio por e-mail e lembretes         | Convites enviados por e-mail com lembretes automáticos; o produto também permite envio por link, SMS, QR code e outros canais.  |
| F-09   | Resumos automáticos e filtros        | Resumo automático dos resultados com filtros para separar os dados por grupos.                                                  |
| F-10   | Painéis e relatórios cruzados        | Painéis personalizados, relatórios cruzados e análise de texto para achar padrões e temas recorrentes.                          |
| F-11   | Segmentação de resultados            | Quebra dos resultados por equipe, departamento e outros grupos, preservando o anonimato dos respondentes individuais.          |
| F-12   | Comparação com autoavaliação         | O modelo permite comparar a autoavaliação da pessoa com a visão dos demais.                                                     |
| F-13   | Exportação de dados                  | A página do modelo menciona exportação dos resultados.                                                                          |
| F-14   | Personalização visual                | Marca, temas, fontes e cores do questionário.                                                                                   |
| F-15   | Programas e tendência no tempo       | Vários questionários podem ser ligados para mostrar a evolução ao longo do tempo (disponível em planos Advantage ou superiores).|
| F-16   | Apoio ao desenvolvimento             | O modelo apoia a definição de 2 a 3 metas de desenvolvimento a partir dos temas identificados.                                  |
| F-17   | Colaboração e administração          | Trabalho colaborativo em tempo real (plano Teams) e administração com acesso seguro e integrações (plano Enterprise).          |

## 3. Limitações em relação ao nosso sistema

| Lacuna                                                                                                         | Observação                                                       |
|----------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| É uma ferramenta de pesquisa genérica, não um sistema de gestão de competências                                | Não há ciclo de avaliação por cargo com catálogo próprio de competências |
| Acompanhamento de metas de desenvolvimento com prazos e lembretes não aparece como recurso nativo              | O modelo apenas orienta a definir as metas                       |
| Controle de acesso por perfil (RH, gestor, colaborador) não é descrito nas páginas consultadas                 | A visibilidade fica a cargo de quem configura                    |
| Recursos de histórico, integrações e colaboração dependem do plano contratado                                  | Parte das funções é paga                                         |
