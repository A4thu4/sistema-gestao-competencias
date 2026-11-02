# Casos de uso

Atores, diagrama e descrição dos casos de uso essenciais, no modelo do capítulo 6 do Larman. Regra do enunciado: **pelo menos 1 caso de uso completo por integrante**.

| Arquivo | Descrição | Status |
|---------|-----------|--------|
| modelo-caso-de-uso.md | Modelo do formato completo (*fully dressed*) | Concluído |
| diagrama-casos-de-uso.* | Diagrama de casos de uso (fonte e imagem) | A fazer |
| UC-NN-*.md | Um arquivo por caso de uso completo | A fazer |

## Atores

Identificados a partir dos usuários e serviços externos do [Documento Visão](../10-documento-visao/documento-visao.md) (seções 3.2 e 4.1).

| Ator | Tipo | Descrição |
|------|------|-----------|
| Usuário | Principal (abstrato) | Qualquer pessoa com conta no sistema; generaliza RH, Gestor e Colaborador |
| RH | Principal | Mantém competências e questionários, cria e acompanha ciclos, libera resultados e modera comentários |
| Gestor | Principal | Avalia a equipe, registra observações, acompanha o ciclo e conduz o plano de desenvolvimento |
| Colaborador | Principal | Faz a autoavaliação, avalia pares e o líder, consulta os próprios resultados e registra o progresso das metas |
| Avaliador externo | Principal | Cliente, fornecedor ou parceiro que responde por link individual, sem conta (`RF-34`) |
| Serviço de e-mail | Suporte | Envia convites, lembretes e notificações (`RF-12`, `RF-24`, `RF-28`) |
| Provedor de identidade (SSO) | Suporte | Autentica com a conta corporativa, quando disponível (`RF-32`) |

## Casos de uso essenciais

| ID | Caso de uso | Ator principal | Formato (completo/resumido) | Requisitos | Responsável |
|----|-------------|----------------|-----------------------------|------------|-------------|
| UC-01 | _..._ | _..._ | _..._ | _..._ | A definir |

## Casos de uso resumidos

> Um parágrafo por caso de uso (formato *brief* do Larman): cenário principal de sucesso em poucas frases.

## Convenções

- Arquivo: `UC-NN-nome-curto.md`, a partir de [modelo-caso-de-uso.md](modelo-caso-de-uso.md).
- Casos de uso derivados dos objetivos no nível do usuário do [Documento Visão](../10-documento-visao/documento-visao.md).
- Casos de uso no nível de **processo elementar de negócio** (Larman): uma pessoa, um lugar, uma sessão, resultado de valor.
- Cada caso de uso cita os `RF`/`RNF` que cobre; essas ligações alimentam a [matriz vertical](../14-rastreabilidade/matriz-vertical.md).

Responsável: Arthur Mamedes Borges (issue #25)
