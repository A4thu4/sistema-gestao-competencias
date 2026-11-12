# UC-02 — Criar ciclo de avaliação

> Objetivo: o RH monta um ciclo de avaliação (tipos de avaliação, período, quem é avaliado, com qual questionário e por quem) e o deixa agendado para abrir sozinho, com convites enviados por e-mail. Formato completo (*fully dressed*) do capítulo 6 do Larman.

| Campo | Valor |
|-------|-------|
| **Responsável** | José Eduardo |
| **Escopo** | Sistema de Gestão de Competências |
| **Nível** | Objetivo do usuário |
| **Ator principal** | RH |
| **Atores de apoio** | Serviço de e-mail (sistema) |
| **Requisitos cobertos** | `RF-08`, `RF-09`, `RF-10`, `RF-12` (convite na abertura), `RF-34`, `RNF-02` |
| **Telas relacionadas** | [TL-02](../12-prototipos/telas/TL-02-criar-ciclo.png) (ver [protótipos](../12-prototipos/README.md#telas)) |

## Interessados e interesses

- **RH:** criar o ciclo sem planilhas nem e-mails manuais; combinar os tipos de avaliação (chefe–colaborador, autoavaliação, pares e 360º); definir o período; escolher quem avalia quem; ter os convites enviados automaticamente.
- **Gestor:** que sua equipe seja avaliada com o questionário do cargo, com avaliadores coerentes com a estrutura da equipe; ser avaliado pelos subordinados (360º) com anonimato.
- **Avaliadores internos (chefe, par, subordinado, o próprio avaliado):** receber o convite no início do ciclo e saber quem devem avaliar, com prazo claro.
- **Avaliador externo (cliente, fornecedor ou parceiro):** receber um link individual, sem precisar de conta, e responder dentro do prazo.
- **Colaborador avaliado:** não escolher os próprios avaliadores, para que o resultado seja confiável.
- **Empresa / TI:** ciclos criados e configurados pelo RH, sem alteração de código nem nova implantação.
- **Encarregado de dados (DPO):** coletar dos avaliadores externos apenas os dados necessários e manter o anonimato das respostas.

## Pré-condições

- O RH está autenticado, com perfil RH, e já aceitou o termo de consentimento.
- Existem pessoas, cargos e estrutura de equipe (quem é gestor de quem) cadastrados no sistema.
- Existe ao menos um questionário publicado (ver [UC-01](UC-01-gerenciar-competencias-questionarios.md)) para os cargos que serão avaliados.

## Pós-condições (garantia de sucesso)

- O ciclo está gravado como **agendado**, com nome, tipos de avaliação, data de abertura e de encerramento (`RF-08`, `RF-09`).
- Cada pessoa avaliada está vinculada ao questionário do seu cargo e tem avaliadores definidos por tipo, escolhidos pelo RH e não pelo avaliado (`RF-10`).
- Cada designação guarda a relação do avaliador com o avaliado (chefe, par, subordinado, autoavaliação ou externo), que o [UC-03](UC-03-responder-avaliacao.md) usa para mostrar só as perguntas dessa relação (`RF-11`).
- Os avaliadores externos convidados estão registrados e receberão um link individual (`RF-34`).
- Na data de abertura, o ciclo passa a **aberto** e cada avaliador recebe o convite por e-mail (`RF-12`).
- Na data de encerramento, novas respostas serão bloqueadas (`RF-09`).
- Os lembretes automáticos aos avaliadores com resposta pendente ficam no UC-07 (`RF-12`, `RF-13`).

**Garantia mínima:** nenhum ciclo incompleto é agendado; o que foi informado permanece como rascunho; nenhum convite é enviado antes da confirmação do RH e da data de abertura; nenhum avaliado fica sem avaliador em algum dos tipos escolhidos.

## Cenário de sucesso principal

1. O RH acessa a área "Ciclos de avaliação".
2. O sistema exibe a lista de ciclos (nome, período, situação — rascunho, agendado, aberto ou encerrado — e quantidade de avaliados) e a ação "Novo ciclo".
3. O RH seleciona "Novo ciclo".
4. O sistema solicita o nome do ciclo, os tipos de avaliação que o compõem (chefe–colaborador, autoavaliação, pares e subordinados avaliando o líder — 360º) e as datas de abertura e de encerramento (`RF-08`, `RF-09`).
5. O RH informa o nome, marca os tipos e define as datas.
6. O sistema valida os dados (nome preenchido, ao menos um tipo, abertura não anterior a hoje, encerramento posterior à abertura) e exibe os cargos e as pessoas disponíveis para serem avaliadas.
7. O RH seleciona quem será avaliado (por cargo, por área ou individualmente) e, para cada cargo, escolhe o questionário publicado.
8. O sistema confere que cada cargo selecionado tem questionário publicado, com perguntas para todos os tipos escolhidos, e sugere os avaliadores de cada avaliado: o chefe e os subordinados a partir da estrutura da equipe, e a própria pessoa na autoavaliação (`RF-10`).
9. O RH revisa e ajusta os avaliadores de cada avaliado, por tipo: chefe, pares e subordinados (`RF-10`).
10. O sistema valida os avaliadores: cada avaliado tem ao menos um avaliador por tipo escolhido, ninguém avalia a si mesmo fora da autoavaliação e todo avaliador tem e-mail válido. Também alerta, sem impedir, onde pares ou subordinados somam menos de 3 por avaliado (`RNF-02`).
11. O sistema exibe o resumo do ciclo (nome, período, tipos, avaliados, questionários, avaliadores por tipo e alertas) e as ações "Agendar ciclo" e "Salvar rascunho".
12. O RH confirma "Agendar ciclo".
13. O sistema grava o ciclo como agendado e exibe a confirmação com a data de abertura.
14. Na data e hora de abertura, o sistema muda o ciclo para aberto e envia, pelo serviço de e-mail, o convite a cada avaliador com o acesso ao preenchimento (`RF-12`). O caso de uso termina.

## Extensões (fluxos alternativos)

- **\*a. A qualquer momento antes do passo 12, o RH interrompe o preenchimento ou a sessão expira:**
  1. O sistema mantém o que foi informado como rascunho, sem agendar e sem enviar convites.
  2. O RH retoma pela lista de ciclos (passo 2), selecionando o rascunho (2a).
- **\*b. A qualquer momento, falha ao gravar:**
  1. O sistema informa o erro, mantém o rascunho e não agenda o ciclo.
  2. O RH tenta novamente; o caso de uso retorna ao passo em que estava.
- **1a. O usuário não tem perfil RH:**
  1. O sistema nega o acesso à área, com a verificação feita no servidor.
  2. O caso de uso termina.
- **2a. O RH seleciona um ciclo existente em rascunho ou agendado:**
  1. O sistema abre o ciclo com os dados já informados.
  2. Segue no passo 4 para ajustar tipos, datas, avaliados e avaliadores; o agendamento ocorre no passo 12.
- **2b. O RH seleciona um ciclo já aberto (regra proposta):**
  1. O sistema bloqueia a alteração de tipos, questionários e data de abertura, para não invalidar respostas já enviadas.
  2. O sistema permite apenas ajustar a data de encerramento e incluir novos avaliadores, que recebem o convite na hora (`RF-09`, `RF-12`).
  3. O caso de uso termina após a gravação.
- **2c. O RH seleciona um ciclo encerrado:**
  1. O sistema exibe o ciclo somente para consulta, sem permitir alterações.
  2. O caso de uso termina.
- **6a. Os dados do passo 5 são inválidos** (nome vazio, nenhum tipo marcado, abertura anterior a hoje, encerramento anterior ou igual à abertura):
  1. O sistema destaca cada pendência com mensagem em linguagem simples e mantém o que foi informado.
  2. Volta ao passo 5.
- **8a. Um cargo selecionado não tem questionário publicado:**
  1. O sistema informa quais cargos estão sem questionário e oferece remover esses cargos do ciclo ou ir ao [UC-01](UC-01-gerenciar-competencias-questionarios.md) para publicá-lo, mantendo o rascunho do ciclo.
  2. Volta ao passo 7.
- **8b. O questionário do cargo não tem perguntas para um dos tipos escolhidos** (por exemplo, ciclo com pares, mas nenhuma pergunta aplicada a pares):
  1. O sistema informa o cargo e o tipo sem perguntas e oferece retirar o tipo do ciclo ou ajustar o questionário no UC-01.
  2. Volta ao passo 4 ou 7, conforme a escolha do RH.
- **9a. O sistema não consegue sugerir o chefe ou os subordinados de uma pessoa** (estrutura da equipe incompleta):
  1. O sistema marca a pessoa como pendente de avaliadores.
  2. O RH indica os avaliadores manualmente; segue no passo 9.
- **9b. O RH quer convidar avaliadores externos (`RF-34`):**
  1. O RH escolhe o avaliado e informa o nome e o e-mail de cada avaliador externo.
  2. O sistema valida o e-mail, registra o avaliador externo no ciclo e prepara o link individual, que será enviado na abertura, sem exigir conta e válido até o envio da avaliação ou o encerramento do ciclo, como no UC-03.
  3. Volta ao passo 9.
- **10a. Um avaliado tem menos de 3 pares ou menos de 3 subordinados (`RNF-02`):**
  1. O sistema alerta que os resultados agregados desse grupo não serão exibidos enquanto houver menos de 3 respostas.
  2. O RH inclui mais avaliadores (volta ao passo 9) ou mantém assim (segue no passo 11).
- **10b. A validação encontra pendências** (avaliado sem avaliador em algum tipo, pessoa indicada como avaliadora de si mesma fora da autoavaliação, e-mail inválido ou ausente):
  1. O sistema impede o agendamento, destaca cada pendência com mensagem em linguagem simples e mantém o rascunho.
  2. Volta ao passo 9.
- **12a. O RH prefere não agendar agora:**
  1. O sistema grava o ciclo como rascunho.
  2. O caso de uso termina.
- **13a. A data de abertura é o dia atual:**
  1. O sistema abre o ciclo imediatamente após a gravação.
  2. Segue no passo 14.
- **14a. O serviço de e-mail está indisponível ou o envio de um convite falha:**
  1. O sistema mantém o ciclo aberto, tenta reenviar automaticamente e registra os convites não enviados.
  2. O sistema exibe ao RH a lista de convites pendentes para reenvio manual; o caso de uso termina.

## Requisitos especiais

- **Controle de acesso:** apenas o perfil RH acessa o caso de uso, com verificação feita no servidor (`RF-33`, `RNF-03`).
- **Avaliadores definidos pelo RH (`RF-10`):** o avaliado não escolhe nem altera os próprios avaliadores.
- **Anonimato (`RNF-01`, `RNF-02`):** o RH define quem avalia quem, mas nenhum perfil vê a autoria das respostas de pares e subordinados; resultados agregados só aparecem com no mínimo 3 respostas no grupo. As mesmas regras valem para os avaliadores externos.
- **Prazo (`RF-09`):** após a data de encerramento, o sistema bloqueia novas respostas.
- **Minimização de dados (`RNF-06`):** do avaliador externo são coletados apenas nome e e-mail.
- **Linguagem simples (`RNF-09`):** mensagens de validação, alertas e convites sem jargão técnico.
- **Desempenho (`RNF-11`):** o envio de convites na abertura, para toda a organização, não deve degradar o sistema.
- **Transversais:** comunicação por HTTPS e dados armazenados criptografados (`RNF-04`); interface responsiva (`RNF-07`).

## Lista de variações de tecnologia e dados

- Tipos de avaliação do ciclo, combináveis: chefe–colaborador, autoavaliação, pares e subordinados avaliando o líder (`RF-08`).
- Seleção dos avaliados: por cargo, por área ou individual.
- Avaliador interno (com conta) ou externo (link individual por e-mail, sem conta) (`RF-34`).
- Início do ciclo: agendado para data futura ou imediato.
- Definição dos avaliadores: sugestão automática pela estrutura da equipe ou indicação manual do RH.
- Situação do ciclo: rascunho, agendado, aberto ou encerrado.

## Frequência de ocorrência

Uma vez por ciclo de avaliação, preparado com antecedência, antes da abertura. Hoje o ciclo é anual (em novembro, segundo `ENT-1`); `QUE-2` prefere duas vezes por ano e `QUE-1` quer feedback contínuo, então pode haver mais de um ciclo no ano. As alterações em ciclo aberto (2b) são raras e pontuais.

## Questões em aberto

- **Estrutura da organização:** nenhum documento do repositório define como pessoas, áreas e a relação gestor–equipe entram no sistema (cadastro manual, importação ou conta corporativa); os passos 7, 8 e 9 dependem disso. É a mesma lacuna dos cargos no UC-01. Proposta: tratar como cadastro mantido pelo RH, fora do escopo destes casos de uso, e registrar como suposição na ERS.
- **Quem define os avaliadores:** `SE-22` (`ENT-2`) diz "o RH ou o gestor", mas o diagrama liga só o RH ao UC-02. Adotado: só o RH. Se o gestor também definir, é preciso ajustar o diagrama e este caso de uso.
- **Avaliador externo:** o consentimento (UC-05), as perguntas da relação "externo" (UC-03) e o grupo próprio nos resultados (UC-04) já estão como questões nesses casos de uso. Aqui falta confirmar só que o externo é designado pelo RH para cada avaliado, como adotado no passo 9b, e que isso não vira um quinto tipo no `RF-08`.
- **Anonimato × designação:** o sistema precisa guardar quem foi designado para avaliar quem, e `RF-13` mostra quem já respondeu. Confirmar que isso é compatível com `RNF-01`, desde que nenhuma resposta seja ligada ao avaliador.
- **Congelamento do questionário:** definir se o questionário fica bloqueado ao agendar o ciclo ou só ao abrir; o UC-01 (extensão 2b) bloqueia apenas quando já há respostas.
- **Ciclo aberto:** confirmar a regra proposta em 2b e se o RH pode cancelar um ciclo.
- **Mínimo de 3 respostas:** `ENT-1` e os dois questionários (4.4) pedem o mínimo de 3 para exibir resultados, não para montar o ciclo. Adotado: o passo 10 só alerta. Confirmar se deve bloquear; equipes pequenas nos relatórios são tratadas no UC-04.
- **Duplicar ciclo anterior:** `SE-08` pede reaproveitar só o questionário (`RF-07`); como os ciclos se repetem, avaliar um requisito para copiar um ciclo anterior (avaliados e avaliadores).
- **Auditoria:** o log de `RNF-05` não cobre criação nem alteração de ciclo; confirmar se é necessário para este caso de uso.
