# DOC-1: Lei Geral de Proteção de Dados (Lei nº 13.709/2018)

## 1. Descrição
Este documento mapeia as exigências da LGPD aplicáveis ao **Sistema de Gestão de Competências**, com foco no tratamento de dados pessoais e organizacionais (como avaliações de desempenho, dados de identificação e feedbacks do modelo 360º).

## 2. Artigos Aplicáveis e Requisitos Derivados

### 2.1. Gestão de Consentimento e Transparência (Art. 7º e Art. 8º)
*   **Requisito Funcional:** O sistema deve apresentar um Termo de Consentimento claro no primeiro acesso do usuário (seja avaliador ou avaliado), detalhando a finalidade da coleta de dados para gestão de RH.
*   **Requisito Funcional:** O titular dos dados deve ter a opção de gerenciar seu consentimento e, caso permitido pelas políticas da empresa, solicitar a exclusão de seus dados pessoais.

### 2.2. Anonimato e Sigilo nas Avaliações (Art. 6º, Inciso III - Princípio da Necessidade/Minimização)
*   **Requisito Funcional:** Para garantir a integridade do processo de Avaliação 360º, os resultados apresentados em relatórios e dashboards devem ser agregados e anonimizados. O avaliado não deve conseguir identificar quem aplicou notas específicas (pares ou subordinados).

### 2.3. Controle de Acesso e Perfis (Art. 46)
*   **Requisito de Segurança:** O sistema deve implementar um controle de acesso baseado em papéis (RBAC). Exemplo: Administrador/RH, Gestor e Colaborador.
*   **Requisito Não Funcional:** A visibilidade dos dados deve ser estritamente controlada; um gestor deve ter acesso apenas aos relatórios de sua equipe direta.

### 2.4. Segurança e Criptografia (Art. 46)
*   **Requisito Não Funcional:** Todos os dados pessoais e resultados de avaliações devem ser armazenados de forma segura e o tráfego na aplicação web deve utilizar protocolos criptografados (HTTPS/TLS).