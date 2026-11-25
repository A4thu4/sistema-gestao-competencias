# Prompt B8: Melhorias funcionais e dados de exemplo (texto completo)

Ferramenta: Anthropic, Claude (Claude Code, modelo Opus 5.5), como tarefa atribuída a um agente desenvolvedor. Data: 25/11/2026. Junto com este texto, o agente recebeu as instruções do [B6](B6-instrucoes-agentes.md).

Título da tarefa: SGC: dados de exemplo com nexo e regras do resultado (funcional)

```text
Tarefa-mãe: GAC-78. Detalhe de cada item, com arquivos e linhas, no documento `revisao` da GAC-78.

**Prazo: hoje, 25/11, até 17h45.** Depois disso o operador junta os branches e publica para a demonstração da noite. Faça na ordem; o que não couber, comente e deixe.

Obrigatórias:
1. **M-04** dados de exemplo com nexo: Kátia sem designações nos ciclos 2026.1 e 2026.2; meta da Carla coerente com a competência; horários variados no plano; registros e e-mails da carga datados no período do ciclo (ou carga sem auditoria); Ana (RH) fora das designações de par. Ajuste os testes que contam números.
2. **M-05** aviso de anonimato por competência (lista as competências em que o grupo ficou de fora), sem contradizer a tabela.
3. **M-06** mesmo arredondamento (HALF_UP, no servidor) no resultado e no "Resultado do ciclo anterior" da avaliação.

Não obrigatórias, nesta ordem, se sobrar tempo: **O-01** (moderação sem permitir deduzir o autor), **O-09** (`EventoAuditoria` sem Spring Security no domínio), **O-06** (TL-01: abrir na aba Competências, busca no catálogo), **O-02** (ciclo 2025.2 encerrado nos dados de exemplo, para a evolução entre ciclos aparecer).

Fronteira com o Dev SGC Visual: ele mexe nos templates de layout, login, visão geral, auditoria, e-mails, ciclos, observações e nos textos do `painel.html`, em `RegistroAuditoria`/`EmailEnviado`, nos cartões do painel inicial, nos `package-info.java` e nas chamadas `EventoAuditoria.de(...)` dos serviços de resultados. Evite esses pontos; se precisar, mudança mínima.
```
