# Matriz de rastreabilidade horizontal

Rastreia relações **dentro do mesmo nível**: requisito × requisito e caso de uso × caso de uso. Serve para avaliar o impacto de uma mudança (se um requisito mudar, o que mais é afetado?).

As relações foram tiradas da [lista de requisitos](../08-requisitos/lista-requisitos.md), dos [casos de uso](../11-casos-de-uso) (pré-condições e extensões) e das questões em aberto da [ERS](../13-especificacao-requisitos/ers-ieee830.md#apêndice-b--questões-em-aberto-e-alterações-propostas) (`Q-NN`).

## 1. Requisito × requisito

Tipos de relação: **depende de** (precisa do outro para existir), **restringido por** (o outro impõe regra), **conflita com** (exige decisão).

### 1.1 Relações

| Requisito | Relação | Requisito(s) | Observação |
|-----------|---------|--------------|------------|
| RF-02 | depende de | RF-01 | O cargo é associado a competências já cadastradas. |
| RF-03 | depende de | RF-01, RF-02 | O questionário usa as competências do catálogo, pré-selecionadas pelo cargo. |
| RF-03 | restringido por | RNF-13, RNF-08 | Configurável pelo RH sem código; questionário extenso compromete o tempo de 10 minutos (dor D-12, `Q-10`). |
| RF-04 | depende de | RF-03 | A escala pertence ao questionário. |
| RF-05 | depende de | RF-03 | Perguntas abertas fazem parte do questionário. |
| RF-06 | depende de | RF-03 | A opção vale para cada item de escala do questionário. |
| RF-07 | depende de | RF-03 | Modelos e cópias geram novos questionários. |
| RF-08 | depende de | RF-03 | O ciclo usa questionários publicados. |
| RF-09 | depende de | RF-08 | As datas pertencem ao ciclo. |
| RF-10 | depende de | RF-08 | Os avaliadores são definidos por tipo de avaliação do ciclo. |
| RF-10 | restringido por | RNF-02 | Menos de 3 pares ou subordinados gera alerta no agendamento (`Q-14`). |
| RF-11 | depende de | RF-03, RF-10 | Precisa da classificação das perguntas por tipo de avaliador (`Q-10`) e da relação de cada designação. |
| RF-12 | depende de | RF-09, RF-10 | Convites na abertura e lembretes antes do encerramento, para os avaliadores designados. |
| RF-13 | depende de | RF-10 | O andamento é contado por designação. |
| RF-13 | restringido por | RNF-03 | O gestor vê só a própria equipe. |
| RF-14 | depende de | RF-11 | Salva as respostas do formulário apresentado ao avaliador. |
| RF-15 | depende de | RF-11 | Os campos obrigatórios são os do formulário. |
| RF-16 | depende de | RF-15 | O envio só ocorre sem pendências. |
| RF-16 | restringido por | RNF-05 | A reabertura pelo RH é registrada no log de auditoria. |
| RF-17 | restringido por | RNF-03 | O gestor registra e vê observações só da equipe direta. |
| RF-18 | depende de | RF-26 | O resultado do ciclo anterior vem do histórico. |
| RF-19 | depende de | RF-09, RF-16 | Consolida as avaliações enviadas, no encerramento do ciclo. |
| RF-19 | restringido por | RF-06, RNF-12 | Itens "não tenho como avaliar" ficam fora da média; o cálculo deve ser reproduzível. |
| RF-20 | depende de | RF-19 | O painel mostra as médias consolidadas. |
| RF-20 | restringido por | RNF-01, RNF-02, RNF-03 | Sem autoria, mínimo de 3 respostas por grupo, só os próprios resultados (ou da equipe, para o gestor). |
| RF-21 | depende de | RF-19 | O relatório agrega as médias consolidadas. |
| RF-21 | restringido por | RNF-01, RNF-02, RNF-03 | Mesmas regras do painel. |
| RF-22 | restringido por | RNF-01, RNF-05 | Comentários sem autor; a ocultação é registrada no log. |
| RF-23 | depende de | RF-20, RF-21 | Exporta o que o painel e os relatórios mostram. |
| RF-23 | restringido por | RNF-01, RNF-02 | O arquivo segue as mesmas regras de anonimato da tela. |
| RF-24 | depende de | RF-19 | Só se libera um resultado já consolidado. |
| RF-25 | depende de | RF-04, RF-09 | Mostra a descrição dos níveis da escala desde a abertura do ciclo. |
| RF-26 | depende de | RF-19 | O histórico reúne os resultados consolidados de cada ciclo. |
| RF-27 | depende de | RF-01, RF-19 | As metas se ligam a competências e partem do resultado do ciclo. |
| RF-28 | depende de | RF-27 | Os lembretes são sobre as metas do plano. |
| RF-29 | depende de | RF-31 | O termo aparece depois da autenticação, no primeiro acesso. |
| RF-30 | depende de | RF-29 | O titular consulta o consentimento que deu. |
| RF-30 | restringido por | RNF-01, RNF-06 | A cópia ou exclusão não pode revelar autoria; só dados necessários são coletados. |
| RF-32 | depende de | RF-31 | O SSO é uma forma alternativa de autenticação individual. |
| RF-33 | depende de | RF-31 | O perfil é atribuído ao usuário autenticado. |
| RF-34 | depende de | RF-08, RF-12 | O externo é convidado para um ciclo, por e-mail. |
| RF-34 | restringido por | RNF-01, RNF-02 | Respostas dos externos seguem as regras de anonimato. |
| RNF-03 | depende de | RF-33 | O controle no servidor usa os perfis. |
| RNF-13 | depende de | RF-01, RF-03, RF-04 | A configuração sem código é a das competências, questionários e escalas. |

### 1.2 Conflitos que exigem decisão

| Requisito | Relação | Requisito(s) | Decisão pendente |
|-----------|---------|--------------|------------------|
| RF-18 | conflita com | RNF-03 | Mostrar o resultado anterior "ao avaliador" expõe dados a pares. Adotado: só ao gestor do avaliado (`Q-04`). |
| RF-13 | conflita com | RNF-01 | O andamento mostra quem já respondeu e a designação guarda quem avalia quem. Adotado: manter a designação sem ligar a resposta ao avaliador (`Q-08`). |
| RF-29 | conflita com | RF-34 | O termo é exigido de todo usuário, mas o externo não tem conta (`Q-03`). |
| RF-26 | conflita com | RF-04 | Ciclos com escalas diferentes (1 a 5 e 1 a 10) não são comparáveis sem regra de normalização (`Q-07`). |
| RF-11 | conflita com | RF-34 | `RF-11` não cita a relação "externo", que o `RF-34` cria (`Q-03`). |

## 2. Caso de uso × caso de uso

Tipos de relação: **pré-requisito de** (precisa ter ocorrido antes), `<<include>>` (sempre executado como parte do outro), `<<extend>>` (executado sob condição, a partir de um ponto do outro) e **fornece dados para** (produz informação que o outro exibe, sem ser obrigatório).

| De | Relação | Para | Observação |
|----|---------|------|------------|
| UC-10 Autenticar-se | pré-requisito de | UC-01 a UC-09 | Todos exigem usuário autenticado, menos o avaliador externo no UC-03, que entra por link. |
| UC-05 Gerenciar consentimento | `<<extend>>` | UC-10 Autenticar-se | No primeiro acesso ou com nova versão do termo, o aceite acontece antes da página inicial. |
| UC-05 Gerenciar consentimento | pré-requisito de | UC-01 a UC-04, UC-06 a UC-09 | Sem aceite, nenhuma funcionalidade do perfil é liberada (`RF-29`). |
| UC-01 Gerenciar competências e questionários | pré-requisito de | UC-02 Criar ciclo | O ciclo só usa questionários publicados. |
| UC-02 Criar ciclo | pré-requisito de | UC-03 Responder avaliação | O avaliador só responde em ciclo aberto, como avaliador designado. |
| UC-02 Criar ciclo | pré-requisito de | UC-07 Acompanhar andamento | O andamento é do ciclo aberto. |
| UC-03 Responder avaliação | pré-requisito de | UC-04 Consultar resultados | Os resultados vêm das avaliações enviadas, depois do encerramento. |
| UC-03 Responder avaliação | pré-requisito de | UC-09 Reabrir avaliação | Só se reabre avaliação já enviada (extensão 10a do UC-03). |
| UC-04 Consultar resultados | pré-requisito de | UC-06 Gerenciar plano | O plano parte do resultado liberado do ciclo. |
| UC-08 Registrar observações | fornece dados para | UC-03 Responder avaliação | As observações do período aparecem ao gestor durante a avaliação. |
| UC-09 Moderar comentários | fornece dados para | UC-04 Consultar resultados | Comentários ocultados deixam de aparecer nos resultados (extensão 5a do UC-04). |

O [diagrama de casos de uso](../11-casos-de-uso/diagrama-casos-de-uso.puml) ainda não mostra o `<<extend>>` entre UC-05 e UC-10. Incluí-lo é opcional: Larman recomenda usar `<<include>>` e `<<extend>>` com moderação no diagrama.

Responsável: Arthur Mamedes Borges (issue #34)
