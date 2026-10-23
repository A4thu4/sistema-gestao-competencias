# Matriz de rastreabilidade horizontal

Rastreia relações **dentro do mesmo nível**: requisito × requisito e caso de uso × caso de uso. Serve para avaliar o impacto de uma mudança (se um requisito mudar, o que mais é afetado?).

## 1. Requisito × requisito

Tipos de relação: **depende de** (precisa do outro para existir), **restringido por** (o outro impõe regra), **conflita com** (exige decisão).

| Requisito | Relação | Requisito(s) | Observação |
|-----------|---------|--------------|------------|
| _..._ | _..._ | _..._ | _..._ |

## 2. Caso de uso × caso de uso

Tipos de relação: **pré-requisito de**, `<<include>>`, `<<extend>>`.

| De | Relação | Para | Observação |
|----|---------|------|------------|
| _..._ | _..._ | _..._ | _..._ |

Responsável: A definir (issue #34)
