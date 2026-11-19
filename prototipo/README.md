# Protótipo — Sistema de Gestão de Competências

Código do protótipo. A documentação (telas, roteiro de demonstração) fica em [`docs/17-prototipo`](../docs/17-prototipo/README.md) e a arquitetura em [`docs/15-documento-arquitetura`](../docs/15-documento-arquitetura/README.md).

## Stack

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 (Web, Data JPA, Validation; Security entra na #55) |
| Telas | Thymeleaf + Bootstrap 5 (CDN); gráficos com Chart.js (CDN) |
| Banco | PostgreSQL 16 em Docker |
| Migrações | Flyway |
| Build | Maven |

## Como rodar

Pré-requisitos: JDK 21, Maven 3.9 (ou o Maven embutido do IntelliJ IDEA) e Docker Desktop.

```bash
cd prototipo
docker compose up -d          # sobe o PostgreSQL em localhost:5432 (banco, usuário e senha: sgc)
mvn spring-boot:run           # sobe a aplicação em http://localhost:8080
```

No IntelliJ: abra a pasta `prototipo` como projeto Maven e rode `SgcApplication`.

Para apagar o banco e recriar tudo a partir das migrações: `docker compose down -v` e depois `docker compose up -d`.

## Organização do código

Um pacote por módulo de negócio (o diagrama C2 do Documento de Arquitetura mostra estes mesmos pacotes). Dentro de cada um, quatro camadas:

```
br.edu.pucgoias.sgc
├── identidade      login, perfis, usuários, cargos e equipes        (#55)
├── competencias    UC-01                                             (#56)
├── ciclos          UC-02                                             (#57)
├── avaliacoes      UC-03                                             (#58)
├── resultados      UC-04                                             (#59)
├── privacidade     UC-05                                             (#60)
└── compartilhado   configuração, erros e log de auditoria            (#60)

<pacote>
├── web             controllers: recebem a requisição e devolvem a tela
├── servico         regras do caso de uso (@Service, @Transactional)
├── dominio         entidades JPA e objetos de valor
└── repositorio     interfaces Spring Data (JpaRepository)
```

Regras:

- O controller só chama o serviço do próprio pacote. Nada de repositório no controller.
- Um pacote usa outro só pelo **serviço** dele, nunca pelo repositório ou pelas entidades internas.
- `compartilhado` não depende de nenhum pacote de negócio.
- Controle de acesso no servidor (`RNF-03`): toda consulta a dados de pessoas confere o perfil e a equipe do usuário logado.

## Telas

Templates em `src/main/resources/templates/<pacote>/`, usando o layout base criado na #55. Os protótipos do Figma estão em [`docs/12-prototipos`](../docs/12-prototipos/README.md).

## Migrações (banco)

Cada pacote tem a sua faixa de versões, para duas pessoas nunca criarem a mesma versão:

| Pacote | Faixa | Exemplo |
|--------|-------|---------|
| identidade | `V1_x` | `V1_1__cria_usuario_cargo_equipe.sql`, `V1_2__dados_exemplo.sql` |
| competencias | `V2_x` | `V2_1__cria_competencia_questionario.sql` |
| ciclos | `V3_x` | `V3_1__cria_ciclo_designacao.sql` |
| avaliacoes | `V4_x` | `V4_1__cria_avaliacao_resposta.sql` |
| resultados | `V5_x` | `V5_1__cria_resultado_consolidado.sql` |
| privacidade | `V6_x` | `V6_1__cria_termo_aceite_auditoria.sql` |

- As tabelas seguem o [modelo relacional](../docs/16-projeto-banco-dados/projeto-banco-dados.md) (#54). Mudou a tabela? Atualize o modelo e o [dicionário](../docs/16-projeto-banco-dados/dicionario-dados.md).
- Nunca edite uma migração que já está na `main`; crie a próxima da sua faixa.
- O JPA não cria tabelas (`ddl-auto: validate`): se a entidade não bater com a migração, a aplicação não sobe.

## Fluxo de trabalho

1. Pegue a sua issue no [Kanban](https://github.com/users/A4thu4/projects/6) e mova para "Em andamento".
2. Crie uma branch a partir da `main`: `feat/<pacote>` (por exemplo `feat/avaliacoes`).
3. Commits no padrão `tipo: descrição` (`feat:`, `fix:`, `docs:`), com `closes #NN` no último.
4. Abra um pull request para a `main` e peça revisão de outro integrante.

## Fora do escopo do protótipo

Registrado nas decisões do Documento de Arquitetura: envio real de e-mail (os convites aparecem no log), SSO, criptografia dos dados em repouso (`RNF-04`), teste de carga (`RNF-11`) e versão para aplicativo móvel.
