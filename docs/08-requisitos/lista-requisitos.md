# Lista geral de requisitos

Requisitos do **Sistema de Gestão de Competências** consolidados a partir das sínteses de [entrevistas](../04-entrevistas/sintese.md) (`SE-NN`), [questionários](../05-questionarios/sintese.md) (`SQ-NN`), [sistema análogo](../06-sistema-analogo/sintese.md) (`SA-NN`) da [pesquisa documental](../03-pesquisa-documental) (`DOC-N §seção`) e do [escopo do enunciado](../00-escopo/escopo.md) (`ESC §seção`).

Responsável: Rikelmy Andrade

## Como ler

- **ID:** `RF-NN` para requisito funcional, `RNF-NN` para não funcional.
- **Prioridade:** Alta (essencial para a primeira versão), Média (importante, pode vir em seguida) e Baixa (desejável).
- **Origem:** itens das sínteses que deram origem ao requisito. A ligação com as fontes (`ENT`, `QUE`, `ANA`, `DOC`) está na [matriz de rastreabilidade](matriz-rastreabilidade.md).
- Itens repetidos entre fontes foram unidos em um só requisito; itens amplos foram divididos para que cada requisito seja verificável isoladamente.

## Restrições do projeto

| ID | Restrição |
|----|-----------|
| R-01 | O sistema deve ser implementado em plataforma web, com perspectiva de funcionamento futuro em dispositivos móveis. |
| R-02 | O sistema deve estar em conformidade com a Lei Geral de Proteção de Dados (Lei nº 13.709/2018). |

## Requisitos

### Competências e questionários

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RF-01 | O sistema deve permitir que o RH cadastre, edite e desative competências técnicas e comportamentais, cada uma com nome e descrição, sem necessidade de alteração de código. | Alta | `SE-02`, `SA-02` |
| RF-02 | O sistema deve permitir associar a cada cargo um conjunto de competências a ser avaliado. | Alta | `SE-03`, `SA-02` |
| RF-03 | O sistema deve permitir que o RH crie questionários por cargo, selecionando as competências e as perguntas de cada uma. | Alta | `SE-07`, `SA-01`, `SA-02`, `DOC-2 §2.5` |
| RF-04 | O sistema deve permitir configurar a escala de notas de cada questionário (1 a 5 ou 1 a 10), com uma descrição obrigatória para cada nível. | Alta | `SE-07`, `SQ-15`, `SA-03` |
| RF-05 | O sistema deve permitir incluir perguntas abertas (texto livre) nos questionários. | Alta | `SE-07`, `SQ-04`, `SA-03` |
| RF-06 | O sistema deve oferecer, em cada item avaliado, a opção "não tenho como avaliar", que não entra no cálculo da média. | Média | `SQ-17` |
| RF-07 | O sistema deve oferecer modelos prontos de questionário (chefe–colaborador, pares e 360º) e permitir duplicar o questionário de um ciclo anterior para editá-lo. | Média | `SE-08`, `SA-01` |

### Ciclos de avaliação

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RF-08 | O sistema deve permitir criar ciclos de avaliação combinando os tipos chefe–colaborador, autoavaliação, pares e subordinados avaliando o líder (360º). | Alta | `SE-04`, `SQ-01` |
| RF-09 | O sistema deve permitir definir data de abertura e de encerramento de cada ciclo, bloqueando novas respostas após o encerramento. | Alta | `SE-14` |
| RF-10 | O sistema deve permitir que o RH ou o gestor defina os avaliadores de cada avaliado; o avaliado não pode escolher os próprios avaliadores. | Alta | `SE-22` |
| RF-11 | O sistema deve apresentar a cada avaliador apenas as perguntas correspondentes à sua relação com o avaliado (chefe, par, subordinado ou autoavaliação). | Média | `SA-04` |
| RF-12 | O sistema deve enviar convite por e-mail na abertura do ciclo e lembretes automáticos aos avaliadores com respostas pendentes antes do encerramento. | Alta | `SE-14`, `SQ-08`, `SA-07` |
| RF-13 | O sistema deve exibir o andamento do ciclo (respondidas e pendentes por avaliador): ao RH para toda a organização e ao gestor para a sua equipe. | Alta | `SE-14`, `SQ-09` |
| RF-34 | O sistema deve permitir incluir avaliadores externos (clientes, fornecedores ou parceiros) em um ciclo, convidados por e-mail com link individual que dá acesso apenas ao questionário daquela avaliação, sem necessidade de conta no sistema; as respostas externas seguem as mesmas regras de anonimato e de número mínimo de respondentes (`RNF-01`, `RNF-02`). | Média | `ESC §2.2` |

### Preenchimento

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RF-14 | O sistema deve salvar automaticamente o progresso do preenchimento, permitindo continuar depois sem perda de dados. | Alta | `SE-26`, `SQ-12` |
| RF-15 | O sistema deve indicar os campos obrigatórios não preenchidos durante o preenchimento e impedir o envio enquanto houver pendências. | Alta | `SE-27`, `DOC-2 §2.2` |
| RF-16 | O sistema deve impedir a alteração de uma avaliação após o envio; somente o RH pode reabri-la, informando o motivo. | Alta | `SE-24`, `DOC-2 §2.4` |
| RF-17 | O sistema deve permitir que o gestor registre observações datadas (pontos positivos e de melhoria) sobre cada membro da equipe ao longo do ano e exibi-las durante a avaliação. | Média | `SE-20` |
| RF-18 | O sistema deve exibir ao avaliador, durante a avaliação, o resultado do ciclo anterior do avaliado. | Média | `SE-21`, `SQ-13` |

### Resultados e análise

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RF-19 | O sistema deve consolidar automaticamente as respostas ao encerramento do ciclo, calculando a média por competência e por tipo de avaliador. | Alta | `SE-01`, `SA-08`, `DOC-2 §2.1` |
| RF-20 | O sistema deve exibir um painel individual com gráficos das médias por competência, comparando avaliação do chefe, autoavaliação, média dos pares e média dos subordinados. | Alta | `SE-09`, `SQ-04`, `SA-08`, `SA-10` |
| RF-21 | O sistema deve gerar relatórios por equipe e por área com média por competência e comparação entre pessoas, sem identificar respondentes. | Alta | `SE-10`, `SQ-05`, `SA-09` |
| RF-22 | O sistema deve exibir os comentários das perguntas abertas sem identificar o autor e permitir que o RH oculte comentários ofensivos. | Média | `SE-23`, `SQ-04` |
| RF-23 | O sistema deve exportar os resultados individuais e de equipe em PDF e Excel. | Média | `SE-11`, `SQ-16`, `SA-11` |
| RF-24 | O sistema deve permitir que o RH libere o resultado ao colaborador e notificá-lo por e-mail quando isso ocorrer. | Média | `SQ-14` |
| RF-25 | O sistema deve permitir que o colaborador consulte, desde a abertura do ciclo, as competências e a descrição dos níveis da escala em que será avaliado. | Média | `SQ-18`, `SQ-15` |

### Acompanhamento e desenvolvimento

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RF-26 | O sistema deve manter o histórico de todos os ciclos e exibir a evolução das médias por competência de cada pessoa ao longo do tempo. | Alta | `SE-28`, `SQ-13`, `SA-13` |
| RF-27 | O sistema deve permitir que gestor e colaborador registrem um plano de desenvolvimento com metas ligadas a competências, ações (por exemplo, cursos), prazo e situação. | Alta | `SE-12`, `SQ-06`, `SA-12` |
| RF-28 | O sistema deve enviar lembretes trimestrais ao gestor e ao colaborador para revisar as metas e registrar o progresso. | Média | `SE-12`, `SQ-06` |

### Acesso e LGPD

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RF-29 | O sistema deve exibir o termo de consentimento no primeiro acesso de cada usuário e registrar data e hora do aceite; sem aceite, o usuário não acessa as funcionalidades. | Alta | `SE-15`, `SQ-10`, `DOC-1 §2.1` |
| RF-30 | O sistema deve permitir que o titular consulte o consentimento dado e solicite acesso, correção ou exclusão dos seus dados pessoais, conforme a política da empresa. | Alta | `DOC-1 §2.1` |
| RF-31 | O sistema deve exigir autenticação individual de cada usuário. | Alta | `SE-25` |
| RF-32 | O sistema deve permitir login com a conta corporativa da empresa (SSO), quando disponível. | Baixa | `SE-25` |
| RF-33 | O sistema deve ter os perfis RH (administrador), gestor e colaborador, com permissões distintas. | Alta | `SE-13`, `SA-15`, `DOC-1 §2.3` |

### Segurança e privacidade

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RNF-01 | Nenhum perfil, inclusive o RH, deve conseguir ver a autoria das respostas de pares e subordinados; essas respostas aparecem apenas agregadas. | Alta | `SE-05`, `SQ-02`, `SA-05`, `DOC-1 §2.2` |
| RNF-02 | Resultados agregados de pares ou de subordinados só devem ser exibidos quando houver no mínimo 3 respostas naquele grupo. | Alta | `SE-06`, `SA-06` |
| RNF-03 | O controle de visibilidade deve ser aplicado pelo servidor: o gestor acessa apenas dados da equipe direta e o colaborador apenas os próprios resultados. | Alta | `SE-13`, `DOC-1 §2.3`, `DOC-2 §2.4` |
| RNF-04 | Toda comunicação deve usar HTTPS (TLS 1.2 ou superior) e os dados pessoais e de avaliação devem ser armazenados criptografados. | Alta | `SE-16`, `DOC-1 §2.4` |
| RNF-05 | O sistema deve registrar em log de auditoria os acessos a resultados, as reaberturas de avaliação e a moderação de comentários (usuário, data, hora e motivo). | Alta | `SE-16`, `SE-24`, `DOC-1 §2.4` |
| RNF-06 | O sistema deve coletar apenas os dados pessoais necessários à avaliação (princípio da necessidade). | Alta | `DOC-1 §2.2` |

### Usabilidade e portabilidade

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RNF-07 | A interface web deve ser responsiva e utilizável em computador, tablet e celular, e a arquitetura deve permitir um aplicativo móvel futuro. | Alta | `SE-17`, `SQ-03`, `DOC-2 §2.2` |
| RNF-08 | A avaliação de uma pessoa deve poder ser concluída em até 10 minutos e a autoavaliação completa em até 15 minutos por um usuário sem treinamento. | Alta | `SE-19`, `SQ-07` |
| RNF-09 | O termo de consentimento e as mensagens do sistema devem usar linguagem simples, sem jargão jurídico ou técnico. | Média | `SQ-11` |
| RNF-10 | O sistema deve permitir personalizar a identidade visual (logotipo e cores da empresa). | Baixa | `SA-14` |

### Desempenho

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RNF-11 | O sistema deve suportar o acesso simultâneo de todos os usuários da organização no encerramento do ciclo, com páginas e painéis carregando em até 3 segundos (meta a validar na 2ª etapa). | Alta | `SE-18`, `DOC-2 §2.3` |

### Confiabilidade e manutenibilidade

| ID | Requisito | Prioridade | Origem |
|----|-----------|------------|--------|
| RNF-12 | Os cálculos de médias e consolidações devem ser exatos e reproduzíveis a partir das respostas armazenadas. | Alta | `DOC-2 §2.1` |
| RNF-13 | Competências, questionários e escalas devem ser configuráveis pelo RH na interface, sem alteração de código ou nova implantação. | Alta | `SE-02`, `DOC-2 §2.5` |

## Resumo

| Tipo | Quantidade |
|------|-----------:|
| Funcionais (RF) | 34 |
| Não funcionais (RNF) | 13 |
| **Total** | **47** |
| Prioridade alta | 33 |
| Prioridade média | 12 |
| Prioridade baixa | 2 |
