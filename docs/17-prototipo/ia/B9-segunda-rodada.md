# Prompt B9: Segunda rodada: comentários de exemplo e autoavaliação (texto completo)

Ferramenta: Anthropic, Claude (Claude Code, modelo Opus 5.5), como tarefa atribuída a um agente desenvolvedor. Data: 25/11/2026. Junto com este texto, o agente recebeu as instruções do [B6](B6-instrucoes-agentes.md).

Título da tarefa: SGC: comentários de exemplo sem repetição e autoavaliação à parte

```text
Tarefa-mãe: GAC-78. Segunda rodada, achada nos prints depois de juntar os dois branches. O seu branch `melhoria/funcional` já foi avançado para a versão juntada (commit `dfa2e25`, com o trabalho do Dev SGC Visual): trabalhe em cima dele.

**Prazo: hoje, 25/11, até 16h00.**

1. **Comentários repetidos nos dados de exemplo.** Em `/resultados/1/pessoas/4` (Carla, ciclo 2025.2) a frase "Os clientes elogiam muito a paciência dela no atendimento." aparece 3 vezes; em `/resultados/2/pessoas/4` (2026.1) "É a pessoa que a equipe procura quando o cliente está nervoso." aparece 2 vezes. Cada avaliador escreve com as próprias palavras; nenhuma frase repetida no mesmo resultado. Confira todas as pessoas, não só a Carla.
2. **Comentário da autoavaliação misturado aos anônimos.** A Carla vê "Acho que evoluí neste período, mas preciso estudar mais o produto." dentro de "Comentários · Sem identificação de quem escreveu". Mostrar à parte, como "Seu comentário (autoavaliação)", e só para a própria pessoa; gestor e RH veem como "Autoavaliação". Valer também no PDF e no Excel se eles listam comentários.
3. **Competência errada nos comentários.** Todos aparecem como "Comunicação · Comentários e sugestões", inclusive os que falam do produto ou de resolver chamados. Cada comentário de exemplo deve ficar na competência do assunto dele.
4. **Travessão nos nomes dos questionários** ("Questionário — Desenvolvedor"): trocar por "Questionário Desenvolvedor" ou "Desenvolvedor (questionário)", nos dados de exemplo e onde os testes conferem.

Rode `sgc-vm.sh` até `BUILD_OK`, faça commits pequenos e comente o resultado. Depois mude para `in_review`.
```
