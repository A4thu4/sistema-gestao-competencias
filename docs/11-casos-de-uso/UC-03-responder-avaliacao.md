# UC-03 — Responder avaliação

| Campo | Valor |
|-------|-------|
| **Responsável** | Gabriel Araújo Caetano |
| **Escopo** | Sistema de Gestão de Competências |
| **Nível** | Objetivo do usuário |
| **Ator principal** | Avaliador: Gestor, Colaborador ou Avaliador externo |
| **Requisitos cobertos** | `RF-06`, `RF-11`, `RF-14` a `RF-18`, `RF-25`, `RNF-01`, `RNF-08` |
| **Telas relacionadas** | [TL-03](../12-prototipos/telas/TL-03-responder-avaliacao.png) (ver [protótipos](../12-prototipos/README.md#telas)) |

## Interessados e interesses

- **Avaliador (gestor, colaborador ou externo):** responder rápido, sem treinamento; poder interromper e continuar depois; entender o que cada nota significa; ter a garantia de que a resposta a pares e ao líder é anônima, para poder ser sincero.
- **Avaliado:** ser avaliado pelas competências do seu cargo, com critérios conhecidos, com base em fatos do período e não só na memória recente, e receber comentários que ajudem no feedback.
- **RH:** receber avaliações completas, sem campos em branco, dentro do prazo do ciclo e sem alterações depois do envio que não passem por ele.
- **Encarregado de dados (DPO):** coleta só do necessário, anonimato preservado e dados protegidos conforme a LGPD.

## Pré-condições

- Existe um ciclo de avaliação aberto (UC-02) em que o ator é avaliador designado de pelo menos uma pessoa.
- Gestor e colaborador estão autenticados (UC-10) e aceitaram o termo de consentimento (UC-05).
- O avaliador externo tem um link individual válido, recebido por e-mail (`RF-34`).

## Pós-condições (garantia de sucesso)

- As respostas ficam gravadas e ligadas ao ciclo, ao avaliado e à relação do avaliador com ele (chefe, par, subordinado, autoavaliação ou externo).
- A avaliação fica marcada como enviada e bloqueada para edição (`RF-16`).
- A autoria das respostas de pares, subordinados e externos não fica visível a nenhum perfil (`RNF-01`).
- O andamento do ciclo é atualizado (`RF-13`) e o avaliador deixa de receber lembretes sobre essa avaliação (`RF-12`).

## Cenário de sucesso principal

1. O avaliador acessa a área de avaliações do ciclo.
2. O sistema exibe as avaliações sob responsabilidade dele, com o nome do avaliado, a relação (chefe, par, subordinado ou autoavaliação), a situação (não iniciada, em preenchimento ou enviada) e a data de encerramento do ciclo.
3. O avaliador escolhe uma avaliação não enviada.
4. O sistema apresenta o questionário do cargo do avaliado, agrupado por competência, só com as perguntas da relação do avaliador com ele (`RF-11`), e mostra a descrição de cada nível da escala (`RF-25`). Quando o avaliador é o gestor do avaliado, o sistema exibe também as observações registradas no período (`RF-17`, UC-08) e o resultado do ciclo anterior (`RF-18`).
5. O avaliador dá uma nota a cada item ou marca "não tenho como avaliar" (`RF-06`), e escreve os comentários nas perguntas abertas.
6. O sistema salva automaticamente cada resposta (`RF-14`) e sinaliza, no próprio item, os campos obrigatórios ainda não preenchidos (`RF-15`).
7. O avaliador termina o preenchimento e pede o envio.
8. O sistema confere que não há campos obrigatórios pendentes e pede confirmação, avisando que a avaliação não poderá ser alterada depois do envio.
9. O avaliador confirma.
10. O sistema registra a avaliação como enviada, bloqueia a edição (`RF-16`), atualiza o andamento do ciclo e volta à lista de avaliações, com a situação atualizada.

O avaliador repete os passos 3 a 10 para as demais avaliações pendentes.

## Extensões (fluxos alternativos)

- **\*a. A qualquer momento, o avaliador interrompe o preenchimento (fecha o navegador, perde a conexão ou sai da tela):**
  1. O sistema mantém as respostas já salvas (`RF-14`).
  2. Quando o avaliador volta, o sistema reabre a avaliação com as respostas preenchidas; continua no passo 5.
- **\*b. A qualquer momento, o ciclo atinge a data de encerramento:**
  1. O sistema bloqueia novas respostas e o envio (`RF-09`) e informa o avaliador de que o ciclo foi encerrado.
  2. O caso de uso termina; a avaliação não enviada fica como não respondida.
- **1a. O ator é um avaliador externo:**
  1. O avaliador abre o link individual recebido por e-mail, sem login (`RF-34`).
  2. O sistema valida o link e abre direto a avaliação correspondente; continua no passo 4, com as perguntas da relação "externo".
  3. Se o link for inválido, se a avaliação já tiver sido enviada ou se o ciclo estiver encerrado, o sistema informa o motivo e não exibe o questionário; o caso de uso termina.
- **2a. O avaliador não tem avaliações pendentes:**
  1. O sistema informa que todas as avaliações do ciclo já foram enviadas; o caso de uso termina.
- **4a. A avaliação é uma autoavaliação:**
  1. O sistema apresenta as perguntas de autoavaliação, sem as observações registradas pelo gestor; continua no passo 5.
- **4b. O avaliado não tem resultado de ciclo anterior:**
  1. O sistema informa que não há resultado anterior; continua no passo 5.
- **8a. Há campos obrigatórios pendentes:**
  1. O sistema impede o envio, lista os itens pendentes e leva o avaliador ao primeiro deles (`RF-15`).
  2. Volta ao passo 5.
- **9a. O avaliador desiste do envio:**
  1. O sistema mantém a avaliação em preenchimento, com as respostas salvas; volta ao passo 5.
- **10a. Depois do envio, o avaliador percebe um erro:**
  1. O sistema não permite a alteração (`RF-16`).
  2. O avaliador pede ao RH a reabertura, que é tratada no UC-09.

## Requisitos especiais

- **Tempo de preenchimento (`RNF-08`):** avaliar uma pessoa em até 10 minutos e concluir a autoavaliação em até 15 minutos, sem treinamento.
- **Anonimato (`RNF-01`):** a autoria das respostas de pares, subordinados e externos nunca aparece para nenhum perfil, inclusive o RH. A tela de preenchimento deve deixar claro ao avaliador quando a resposta é anônima.
- **Visibilidade pelo servidor (`RNF-03`):** o avaliador só acessa as avaliações para as quais foi designado; observações e resultado anterior só aparecem para o gestor do avaliado.
- **Celular (`RNF-07`):** o preenchimento deve funcionar em celular e tablet, já que as equipes de operação e suporte não usam computador.
- **Segurança (`RNF-04`):** respostas trafegam por HTTPS e ficam armazenadas criptografadas.
- **Pico no encerramento (`RNF-11`):** as telas devem carregar em até 3 segundos mesmo com toda a organização respondendo no último dia.
- **Linguagem (`RNF-09`):** mensagens e avisos em linguagem simples.

## Lista de variações de tecnologia e dados

- Acesso por login individual ou conta corporativa (UC-10); avaliador externo por link individual, sem conta.
- Navegador em computador, tablet ou celular.
- Escala de 1 a 5 ou de 1 a 10, conforme o questionário (`RF-04`).
- Respostas de escala (nota ou "não tenho como avaliar") e de texto livre (`RF-05`).

## Frequência de ocorrência

Uma vez por avaliação designada em cada ciclo. Cada pessoa costuma ter várias: o gestor avalia todos da equipe direta, e o colaborador faz a autoavaliação e avalia pares e o líder. A maior parte acontece perto da data de encerramento do ciclo.

## Questões em aberto

- **Resultado do ciclo anterior (`RF-18`):** o requisito diz "ao avaliador", mas a fonte (`SE-21`) é a entrevista com o gestor, e mostrar o resultado a pares conflitaria com `RNF-03`. Adotamos a exibição só ao gestor do avaliado; confirmar com o RH.
- **Respostas salvas e não enviadas no encerramento:** adotamos que a avaliação fica como não respondida e não entra na consolidação; confirmar com o RH se ela deve ser descartada ou considerada.
- **Perguntas para avaliadores externos:** o `RF-11` lista as relações chefe, par, subordinado e autoavaliação, sem citar a relação "externo". Confirmar se o questionário terá um bloco próprio para externos e ajustar o `RF-11`, se for o caso.
- **Link do avaliador externo:** adotamos que o link pode ser aberto várias vezes até o envio, para permitir continuar depois (`RF-14`); confirmar se precisa de prazo de validade.
