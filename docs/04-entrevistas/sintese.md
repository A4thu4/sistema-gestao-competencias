# Síntese dos requisitos das entrevistas

Requisitos identificados nas entrevistas [ENT-1](entrevista-1.md) (Gerente de RH) e [ENT-2](entrevista-2.md) (Coordenador de TI, gestor de equipe). Cada item recebe um identificador `SE-NN`, usado na [lista geral de requisitos](../08-requisitos/lista-requisitos.md) para rastrear a origem.

Responsável: Gabriel Araújo Caetano

## 1. Visão dos entrevistados

| Aspecto | ENT-1 (RH) | ENT-2 (gestor) |
|---------|------------|----------------|
| Papel na avaliação | Organiza o ciclo e consolida os resultados | Avalia a equipe e é avaliado pelo diretor |
| Principal dor | Consolidação manual de cerca de 3 semanas, planilhas com erros e circulando por e-mail | Planilha longa (mais de 30 competências) e avaliação baseada na memória recente |
| O que mais valoriza | Anonimato, controle de prazos e acompanhamento contínuo | Comentários escritos, comparação com a autoavaliação e registro ao longo do ano |
| Resultado esperado | Fechar o ciclo em uma semana e acompanhar a evolução das pessoas | Avaliar cada pessoa em cerca de 10 minutos |

## 2. Requisitos identificados

| ID | Tipo | Requisito identificado | Fonte | Evidência |
|----|------|------------------------|-------|-----------|
| SE-01 | RF | Consolidar automaticamente as respostas das avaliações, eliminando a consolidação manual em planilha. | `ENT-1` | A gerente de RH gasta cerca de três semanas juntando planilhas à mão. |
| SE-02 | RF | Manter um catálogo de competências técnicas e comportamentais que o RH cadastra e altera sem depender da TI. | `ENT-1` | Hoje o catálogo fica em um documento Word. |
| SE-03 | RF | Associar a cada cargo um conjunto próprio de competências. | `ENT-1` | Cada cargo usa um conjunto diferente de competências. |
| SE-04 | RF | Permitir os tipos de avaliação chefe–colaborador, autoavaliação, pares e equipe avaliando o líder (360º). | `ENT-1` `ENT-2` | Hoje só o chefe avalia; ambos veem valor na visão dos colegas e da equipe. |
| SE-05 | RNF | Manter anônima a autoria das respostas de pares e subordinados, exibindo-as apenas agregadas. | `ENT-1` `ENT-2` | Sem anonimato o avaliador não é honesto. |
| SE-06 | RNF | Exibir resultados de grupo somente quando houver no mínimo 3 respondentes. | `ENT-1` | Com dois avaliadores todos deduzem quem respondeu. |
| SE-07 | RF | Criar questionários por cargo, escolher a escala de nota (1 a 5 ou 1 a 10) e incluir perguntas abertas. | `ENT-1` | Os questionários mudam bastante entre ciclos. |
| SE-08 | RF | Reaproveitar o questionário do ciclo anterior, ajustando apenas o que mudou. | `ENT-1` | Evita refazer tudo a cada ano. |
| SE-09 | RF | Exibir painel por pessoa com média por competência, comparando chefe, autoavaliação e média dos pares, com gráficos. | `ENT-1` `ENT-2` | A conversa de feedback nasce da diferença entre as visões; tabelas sem gráfico não são lidas. |
| SE-10 | RF | Gerar relatórios por equipe e por área com médias por competência. | `ENT-1` `ENT-2` | RH e gestor querem ver onde o grupo está mais fraco. |
| SE-11 | RF | Exportar os resultados em PDF e Excel. | `ENT-1` | Necessidade direta do RH. |
| SE-12 | RF | Registrar metas de desenvolvimento (inclusive cursos) com prazo e lembrar o gestor de revisá-las a cada trimestre. | `ENT-1` `ENT-2` | Hoje o assunto só volta no ciclo seguinte. |
| SE-13 | RNF | Garantir pelo sistema a visibilidade por perfil: RH vê tudo (sem autoria nos anônimos), gestor só a equipe direta, colaborador só o próprio resultado. | `ENT-1` `ENT-2` | Não pode depender de combinação entre as pessoas. |
| SE-14 | RF | Definir datas de abertura e encerramento do ciclo, acompanhar quem respondeu e enviar lembretes automáticos por e-mail. | `ENT-1` | O RH persegue gestores atrasados à mão. |
| SE-15 | RF | Apresentar termo de consentimento no primeiro acesso, informando a finalidade dos dados. | `ENT-1` | Empresa em adequação à LGPD. |
| SE-16 | RNF | Proteger os dados armazenados e registrar os acessos. | `ENT-1` | Preocupação com LGPD. |
| SE-17 | RNF | Funcionar em celular. | `ENT-1` `ENT-2` | Equipes de operação e suporte não usam computador. |
| SE-18 | RNF | Suportar o pico de acessos no dia de encerramento do ciclo sem travar. | `ENT-1` | Todos acessam no mesmo dia. |
| SE-19 | RNF | Ser simples: avaliar uma pessoa em cerca de 10 minutos. | `ENT-1` `ENT-2` | Preenchimento pesado leva ao abandono. |
| SE-20 | RF | Registrar observações (pontos positivos e de melhoria) ao longo do ano e exibi-las ao avaliar. | `ENT-2` | O gestor avalia pelo que lembra dos últimos meses. |
| SE-21 | RF | Exibir ao avaliador a avaliação do ciclo anterior da pessoa. | `ENT-2` | Permite comparar com o ano anterior. |
| SE-22 | RF | Permitir que o RH ou o gestor defina quem avalia quem; o avaliado não escolhe. | `ENT-2` | Evita disputas na escolha dos avaliadores. |
| SE-23 | RF | Exibir comentários escritos sem identificar o autor e permitir que o RH remova comentário ofensivo. | `ENT-2` | Os comentários são o que mais orienta o feedback. |
| SE-24 | RF | Impedir alteração da avaliação após o envio; somente o RH reabre, registrando quem reabriu e o motivo. | `ENT-2` | Sem esse controle a avaliação perde credibilidade. |
| SE-25 | RF | Autenticar com login individual, com opção de entrar com a conta corporativa. | `ENT-2` | Evitar mais uma senha. |
| SE-26 | RF | Salvar o progresso da avaliação para continuar depois. | `ENT-2` | O gestor pode precisar interromper o preenchimento. |
| SE-27 | RF | Avisar sobre campo obrigatório não preenchido durante o preenchimento, e não só no envio. | `ENT-2` | Erros só no fim frustram o usuário. |
| SE-28 | RF | Guardar o histórico para acompanhar a evolução de cada pessoa ao longo dos anos. | `ENT-1` | Objetivo declarado pela gerente de RH. |

## 3. Pontos de convergência e de atenção

- **Convergência:** os dois entrevistados pedem 360º, anonimato, visibilidade restrita por perfil, acompanhamento de metas e uso em celular.
- **Comentários anônimos:** ENT-2 quer ler os comentários, mas sem autor; isso exige cuidado para que o texto não identifique quem escreveu (tratado em `SE-23`).
- **Escolha dos avaliadores:** ENT-2 alerta que o avaliado não deve escolher quem o avalia (`SE-22`).
- **Integridade:** ENT-2 pede que a avaliação enviada não seja alterada, nem pelo próprio gestor (`SE-24`).

Total: 28 itens (21 funcionais e 7 não funcionais).
