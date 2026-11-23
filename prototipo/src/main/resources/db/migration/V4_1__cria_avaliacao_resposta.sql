-- Pacote avaliacoes (UC-03, UC-08, UC-09).
--
-- Anonimato (RNF-01, DA-06): enquanto o avaliador preenche, as respostas ficam em rascunho_resposta, ligadas à
-- designação, para o salvamento automático (RF-14). No envio, elas são copiadas para avaliacao_enviada/resposta e o
-- rascunho é apagado. A avaliação enviada guarda só ciclo, avaliado e relação: não tem designação, avaliador,
-- data nem identificador sequencial (o id é UUID aleatório), então nenhum perfil consegue ligar a resposta a quem
-- respondeu. O campo verificador é um HMAC da designação com uma chave do servidor; ele só serve para o RH reabrir
-- uma avaliação a pedido do próprio avaliador (UC-09) e nunca aparece em tela, relatório ou exportação.

create table rascunho_resposta (
    id            bigint generated always as identity primary key,
    designacao_id bigint    not null references designacao (id) on delete cascade,
    pergunta_id   bigint    not null references pergunta (id),
    nota          smallint,
    nao_avalia    boolean   not null default false,    -- "não tenho como avaliar" (RF-06)
    texto         varchar(2000),
    atualizado_em timestamp with time zone not null default now(),
    unique (designacao_id, pergunta_id)
);

create table avaliacao_enviada (
    id              uuid        primary key,
    ciclo_id        bigint      not null references ciclo (id),
    avaliado_id     bigint      not null references usuario (id),
    relacao         varchar(15) not null check (relacao in ('CHEFE', 'AUTO', 'PAR', 'SUBORDINADO', 'EXTERNO')),
    questionario_id bigint      not null references questionario (id),
    verificador     varchar(64) not null unique
);

create index ix_avaliacao_enviada_ciclo_avaliado on avaliacao_enviada (ciclo_id, avaliado_id);

create table resposta (
    id                   bigint generated always as identity primary key,
    avaliacao_enviada_id uuid     not null references avaliacao_enviada (id) on delete cascade,
    pergunta_id          bigint   not null references pergunta (id),
    nota                 smallint,
    nao_avalia           boolean  not null default false,
    texto                varchar(2000),
    comentario_oculto    boolean  not null default false, -- moderação (UC-09, RF-22)
    motivo_ocultacao     varchar(300),
    check (nota is null or nota between 1 and 10),
    check (not (nao_avalia and nota is not null))
);

create index ix_resposta_avaliacao on resposta (avaliacao_enviada_id);

-- Observações do gestor ao longo do período (UC-08, RF-17).
create table observacao (
    id             bigint generated always as identity primary key,
    gestor_id      bigint        not null references usuario (id),
    colaborador_id bigint        not null references usuario (id),
    tipo           varchar(10)   not null check (tipo in ('POSITIVO', 'MELHORIA')),
    descricao      varchar(1000) not null,
    data_fato      date          not null,
    criada_em      timestamp with time zone not null default now()
);

create index ix_observacao_colaborador on observacao (colaborador_id, data_fato);
