# ANA: Síntese dos requisitos do sistema análogo

Requisitos derivados das funcionalidades do SurveyMonkey descritas em [funcionalidades.md](funcionalidades.md). Todos têm como fonte `ANA`. Cada item recebe um identificador `SA-NN`, usado na [lista geral de requisitos](../08-requisitos/lista-requisitos.md).

Responsável: Lorenzo Oliveira Maciel

## 1. Requisitos identificados

| ID | Tipo | Requisito identificado | Fonte | Funcionalidade de origem |
|----|------|------------------------|-------|--------------------------|
| SA-01 | RF | Oferecer modelos de questionário prontos (chefe–colaborador, pares e 360º) que o RH possa personalizar. | `ANA` | F-01, F-07 |
| SA-02 | RF | Agrupar as perguntas por competência e adaptá-las por cargo ou nível. | `ANA` | F-02, F-07 |
| SA-03 | RF | Permitir perguntas de escala (por exemplo, de 1 a 5) e perguntas abertas. | `ANA` | F-03, F-04 |
| SA-04 | RF | Apresentar a cada avaliador apenas as perguntas da sua relação com o avaliado (chefe, colega, subordinado ou autoavaliação). | `ANA` | F-05 |
| SA-05 | RNF | Permitir respostas anônimas para pares e subordinados. | `ANA` | F-06 |
| SA-06 | RNF | Exibir resultados de grupo apenas com um número mínimo de respostas (por exemplo, 3). | `ANA` | F-06 |
| SA-07 | RF | Enviar convites por e-mail e lembretes automáticos de prazo. | `ANA` | F-08 |
| SA-08 | RF | Gerar resumo automático dos resultados por pessoa, com filtros. | `ANA` | F-09 |
| SA-09 | RF | Oferecer painéis e relatórios por pessoa, equipe e departamento sem expor quem respondeu. | `ANA` | F-10, F-11 |
| SA-10 | RF | Comparar a autoavaliação com a avaliação dos demais. | `ANA` | F-12 |
| SA-11 | RF | Exportar os resultados (por exemplo, em PDF e Excel). | `ANA` | F-13 |
| SA-12 | RF | Registrar metas de desenvolvimento a partir dos resultados da avaliação. | `ANA` | F-16 |
| SA-13 | RF | Guardar o histórico dos ciclos para mostrar a evolução ao longo do tempo. | `ANA` | F-15 |
| SA-14 | RNF | Permitir personalizar a identidade visual do questionário (marca, cores). | `ANA` | F-14 |
| SA-15 | RNF | Oferecer acesso e administração seguros por perfil de usuário. | `ANA` | F-17 |

## 2. Diferenciais do nosso sistema

Pontos em que nosso sistema deve ir além do análogo, segundo as [limitações levantadas](funcionalidades.md#3-limitações-em-relação-ao-nosso-sistema). Eles não geram itens `SA`, pois não vêm do análogo, mas estão cobertos por outras fontes:

| Diferencial | Coberto por |
|-------------|-------------|
| Catálogo de competências próprio por cargo, dentro do sistema | `SE-02`, `SE-03` |
| Acompanhamento contínuo das metas, com prazos e lembretes | `SE-12`, `SQ-06` |
| Controle de acesso por perfil: RH, gestor (somente a própria equipe) e colaborador (somente o próprio resultado) | `SE-13`, `DOC-1` |
| Termo de consentimento no primeiro acesso, conforme a LGPD | `SE-15`, `SQ-10`, `DOC-1` |

Total: 15 itens (11 funcionais e 4 não funcionais).
