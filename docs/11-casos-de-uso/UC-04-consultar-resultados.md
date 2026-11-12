# UC-04 — Consultar resultados da avaliação

| Campo | Valor |
|-------|-------|
| **Responsável** | Lorenzo Oliveira Maciel |
| **Escopo** | Sistema de Gestão de Competências |
| **Nível** | Objetivo do usuário |
| **Ator principal** | Colaborador, Gestor ou RH |
| **Requisitos cobertos** | `RF-19` a `RF-24`, `RF-26`, `RNF-01` a `RNF-03`, `RNF-05`, `RNF-12` |
| **Telas relacionadas** | [TL-04](../12-prototipos/telas/TL-04-consultar-resultados.png) (ver [protótipos](../12-prototipos/README.md#telas)) |

## Interessados e interesses

- **Colaborador:** receber o resultado logo que for liberado, em um painel com gráficos fáceis de ler, comparando a visão do chefe, a autoavaliação e a dos pares e subordinados; ler os comentários para preparar a conversa de feedback; acompanhar a própria evolução em relação aos ciclos anteriores.
- **Gestor:** ver onde a equipe está mais forte e mais fraca, comparar as pessoas e usar o resultado na conversa de feedback e no plano de desenvolvimento, sem saber quem respondeu o quê.
- **RH:** deixar de consolidar planilhas à mão (cerca de três semanas hoje); decidir quando o resultado é liberado ao colaborador; gerar relatórios por equipe e por área e exportá-los em PDF e Excel.
- **Avaliadores de pares e subordinados:** ter a garantia de que o resultado nunca revela a autoria das suas respostas, nem por média de um grupo pequeno nem pelo texto dos comentários.
- **Encarregado de dados (DPO):** cada perfil ver só o que lhe cabe, anonimato preservado e registro de quem acessou os resultados, conforme a LGPD.

## Pré-condições

- O ciclo de avaliação foi encerrado (`RF-09`) e as respostas foram consolidadas pelo sistema (`RF-19`).
- O usuário está autenticado (UC-10) e aceitou o termo de consentimento (UC-05).
- Para o colaborador consultar o próprio resultado, o RH já o liberou (`RF-24`).

## Pós-condições (garantia de sucesso)

- O usuário vê apenas os resultados permitidos ao seu perfil: o colaborador, os próprios; o gestor, os próprios e os da equipe direta; o RH, os próprios e os de toda a organização (`RNF-03`).
- A autoria das respostas de pares, subordinados e externos não aparece em nenhuma tela, relatório ou arquivo exportado (`RNF-01`), e as médias de grupos com menos de 3 respostas não são exibidas (`RNF-02`).
- O acesso ao resultado, a liberação pelo RH e as exportações ficam registrados no log de auditoria, com usuário, data e hora (`RNF-05`).
- Quando o usuário exporta, o arquivo contém os mesmos dados que a tela exibe, com as mesmas restrições de anonimato (`RF-23`).

## Cenário de sucesso principal

1. O usuário acessa a área de resultados.
2. O sistema identifica o perfil do usuário e lista os ciclos com resultado disponível para ele (colaborador: os próprios, já liberados; gestor: os próprios e os da equipe direta; RH: os próprios e os da organização), com a data de encerramento de cada ciclo.
3. O usuário escolhe um ciclo e, quando é gestor ou RH, a pessoa cujo resultado quer ver.
4. O sistema exibe o painel individual: a média por competência e os gráficos que comparam a avaliação do chefe, a autoavaliação, a média dos pares, a média dos subordinados e, quando o ciclo teve avaliadores externos, a média deles (`RF-20`, `RF-34`). Itens marcados como "não tenho como avaliar" ficam fora da média (`RF-06`). O sistema registra o acesso no log (`RNF-05`).
5. O sistema exibe os comentários das perguntas abertas, sem identificar o autor (`RF-22`).
6. O usuário consulta a evolução da pessoa: o sistema exibe as médias por competência de cada ciclo anterior ao lado das do ciclo atual (`RF-26`).
7. O usuário pede a exportação do resultado em PDF ou Excel (`RF-23`).
8. O sistema gera o arquivo com os mesmos dados da tela, registra a exportação no log (`RNF-05`) e disponibiliza o download.

O usuário pode voltar ao passo 3 para consultar outra pessoa ou outro ciclo.

## Extensões (fluxos alternativos)

- **\*a. A qualquer momento, o usuário tenta acessar o resultado de uma pessoa ou de uma equipe que não lhe cabe (por exemplo, alterando o endereço da página):**
  1. O sistema nega o acesso no servidor (`RNF-03`), informa que o usuário não tem permissão e não exibe nenhum dado do resultado.
  2. Volta ao passo 2.
- **2a. O ator é o RH e há um ciclo consolidado ainda não liberado ao colaborador:**
  1. O sistema exibe o resultado consolidado do ciclo e a opção de liberá-lo.
  2. O RH revisa o resultado e confirma a liberação (`RF-24`).
  3. O sistema marca o resultado como liberado, envia e-mail de aviso a cada colaborador avaliado pelo serviço de e-mail (`RF-24`) e registra a liberação no log (`RNF-05`).
  4. Volta ao passo 3.
- **2b. O ator é um colaborador e o RH ainda não liberou o resultado do ciclo:**
  1. O sistema informa que o resultado ainda não foi liberado e que o colaborador será avisado por e-mail quando for.
  2. O caso de uso termina.
- **2c. O ciclo ainda não foi encerrado e consolidado:**
  1. O sistema informa que o resultado só fica disponível depois do encerramento do ciclo (`RF-09`, `RF-19`).
  2. O caso de uso termina; o andamento do ciclo é consultado no UC-07.
- **3a. O ator é gestor ou RH e quer ver o resultado de uma equipe ou de uma área:**
  1. O sistema exibe o relatório com a média por competência da equipe ou da área e a comparação entre as pessoas, sem identificar respondentes (`RF-21`). O gestor só escolhe a própria equipe direta; o RH escolhe qualquer equipe ou área.
  2. Continua no passo 7.
- **4a. Um grupo de avaliadores (pares, subordinados ou externos) tem menos de 3 respostas:**
  1. O sistema não exibe a média desse grupo nem a usa em nenhum gráfico ou relatório (`RNF-02`) e informa que não há respostas suficientes para preservar o anonimato.
  2. Continua no passo 4 com os demais grupos.
- **5a. O RH ocultou um comentário ofensivo (UC-09):**
  1. O sistema deixa de exibir esse comentário nos resultados (`RF-22`).
  2. Continua no passo 5.
- **6a. A pessoa não tem ciclo anterior:**
  1. O sistema informa que ainda não há histórico para comparar; continua no passo 7.

## Requisitos especiais

- **Anonimato (`RNF-01`):** a autoria das respostas de pares, subordinados e externos (`RF-34`) nunca aparece para nenhum perfil, inclusive o RH, em tela, relatório ou arquivo exportado; essas respostas aparecem apenas agregadas.
- **Mínimo de respostas (`RNF-02`):** média de pares, de subordinados ou de externos (`RF-34`) só é exibida com no mínimo 3 respostas no grupo.
- **Visibilidade pelo servidor (`RNF-03`):** o controle de acesso é aplicado no servidor, e não apenas escondendo botões; o colaborador vê só o próprio resultado e o gestor só o próprio e o da equipe direta.
- **Segurança (`RNF-04`):** resultados trafegam por HTTPS (TLS 1.2 ou superior) e ficam armazenados criptografados.
- **Auditoria (`RNF-05`):** acessos a resultados, liberações e exportações ficam no log, com usuário, data e hora.
- **Celular (`RNF-07`):** o painel e os gráficos devem ser legíveis em celular e tablet.
- **Linguagem (`RNF-09`):** mensagens e avisos em linguagem simples.
- **Desempenho (`RNF-11`):** painéis e relatórios carregam em até 3 segundos, inclusive no pico logo após a liberação dos resultados.
- **Exatidão (`RNF-12`):** as médias e consolidações são exatas e reproduzíveis a partir das respostas armazenadas; o mesmo conjunto de respostas sempre gera o mesmo resultado.

## Lista de variações de tecnologia e dados

- Acesso por login individual ou conta corporativa (UC-10).
- Navegador em computador, tablet ou celular.
- Exportação em PDF ou em Excel (`RF-23`).
- Escala de 1 a 5 ou de 1 a 10, conforme o questionário (`RF-04`).
- Gráfico radar do mapa de competências e tabela de notas por competência, conforme o protótipo `TL-04`; o gráfico de evolução entre ciclos (`RF-26`) ainda não foi prototipado.

## Frequência de ocorrência

Uma vez por ciclo para cada colaborador (hoje, ciclos anuais), mais as consultas do gestor à equipe e do RH à organização, e as consultas ao histórico ao longo do ano. O acesso se concentra nos dias seguintes à liberação dos resultados.

## Questões em aberto

- **Quando o gestor vê o resultado da equipe:** o `RF-24` fala apenas em liberar o resultado ao colaborador. Adotamos que gestor e RH veem o consolidado assim que o ciclo é encerrado e que só o colaborador espera a liberação; confirmar com o RH.
- **Comentários das perguntas abertas:** adotamos que o colaborador, o gestor e o RH leem os comentários sem autoria. O texto livre pode identificar quem escreveu (ponto levantado na síntese das entrevistas, `SE-23`); confirmar se o RH deve revisar os comentários antes de liberar o resultado.
- **Anonimato da avaliação do chefe:** o `RNF-01` protege só pares e subordinados, e o painel (`RF-20`) mostra a avaliação do chefe separada. Adotamos que ela aparece identificada como "chefe"; confirmar com o RH.
- **Relatórios de equipes pequenas:** o `RNF-02` fixa o mínimo de 3 respostas, mas não diz o que fazer em um relatório de equipe com 1 ou 2 pessoas, em que a comparação entre pessoas pode expor alguém. Definir se vale a mesma regra.
- **Histórico com escalas diferentes:** se o questionário mudar de escala entre ciclos (1 a 5 e 1 a 10), a evolução (`RF-26`) não é diretamente comparável; definir se o sistema normaliza as notas ou só compara ciclos com a mesma escala.
- **Liberação ao colaborador (`RF-24`):** o requisito não diz se o RH libera o ciclo inteiro de uma vez ou pessoa por pessoa. Adotamos a liberação do ciclo inteiro, com aviso por e-mail a cada colaborador avaliado; confirmar com o RH.
- **Avaliadores externos nos resultados:** o `RF-34` manda aplicar `RNF-01` e `RNF-02` às respostas dos externos, mas o `RF-19` e o `RF-20` não os citam entre os grupos consolidados e comparados. Adotamos um grupo próprio, com a mesma regra de 3 respostas; confirmar e ajustar o `RF-20`, se for o caso.
