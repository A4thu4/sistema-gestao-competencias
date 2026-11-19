## 3. Requisitos não funcionais e como são atendidos

### 3.1 Requisitos não funcionais pelas características da ISO/IEC 25010

Autor: Lorenzo Oliveira Maciel · Revisor: Gabriel Araújo Caetano · Issue: #52 · Referências: ISO/IEC 25010; _outras_

> Dica: base na [lista de requisitos](../../08-requisitos/lista-requisitos.md) (`RNF-01` a `RNF-13`), na [pesquisa sobre a ISO/IEC 25010](../../03-pesquisa-documental/iso-25010.md) e na seção 3.6 da [ERS](../../13-especificacao-requisitos/ers-ieee830.md). O enunciado exige **todas** as características abaixo. Característica sem RNF levantado continua na tabela, com a justificativa (a ERS tem "atributos não levantados" na seção 3.6.6). Cada linha aponta o mecanismo da seção 3.2 que a atende.

**Tabela 3.1 — Requisitos não funcionais por característica da ISO/IEC 25010**

| Característica | Subcaracterística | RNF | Descrição resumida | Mecanismo (3.2) | Preocupações |
|-----------------|--------------------|------------|---------------------------|------------|------------|
| Adequação funcional | Correção funcional | `RNF-12` | _..._ | `DA-NN` | `PRE-NN` |
| Eficiência de desempenho | Comportamento no tempo, capacidade | `RNF-11` | _..._ | _..._ | _..._ |
| Compatibilidade | Interoperabilidade | _RNF ou "não levantado"_ | _..._ | _..._ | _..._ |
| Usabilidade | Apreensibilidade, operabilidade, acessibilidade | `RNF-08`, `RNF-09`, `RNF-10` | _..._ | _..._ | _..._ |
| Confiabilidade | Disponibilidade, recuperabilidade | _..._ | _..._ | _..._ | _..._ |
| Segurança (*security*) | Confidencialidade, integridade, responsabilização | `RNF-01` a `RNF-06` | _..._ | _..._ | _..._ |
| Manutenibilidade | Modularidade, modificabilidade | `RNF-13` | _..._ | _..._ | _..._ |
| Portabilidade | Adaptabilidade | `RNF-07` | _..._ | _..._ | _..._ |
| Segurança (*safety*) | Restrição operacional, identificação de riscos | _..._ | _Ex.: dano à pessoa avaliada por exposição de resultado ou por decisão baseada em dado errado_ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026), com base em ISO/IEC 25010.

### 3.2 Racional dos mecanismos arquiteturais adotados

Autor: Lorenzo Oliveira Maciel · Revisor: Arthur Mamedes Borges · Issue: #52 · Referências: _..._

> Dica: para cada decisão, o problema, as alternativas, o motivo da escolha e os RNF atendidos. Esta é a **única** tabela de decisões do documento: as seções 4 e 5 citam o `DA-NN` em vez de repetir a justificativa (critério de consistência, sem redundância). Coesão, acoplamento e padrões de projeto entram aqui como mecanismos de manutenibilidade, não como seção própria.

**Tabela 3.2 — Decisões de arquitetura**

| ID | Mecanismo ou decisão | Alternativas consideradas | Racional | RNF | Preocupações |
|-------|--------------------------|--------------------|---------------------------|----------|----------|
| DA-01 | Monolito modular em camadas (pacote por módulo de negócio; `web`, `servico`, `dominio`, `repositorio`) | Microsserviços; API REST + SPA | _..._ | `RNF-13`, `RNF-07` | _..._ |
| DA-02 | Java 21 + Spring Boot 3 | FastAPI; Node.js | _..._ | _..._ | _..._ |
| DA-03 | Renderização no servidor (Thymeleaf + Bootstrap responsivo) | React ou Angular no cliente | _..._ | `RNF-07`, `RNF-08` | _..._ |
| DA-04 | PostgreSQL com migrações Flyway | MySQL; geração automática pelo JPA | _..._ | `RNF-12` | _..._ |
| DA-05 | Controle de acesso no servidor (Spring Security, filtro por perfil e equipe) | Esconder opções só na interface | _..._ | `RNF-03` | _..._ |
| DA-06 | Resposta anônima sem vínculo com o avaliador; agregação com mínimo de 3 respostas | Guardar autoria e ocultar na tela | _..._ | `RNF-01`, `RNF-02` | _..._ |
| DA-07 | HTTPS terminado no proxy reverso; cookie de sessão `Secure`, `HttpOnly`, `SameSite` | _..._ | _..._ | `RNF-04` | _..._ |
| DA-08 | Log de auditoria por eventos de domínio (padrão Observer) no pacote `compartilhado` | Gravar log dentro de cada serviço | _..._ | `RNF-05` | _..._ |
| DA-NN | _..._ | _..._ | _..._ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).

**Tabela 3.3 — Riscos da arquitetura e mitigação**

| Risco | Impacto | Mitigação |
|------------------------------|------------------------------|----------------------------------------|
| _Ex.: pico de acessos no encerramento do ciclo_ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).
