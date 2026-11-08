# UC-01 — Gerenciar competências e questionários

> Objetivo: o RH deixa pronto, sem depender da TI, o questionário de um cargo (competências, perguntas e escala) para ser usado nos ciclos de avaliação. Formato completo (*fully dressed*) do capítulo 6 do Larman.

| Campo | Valor |
|-------|-------|
| **Responsável** | José Eduardo |
| **Escopo** | Sistema de Gestão de Competências |
| **Nível** | Objetivo do usuário |
| **Ator principal** | RH |
| **Requisitos cobertos** | `RF-01` a `RF-07`, `RNF-13` |
| **Telas relacionadas** | A definir (ver [protótipos](../12-prototipos)) |

## Interessados e interesses

- **RH:** cadastrar e alterar competências e questionários por conta própria, sem depender da TI; montar questionários diferentes por cargo; escolher a escala; reaproveitar o questionário do ciclo anterior em vez de refazer tudo.
- **Gestor:** receber questionários enxutos e adequados ao cargo da equipe, que possam ser preenchidos em cerca de 10 minutos por pessoa.
- **Avaliadores (gestor, colaborador, par, subordinado):** perguntas claras e descrição de cada nível da escala, para que todos avaliem do mesmo jeito; opção "não tenho como avaliar" quando não convivem com a pessoa.
- **Colaborador avaliado:** conhecer, desde a abertura do ciclo, as competências e os níveis em que será avaliado.
- **Empresa / TI:** mudanças de competências, perguntas e escalas sem alteração de código nem nova implantação.

## Pré-condições

- O RH está autenticado, com perfil RH, e já aceitou o termo de consentimento.
- Existem cargos disponíveis no sistema para associar às competências e aos questionários.

## Pós-condições (garantia de sucesso)

- O questionário está gravado como **publicado**, com nome, cargo, competências, perguntas (de escala e abertas), escala e descrição de todos os níveis (`RF-03`, `RF-04`, `RF-05`).
- O questionário fica disponível para ser escolhido na criação de ciclos.
- As competências usadas estão no catálogo e associadas ao cargo (`RF-01`, `RF-02`).
- A alteração vale imediatamente, sem mudança de código ou nova implantação (`RNF-13`).

**Garantia mínima:** nenhum questionário incompleto é publicado; o que foi informado permanece como rascunho; questionários que já têm respostas não são alterados; competências desativadas não são apagadas, preservando o histórico.

## Cenário de sucesso principal

1. O RH acessa a área "Competências e questionários".
2. O sistema exibe a lista de questionários (nome, cargo, situação — rascunho ou publicado — e ciclos em que foi usado) e a ação "Novo questionário".
3. O RH seleciona "Novo questionário".
4. O sistema oferece três pontos de partida: questionário em branco, modelo pronto (chefe–colaborador, pares ou 360º) ou cópia de um questionário de ciclo anterior (`RF-07`).
5. O RH escolhe "em branco".
6. O sistema solicita o nome do questionário e o cargo a que se destina (`RF-03`).
7. O RH informa o nome e o cargo.
8. O sistema exibe as competências ativas do catálogo, técnicas e comportamentais, com as já associadas ao cargo pré-selecionadas (`RF-01`, `RF-02`).
9. O RH seleciona ou ajusta as competências do questionário.
10. Para cada competência selecionada, o RH adiciona as perguntas, indica a quais tipos de avaliador cada pergunta se aplica (chefe, autoavaliação, par, subordinado) e, se desejar, inclui perguntas abertas de texto livre (`RF-03`, `RF-05`).
11. O RH define a escala de notas (1 a 5 ou 1 a 10) e descreve cada nível (`RF-04`).
12. O sistema valida que o questionário está completo: nome, cargo, ao menos uma competência, ao menos uma pergunta por competência e descrição de todos os níveis da escala.
13. O sistema exibe a pré-visualização como o avaliador verá, com a opção "não tenho como avaliar" em cada item de escala, que não entra no cálculo da média (`RF-06`).
14. O RH confirma a publicação.
15. O sistema grava o questionário como publicado, torna-o disponível para a criação de ciclos e exibe a confirmação. O caso de uso termina.

## Extensões (fluxos alternativos)

- **\*a. A qualquer momento, o RH interrompe o preenchimento ou a sessão expira:**
  1. O sistema mantém o que foi informado como rascunho, sem publicar.
  2. O RH retoma pela lista de questionários (passo 2), selecionando o rascunho (2a).
- **\*b. A qualquer momento, falha ao gravar:**
  1. O sistema informa o erro, mantém o rascunho e não publica o questionário.
  2. O RH tenta novamente; o caso de uso retorna ao passo em que estava.
- **1a. O usuário não tem perfil RH:**
  1. O sistema nega o acesso à área, com a verificação feita no servidor.
  2. O caso de uso termina.
- **2a. O RH seleciona um questionário existente, em rascunho ou sem respostas:**
  1. O sistema abre o questionário com os dados já informados.
  2. Segue no passo 8 para ajustar competências, perguntas e escala; a publicação ocorre no passo 14.
- **2b. O questionário selecionado já tem respostas em algum ciclo (regra proposta):**
  1. O sistema bloqueia a alteração de competências, perguntas e escala, para preservar o histórico e o cálculo das médias.
  2. O sistema oferece duplicá-lo como um novo questionário.
  3. Se o RH aceitar, segue o fluxo do passo 5b com a cópia; caso contrário, o caso de uso termina.
- **2c. O RH quer gerenciar o catálogo de competências:**
  1. O sistema exibe o catálogo com as competências ativas e inativas, cada uma com nome, descrição e tipo (técnica ou comportamental).
  2. O RH edita uma competência, desativa uma ativa ou reativa uma inativa (`RF-01`).
  3. Ao desativar uma competência presente em questionários publicados, o sistema lista os questionários afetados e pede confirmação.
  4. O sistema valida (nome e descrição obrigatórios), grava e passa a refletir a mudança nos próximos questionários, sem apagar o que já foi respondido.
  5. O RH volta ao passo 2 ou o caso de uso termina.
- **5a. O RH escolhe um modelo pronto:**
  1. O sistema carrega o modelo (competências, perguntas e escala) como um novo rascunho (`RF-07`).
  2. Segue no passo 6 com os campos pré-preenchidos; o RH personaliza nos passos 7 a 11.
- **5b. O RH escolhe copiar um questionário de ciclo anterior:**
  1. O sistema lista os questionários anteriores; o RH seleciona um.
  2. O sistema cria um novo rascunho com a estrutura completa do original, sem respostas (`RF-07`).
  3. Segue no passo 6; o RH ajusta apenas o que mudou.
- **9a. A competência necessária não existe no catálogo:**
  1. O RH cadastra a nova competência (nome, descrição e tipo) sem sair do fluxo (`RF-01`).
  2. O sistema valida, inclui no catálogo e a seleciona para o questionário.
  3. Volta ao passo 9.
- **9b. O nome da nova competência já existe no catálogo:**
  1. O sistema avisa da duplicidade e sugere usar a competência existente.
  2. Volta ao passo 9a ou 9.
- **9c. O RH quer que a seleção valha como conjunto padrão do cargo:**
  1. O RH marca a opção de usar a seleção como padrão do cargo.
  2. O sistema atualiza a associação entre o cargo e as competências (`RF-02`).
  3. Segue no passo 10.
- **12a. A validação encontra pendências** (competência sem pergunta, nível sem descrição, nome ou cargo vazio):
  1. O sistema impede a publicação, destaca cada pendência com mensagem em linguagem simples e mantém o rascunho.
  2. Volta ao passo 10 ou 11, conforme a pendência.
- **14a. O RH prefere não publicar agora:**
  1. O sistema grava o questionário como rascunho.
  2. O caso de uso termina.

## Requisitos especiais

- **Configuração pela interface (`RNF-13`):** competências, questionários e escalas são criados e alterados pelo RH na interface, sem alteração de código ou nova implantação.
- **Descrição de níveis obrigatória (`RF-04`):** o questionário só é publicado com todos os níveis da escala descritos.
- **Controle de acesso:** apenas o perfil RH acessa o caso de uso; a verificação é feita no servidor.
- **Preservação do histórico (regra proposta):** questionários com respostas não são alterados; competências são desativadas, nunca apagadas.
- **Linguagem simples:** mensagens de validação e avisos sem jargão técnico.
- **Transversais:** comunicação por HTTPS e dados armazenados criptografados (`RNF-04`); interface responsiva (`RNF-07`).

## Lista de variações de tecnologia e dados

- Escala de 1 a 5 ou de 1 a 10 (`RF-04`).
- Ponto de partida: em branco, modelo pronto (chefe–colaborador, pares, 360º) ou cópia de ciclo anterior (`RF-07`).
- Tipo de competência: técnica ou comportamental.
- Tipo de pergunta: de escala ou aberta (texto livre) (`RF-05`).
- Aplicação da pergunta por tipo de avaliador: chefe, autoavaliação, par ou subordinado.

## Frequência de ocorrência

Uma vez por cargo a cada ciclo de avaliação, geralmente antes da abertura do ciclo; os questionários mudam bastante entre ciclos. A manutenção do catálogo de competências é pontual, entre ciclos.

## Questões em aberto

- **Origem dos cargos:** nenhum requisito define como os cargos entram no sistema (cadastro manual, importação ou conta corporativa); `RF-02` e `RF-03` dependem disso.
- **Perguntas por tipo de avaliador:** para que o UC-03 mostre a cada avaliador só as perguntas da sua relação, o RH precisa classificar as perguntas ao montar o questionário (passo 10). Essa classificação não está descrita em `RF-03`; confirmar com o grupo se o texto do requisito deve ser ajustado.
- **Versionamento:** confirmar a regra assumida em 2b (questionário com respostas fica bloqueado e é duplicado) e se a edição de nome ou descrição de uma competência deve afetar questionários já publicados.
- **Perguntas obrigatórias:** o sistema deve impedir o envio com campos obrigatórios em branco, mas nenhum requisito diz se o RH marca cada pergunta como obrigatória ou opcional.
- **Opção "não tenho como avaliar" (`RF-06`):** assumida como sempre disponível em itens de escala; confirmar se o RH deve poder desativá-la por item.
- **Conteúdo dos modelos prontos (`RF-07`):** definir quais competências e perguntas cada modelo traz e quem as define.
- **Tamanho do questionário:** a dor D-12 (mais de 30 competências por pessoa) e a meta de 10 minutos por pessoa sugerem avisar o RH quando o questionário ficar extenso; hoje não há requisito para isso.
- **Rascunho e auditoria:** o salvamento automático previsto nos requisitos vale só para avaliações, e o log de auditoria não cobre alterações de catálogo ou questionário; confirmar se são necessários para este caso de uso.
