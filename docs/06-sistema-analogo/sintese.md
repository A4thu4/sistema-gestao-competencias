# ANA: Síntese dos requisitos do sistema análogo

Requisitos derivados das funcionalidades do SurveyMonkey descritas em [funcionalidades.md](funcionalidades.md). Todos têm como fonte `ANA`. Os identificadores `RF-NN` e `RNF-NN` finais são atribuídos na [lista de requisitos](../08-requisitos).

## Requisitos funcionais derivados

| Requisito derivado                                                                                                    | Funcionalidade de origem |
|-----------------------------------------------------------------------------------------------------------------------|--------------------------|
| O sistema deve oferecer modelos de questionário prontos de avaliação (chefe–colaborador, pares e 360º) que o RH possa personalizar | F-01, F-07 |
| O sistema deve permitir agrupar as perguntas por competência e adaptá-las por cargo ou nível                          | F-02, F-07               |
| O sistema deve permitir perguntas de escala (por exemplo, de 1 a 5) e perguntas abertas                               | F-03, F-04               |
| O sistema deve apresentar a cada avaliador apenas as perguntas da sua relação com o avaliado (chefe, colega, subordinado ou autoavaliação) | F-05      |
| O sistema deve permitir respostas anônimas para pares e subordinados                                                  | F-06                     |
| O sistema deve exibir resultados de grupo apenas quando houver um número mínimo de respostas (por exemplo, 3)         | F-06                     |
| O sistema deve enviar convites por e-mail e lembretes automáticos de prazo                                            | F-08                     |
| O sistema deve gerar um resumo automático dos resultados por pessoa, com filtros                                      | F-09                     |
| O sistema deve oferecer painéis e relatórios por pessoa, equipe e departamento, sem expor quem respondeu              | F-10, F-11               |
| O sistema deve comparar a autoavaliação com a avaliação dos demais                                                    | F-12                     |
| O sistema deve exportar os resultados (por exemplo, em PDF e Excel)                                                   | F-13                     |
| O sistema deve permitir registrar metas de desenvolvimento a partir dos resultados da avaliação                       | F-16                     |
| O sistema deve guardar o histórico dos ciclos para mostrar a evolução ao longo do tempo                               | F-15                     |

## Requisitos não funcionais derivados

| Requisito derivado                                                                  | Funcionalidade de origem |
|-------------------------------------------------------------------------------------|--------------------------|
| O sistema deve permitir personalizar a identidade visual do questionário (marca, cores) | F-14                  |
| O sistema deve permitir acesso e administração seguros por perfil de usuário         | F-17                     |

## Diferenciais do nosso sistema

Pontos em que nosso sistema deve ir além do análogo, segundo [funcionalidades.md](funcionalidades.md#3-limitações-em-relação-ao-nosso-sistema):

- Catálogo de competências próprio por cargo, dentro do sistema.
- Acompanhamento contínuo das metas, com prazos e lembretes.
- Controle de acesso por perfil: RH, gestor (somente a própria equipe) e colaborador (somente o próprio resultado).
- Termo de consentimento no primeiro acesso, conforme a LGPD (`DOC-1`).
