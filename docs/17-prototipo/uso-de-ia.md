# Uso de inteligência artificial no protótipo

Autor: Gabriel Araújo Caetano · Revisor: _a definir pela equipe_ · Issue: #63

O enunciado da Parte III pede que o uso de IA seja citado como referência, com a organização, o nome da ferramenta, a
URL, a data de acesso e o prompt completo, e que prompts com mais de um parágrafo fiquem em apêndice. Este arquivo
reúne o uso de IA no protótipo, no formato do Apêndice B do
[documento de arquitetura](../15-documento-arquitetura/), para ser copiado para lá na montagem do PDF.

## Como a IA foi usada

- **23/11/2026, implementação.** O código dos pacotes foi escrito com o Claude Code em sessões de desenvolvimento
  separadas por pacote. Cada sessão recebeu o texto comum (B.1) e o texto do seu pacote (B.2 a B.4). A base
  (identidade, competências, privacidade e o esquema do banco) foi feita na conversa principal do mesmo dia, cujo
  histórico completo não foi guardado. Gabriel conferiu o resultado de cada pacote no navegador e com os testes
  automatizados antes de juntar.
- **25/11/2026, revisão e melhorias.** Uma sessão de IA revisou o protótipo (B.5). As melhorias foram feitas por dois
  agentes de desenvolvimento (B.6 a B.9) e conferidas por Gabriel nas telas e nos testes. Registro completo em
  [revisao-2026-11-25.md](revisao-2026-11-25.md).
- Os commits de 23/11 trazem a linha `Co-Authored-By: Claude`. Os commits de 25/11 feitos com a ferramenta estão
  listados, um a um, em [revisao-2026-11-25.md](revisao-2026-11-25.md).

## Referências (para a seção de referências do documento)

ANTHROPIC. **Claude Code**. Ferramenta de programação com IA, modelo Claude Opus 5.5. Disponível em:
<https://claude.com/claude-code>. Acesso em: 23 nov. 2026. Questão: Apêndice B, Seções B.1 a B.4.

ANTHROPIC. **Claude Code**. Ferramenta de programação com IA, modelo Claude Opus 5.5. Disponível em:
<https://claude.com/claude-code>. Acesso em: 25 nov. 2026. Questão: Apêndice B, Seções B.5 a B.9.

## Apêndice B: prompts submetidos

**Tabela B.1 — Prompts usados no protótipo**

| Seção | Título | Ferramenta | Data | Usado em | Texto completo |
|-------|--------|------------|------|----------|----------------|
| B.1 | Regras comuns de desenvolvimento | Anthropic, Claude Code | 23/11/2026 | Todos os pacotes do protótipo | [B1-brief-comum.md](ia/B1-brief-comum.md) |
| B.2 | Pacote `ciclos` (UC-02, UC-07) | Anthropic, Claude Code | 23/11/2026 | `ciclos` | [B2-brief-ciclos.md](ia/B2-brief-ciclos.md) |
| B.3 | Pacote `resultados` (UC-04, UC-06) | Anthropic, Claude Code | 23/11/2026 | `resultados` | [B3-brief-resultados.md](ia/B3-brief-resultados.md) |
| B.4 | Pacote `avaliacoes`, parte 2 (UC-03, UC-08, UC-09) | Anthropic, Claude Code | 23/11/2026 | `avaliacoes` | [B4-brief-avaliacoes.md](ia/B4-brief-avaliacoes.md) |
| B.5 | Revisão do protótipo | Anthropic, Claude Code | 25/11/2026 | [revisao-2026-11-25.md](revisao-2026-11-25.md) | [B5-revisao.md](ia/B5-revisao.md) |
| B.6 | Instruções dos agentes de desenvolvimento | Anthropic, Claude Code | 25/11/2026 | Melhorias de 25/11 | [B6-instrucoes-agentes.md](ia/B6-instrucoes-agentes.md) |
| B.7 | Melhorias de visual e textos | Anthropic, Claude Code | 25/11/2026 | Itens M-02, M-03, M-07, M-08, M-09, O-03, O-07, O-08 | [B7-melhorias-visuais.md](ia/B7-melhorias-visuais.md) |
| B.8 | Melhorias funcionais e dados de exemplo | Anthropic, Claude Code | 25/11/2026 | Itens M-04, M-05, M-06, O-01, O-02, O-06, O-09 | [B8-melhorias-funcionais.md](ia/B8-melhorias-funcionais.md) |
| B.9 | Segunda rodada: comentários de exemplo e autoavaliação | Anthropic, Claude Code | 25/11/2026 | Segunda rodada da revisão | [B9-segunda-rodada.md](ia/B9-segunda-rodada.md) |

Fonte: elaborado pelos autores (2026).

Endereços de máquinas internas foram omitidos dos textos; o restante está como foi enviado.
