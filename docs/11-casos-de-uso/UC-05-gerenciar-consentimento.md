# UC-05 — Gerenciar consentimento e dados pessoais

| Campo | Valor |
|-------|-------|
| **Responsável** | Arthur Mamedes Borges |
| **Escopo** | Sistema de Gestão de Competências |
| **Nível** | Objetivo do usuário |
| **Ator principal** | Usuário (RH, Gestor ou Colaborador) |
| **Requisitos cobertos** | `RF-29`, `RF-30`, `RNF-06`, `RNF-09` |
| **Telas relacionadas** | _A definir (ver [protótipos](../12-prototipos))_ |

## Interessados e interesses

- **Usuário (titular dos dados):** entender, em linguagem simples, para que seus dados são usados; aceitar o termo sem atrito; consultar o que consentiu e exercer seus direitos sobre os próprios dados sem precisar recorrer a e-mail ou planilha.
- **RH:** ter o aceite de todos os participantes registrado antes do ciclo, para que as avaliações tenham base legal; receber as solicitações dos titulares de forma organizada.
- **Encarregado de dados (DPO):** comprovar o consentimento (data e hora do aceite), garantir que só os dados necessários sejam coletados e atender aos pedidos dos titulares conforme a LGPD.
- **Gestor e Colaborador como avaliados:** confiar que seus dados pessoais e resultados não serão expostos além do previsto.

## Pré-condições

- O usuário está autenticado no sistema (UC-10).
- O termo de consentimento vigente está cadastrado pela empresa.

## Pós-condições (garantia de sucesso)

- O aceite do termo fica registrado com data e hora, e o usuário passa a acessar as funcionalidades do seu perfil.
- Quando o usuário faz uma solicitação sobre seus dados (acesso, correção ou exclusão), ela fica registrada e encaminhada para atendimento.

## Cenário de sucesso principal

1. O Usuário acessa o sistema pela primeira vez.
2. O Sistema exibe o termo de consentimento, em linguagem simples, informando quais dados pessoais são tratados e para qual finalidade (avaliação de competências e plano de desenvolvimento).
3. O Usuário lê o termo e confirma o aceite.
4. O Sistema registra o aceite com data e hora e libera as funcionalidades do perfil do Usuário.
5. Em outro momento, o Usuário acessa a área "Meus dados".
6. O Sistema exibe o termo aceito, a data e hora do aceite e os dados pessoais que mantém sobre o Usuário.
7. O Usuário escolhe o tipo de solicitação (acesso, correção ou exclusão), descreve o pedido e confirma.
8. O Sistema registra a solicitação com data e hora, informa ao Usuário o número de protocolo e encaminha o pedido ao RH/encarregado.
9. O Sistema exibe a solicitação na lista do Usuário com a situação "Em análise".

## Extensões (fluxos alternativos)

- **\*a. A qualquer momento, o Usuário desiste da operação:**
  1. O Sistema descarta os dados não confirmados; nada é registrado.
- **3a. O Usuário não aceita o termo:**
  1. O Sistema informa, em linguagem simples, que sem o aceite não é possível usar o sistema e mantém o bloqueio das funcionalidades (`RF-29`).
  2. O caso de uso termina; o termo volta a ser exibido no próximo acesso.
- **2a. A empresa publica uma nova versão do termo:**
  1. No próximo acesso, o Sistema exibe a nova versão destacando o que mudou.
  2. Volta ao passo 3.
- **6a. O Usuário quer apenas consultar o consentimento:**
  1. O caso de uso termina após o passo 6.
- **7a. O Usuário pede correção de um dado:**
  1. O Sistema solicita a indicação do dado incorreto e do valor correto.
  2. Volta ao passo 8.
- **7b. O Usuário pede a exclusão dos dados enquanto participa de um ciclo em andamento:**
  1. O Sistema avisa que a exclusão pode impedir sua participação no ciclo e pede confirmação.
  2. Se o Usuário confirmar, volta ao passo 8; se não, volta ao passo 6.
- **7c. A política da empresa não permite um tipo de solicitação:**
  1. O Sistema não exibe essa opção e informa o canal de atendimento do encarregado (`RF-30`, "conforme a política da empresa").
- **8a. Parte dos dados não pode ser excluída (respostas anônimas já agregadas ou obrigação legal de guarda):**
  1. Ao analisar o pedido, o RH/encarregado registra a justificativa.
  2. O Sistema informa ao Usuário o que foi excluído e o que foi mantido, com o motivo.

## Requisitos especiais

- `RNF-09`: termo e mensagens em linguagem simples, sem jargão jurídico ou técnico.
- `RNF-06`: a área "Meus dados" mostra apenas os dados realmente coletados, que devem ser só os necessários à avaliação.
- `RNF-01`: a exclusão ou consulta dos dados de um avaliador nunca pode revelar a autoria de respostas de pares e subordinados.
- `RNF-05`: o aceite e as solicitações ficam registrados (usuário, data e hora) para auditoria.
- `RNF-07`: o termo e a área "Meus dados" devem funcionar também no celular.

## Lista de variações de tecnologia e dados

- Acesso com login e senha ou com a conta corporativa (SSO, `RF-32`).
- Cópia dos dados entregue para visualização na tela ou para download (PDF).

## Frequência de ocorrência

- Aceite do termo: uma vez por usuário e a cada nova versão do termo.
- Consulta e solicitações: eventual, sob demanda do titular.

## Questões em aberto

- O avaliador externo (`RF-34`), que responde por link sem conta, também precisa aceitar o termo antes de responder? Se sim, o aceite entra no UC-03.
- Revogação do consentimento (LGPD, art. 8º, § 5º): o titular pode revogar o aceite? Como `RF-29` bloqueia o uso sem aceite, revogar equivaleria a sair do sistema. Avaliar a criação de um requisito próprio.
- Prazo para o RH/encarregado responder às solicitações: definir com a empresa e conferir o que a LGPD exige (art. 19).
- Quais dados são mantidos após a exclusão por obrigação legal (LGPD, art. 16) e por quanto tempo.
