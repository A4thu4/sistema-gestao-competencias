# UC-05 — Gerenciar consentimento e dados pessoais

> Objetivo: o usuário dá o consentimento para o tratamento dos seus dados antes de usar o sistema e, depois, consulta esse consentimento e exerce seus direitos de titular (acesso, correção e exclusão) sem depender de e-mail ou planilha. Formato completo (*fully dressed*) do capítulo 6 do Larman.

| Campo | Valor |
|-------|-------|
| **Responsável** | Arthur Mamedes Borges |
| **Escopo** | Sistema de Gestão de Competências |
| **Nível** | Objetivo do usuário |
| **Ator principal** | Usuário (RH, Gestor ou Colaborador) |
| **Requisitos cobertos** | `RF-29`, `RF-30`, `RNF-06`, `RNF-09` |
| **Telas relacionadas** | A definir na issue #32 (ver [protótipos](../12-prototipos)) |

## Interessados e interesses

- **Usuário (titular dos dados):** entender, em linguagem simples, quais dados são tratados e para quê; aceitar o termo sem atrito; consultar o que consentiu e exercer seus direitos sobre os próprios dados pelo próprio sistema.
- **RH:** ter o aceite de todos os participantes registrado antes do ciclo, para que as avaliações tenham base legal; receber as solicitações dos titulares organizadas, com protocolo e situação.
- **Encarregado de dados (DPO):** comprovar o consentimento (versão do termo, data e hora do aceite), garantir que só os dados necessários sejam coletados (`RNF-06`) e atender aos pedidos dos titulares dentro do prazo da LGPD.
- **Empresa (controladora):** cumprir a LGPD sem processos manuais e reduzir o risco de sanções.
- **Avaliadores de pares e subordinados:** ter certeza de que nenhuma consulta ou exclusão de dados de outra pessoa revela a autoria das suas respostas (`RNF-01`).

## Pré-condições

- O usuário está autenticado no sistema (UC-10).
- Existe uma versão vigente do termo de consentimento cadastrada pelo RH.

## Pós-condições (garantia de sucesso)

- O aceite fica registrado com a versão do termo, a data e a hora, e o usuário passa a acessar as funcionalidades do seu perfil (`RF-29`).
- Cada solicitação do titular (acesso, correção ou exclusão) fica registrada com protocolo, data, hora e situação, e é encaminhada ao RH/encarregado (`RF-30`).

**Garantia mínima:** sem aceite registrado da versão vigente, nenhuma funcionalidade do perfil é liberada; nenhuma solicitação confirmada é perdida; nenhuma consulta ou exclusão revela a autoria de respostas de pares e subordinados.

## Cenário de sucesso principal

O cenário tem dois momentos: o aceite no primeiro acesso (passos 1 a 4) e o exercício dos direitos do titular na área "Meus dados" (passos 5 a 10). O UC-10 chama a primeira parte antes de exibir a página inicial.

1. O usuário acessa o sistema pela primeira vez.
2. O sistema exibe o termo de consentimento vigente, em linguagem simples (`RNF-09`), informando quais dados pessoais são tratados, para qual finalidade (avaliação de competências e plano de desenvolvimento), quem tem acesso a eles, por quanto tempo são guardados, quais são os direitos do titular e o canal do encarregado.
3. O usuário lê o termo e confirma o aceite.
4. O sistema registra o aceite com a versão do termo, a data e a hora e libera as funcionalidades do perfil do usuário.
5. Em outro momento, o usuário acessa a área "Meus dados".
6. O sistema exibe o termo aceito (versão, data e hora do aceite), os dados pessoais que mantém sobre o usuário e as solicitações anteriores com a situação de cada uma.
7. O usuário escolhe o tipo de solicitação (acesso, correção ou exclusão), descreve o pedido e confirma.
8. O sistema registra a solicitação com data e hora, gera um número de protocolo e encaminha o pedido ao RH/encarregado.
9. O sistema informa o protocolo ao usuário e envia a confirmação por e-mail.
10. O sistema exibe a solicitação na lista do usuário com a situação "Em análise".

## Extensões (fluxos alternativos)

- **\*a. A qualquer momento, o usuário desiste da operação:**
  1. O sistema descarta os dados não confirmados; nada é registrado.
- **\*b. A qualquer momento, o sistema não consegue gravar o aceite ou a solicitação:**
  1. O sistema informa o erro em linguagem simples e não libera as funcionalidades nem gera protocolo.
  2. O usuário tenta novamente; o caso de uso volta ao passo em que estava.
- **1a. O usuário já aceitou a versão vigente do termo:**
  1. O sistema não exibe o termo e abre a página inicial; o caso de uso continua no passo 5 quando o usuário acessar "Meus dados".
- **2a. O RH publicou uma nova versão do termo depois do último aceite do usuário:**
  1. No próximo acesso, o sistema exibe a nova versão destacando o que mudou.
  2. Volta ao passo 3.
- **3a. O usuário não aceita o termo:**
  1. O sistema informa, em linguagem simples, que sem o aceite não é possível usar o sistema e mantém o bloqueio das funcionalidades (`RF-29`).
  2. O caso de uso termina; o termo volta a ser exibido no próximo acesso.
- **6a. O usuário quer apenas consultar o consentimento:**
  1. O caso de uso termina após o passo 6.
- **7a. O usuário pede acesso aos dados:**
  1. O sistema gera uma cópia dos dados pessoais do usuário para visualização na tela ou download em PDF, sem identificar autores de respostas de pares e subordinados (`RNF-01`).
  2. O sistema registra a solicitação como atendida, com data e hora; o caso de uso termina.
- **7b. O usuário pede correção de um dado:**
  1. O sistema solicita a indicação do dado incorreto e do valor correto.
  2. Volta ao passo 8.
- **7c. O usuário pede a exclusão dos dados enquanto participa de um ciclo em andamento:**
  1. O sistema avisa que a exclusão pode impedir sua participação no ciclo e pede confirmação.
  2. Se o usuário confirmar, volta ao passo 8; se não, volta ao passo 6.
- **7d. A política da empresa não permite um tipo de solicitação:**
  1. O sistema não exibe essa opção e informa o canal de atendimento do encarregado (`RF-30`, "conforme a política da empresa").
- **10a. O RH/encarregado conclui o atendimento da solicitação:**
  1. O RH/encarregado registra a decisão: atendida, atendida em parte ou negada, com a justificativa.
  2. O sistema atualiza a situação na lista do usuário e o avisa por e-mail.
- **10b. Parte dos dados não pode ser excluída (respostas anônimas já agregadas em resultados ou obrigação legal de guarda):**
  1. O RH/encarregado registra a justificativa ao concluir o atendimento (10a).
  2. O sistema informa ao usuário o que foi excluído e o que foi mantido, com o motivo.

## Requisitos especiais

- **Linguagem simples (`RNF-09`):** termo, avisos e mensagens sem jargão jurídico ou técnico.
- **Necessidade (`RNF-06`):** a área "Meus dados" mostra apenas os dados realmente coletados, que devem ser só os necessários à avaliação.
- **Anonimato (`RNF-01`):** a consulta, a cópia ou a exclusão dos dados de um usuário nunca revela a autoria de respostas de pares e subordinados.
- **Visibilidade pelo servidor (`RNF-03`):** o usuário só acessa os próprios dados e solicitações; a verificação é feita no servidor.
- **Segurança (`RNF-04`):** aceite, solicitações e cópias trafegam por HTTPS e ficam armazenados criptografados.
- **Celular (`RNF-07`):** o termo e a área "Meus dados" devem funcionar também em celular e tablet.

## Lista de variações de tecnologia e dados

- Acesso com login e senha ou com a conta corporativa (SSO, `RF-32`), conforme o UC-10.
- Cópia dos dados para visualização na tela ou para download em PDF.
- Tipos de solicitação disponíveis conforme a política da empresa: acesso, correção e exclusão.

## Frequência de ocorrência

- Aceite do termo: uma vez por usuário e a cada nova versão do termo.
- Consulta e solicitações: eventual, sob demanda do titular.

## Questões em aberto

- **Avaliador externo (`RF-34`):** quem responde por link, sem conta, também precisa aceitar o termo antes de responder? Se sim, o aceite entra na extensão 1a do UC-03.
- **Versões do termo:** a extensão 2a pede um novo aceite a cada versão, mas o `RF-29` fala só do primeiro acesso. Confirmar com o grupo se o requisito deve ser ajustado.
- **Registro para auditoria:** o `RNF-05` cobre acessos a resultados, reaberturas e moderação, mas não o aceite nem as solicitações dos titulares. O `RF-29` já exige data e hora do aceite; avaliar se as solicitações devem entrar no `RNF-05`.
- **Revogação do consentimento (LGPD, art. 8º, § 5º):** o titular pode revogar o aceite? Como o `RF-29` bloqueia o uso sem aceite, revogar equivaleria a sair do sistema. Avaliar um requisito próprio.
- **Outros direitos do titular (LGPD, art. 18):** o `RF-30` cobre acesso, correção e exclusão; a lei também prevê, entre outros, portabilidade e informação sobre compartilhamento. Confirmar o que entra no escopo.
- **Prazo de atendimento (LGPD, art. 19):** definir com a empresa o prazo de resposta do RH/encarregado e conferir o que a lei exige para cada tipo de pedido.
- **Guarda após a exclusão (LGPD, art. 16):** quais dados são mantidos por obrigação legal e por quanto tempo.
- **Quem atende:** confirmar se as solicitações vão ao RH, ao encarregado ou aos dois.
