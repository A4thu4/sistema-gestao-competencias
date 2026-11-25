# Prompt B7: Melhorias de visual e textos (texto completo)

Ferramenta: Anthropic, Claude (Claude Code, modelo Opus 5.5), como tarefa atribuída a um agente desenvolvedor. Data: 25/11/2026. Junto com este texto, o agente recebeu as instruções do [B6](B6-instrucoes-agentes.md).

Título da tarefa: SGC: tirar a cara de IA e as telas sem nexo (visual e textos)

```text
Tarefa-mãe: GAC-78. Detalhe de cada item, com arquivos e linhas, no documento `revisao` da GAC-78.

**Prazo: hoje, 25/11, até 17h45.** Depois disso o operador junta os branches e publica para a demonstração da noite. Faça na ordem; o que não couber, comente e deixe.

Obrigatórias:
1. **M-07** textos com tom de IA ou sem nexo (login, visão geral de todos os perfis, plurais, observações, painel de resultado, breadcrumbs, editor, subtítulos longos, página do avaliador externo).
2. **M-08** identidade visual: marca "SGC" + empresa fictícia, logotipo SVG simples, cor primária própria em variável; passo a passo de `/ciclos/novo` alinhado. Manter o layout do Figma.
3. **M-03** auditoria e e-mails em linguagem de gente (rótulos de ação e de tipo de e-mail, sem IDs nem códigos de requisito).
4. **M-02** horário da auditoria e dos e-mails no fuso de Brasília.
5. **M-09** comentários e package-info enxutos, comentários HTML que não chegam ao navegador, sem travessões nos textos.

Não obrigatórias, se sobrar tempo: **O-03** (resultado no celular), **O-08** ("+ 1 avaliador externo"), **O-07** (filtro e paginação da auditoria).

Fronteira com o Dev SGC Funcional: ele mexe nos `DadosExemplo*`, em `PainelIndividual`/`MontagemPainel`/`Formatos`/`ResultadoAnteriorDoAvaliado`, em moderação, no catálogo de competências e em `EventoAuditoria`. Evite esses arquivos; no `painel.html`, só textos e títulos.
```
