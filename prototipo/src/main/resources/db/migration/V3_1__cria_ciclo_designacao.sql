-- Pacote ciclos (UC-02, UC-07): ciclos, questionário de cada cargo, avaliadores externos e designações.

create table ciclo (
    id                 bigint generated always as identity primary key,
    nome               varchar(120) not null,
    tipo_chefe         boolean      not null default false, -- chefe avalia o colaborador
    tipo_auto          boolean      not null default false, -- autoavaliação
    tipo_pares         boolean      not null default false,
    tipo_subordinados  boolean      not null default false, -- subordinados avaliam o líder (360º)
    data_abertura      date         not null,
    data_encerramento  date         not null,
    situacao           varchar(20)  not null check (situacao in ('RASCUNHO', 'AGENDADO', 'ABERTO', 'ENCERRADO')),
    resultado_liberado boolean      not null default false, -- RF-24
    liberado_em        timestamp with time zone,
    aberto_em          timestamp with time zone,
    encerrado_em       timestamp with time zone,
    criado_em          timestamp with time zone not null default now(),
    check (data_encerramento > data_abertura)
);

-- Questionário publicado usado para cada cargo avaliado no ciclo.
create table ciclo_questionario (
    ciclo_id        bigint not null references ciclo (id) on delete cascade,
    cargo_id        bigint not null references cargo (id),
    questionario_id bigint not null references questionario (id),
    primary key (ciclo_id, cargo_id)
);

-- Avaliador externo: só nome e e-mail (RNF-06, RF-34).
create table avaliador_externo (
    id       bigint generated always as identity primary key,
    ciclo_id bigint       not null references ciclo (id) on delete cascade,
    nome     varchar(120) not null,
    email    varchar(160) not null
);

-- Quem avalia quem, e em que relação. Guarda só o andamento (RF-13); a resposta enviada não aponta
-- para a designação (RNF-01, DA-06).
create table designacao (
    id                   bigint generated always as identity primary key,
    ciclo_id             bigint      not null references ciclo (id) on delete cascade,
    avaliado_id          bigint      not null references usuario (id),
    avaliador_id         bigint references usuario (id),
    avaliador_externo_id bigint references avaliador_externo (id) on delete cascade,
    relacao              varchar(15) not null check (relacao in ('CHEFE', 'AUTO', 'PAR', 'SUBORDINADO', 'EXTERNO')),
    situacao             varchar(20) not null default 'NAO_INICIADA'
                         check (situacao in ('NAO_INICIADA', 'EM_PREENCHIMENTO', 'ENVIADA')),
    token                varchar(64) unique,   -- link individual do avaliador externo
    convite_enviado_em   timestamp with time zone,
    ultimo_lembrete_em   timestamp with time zone,
    check ((avaliador_id is not null) <> (avaliador_externo_id is not null)),
    check ((relacao = 'EXTERNO') = (avaliador_externo_id is not null)),
    check (relacao <> 'AUTO' or avaliador_id = avaliado_id),
    check (relacao = 'AUTO' or avaliador_id is null or avaliador_id <> avaliado_id)
);

create index ix_designacao_ciclo on designacao (ciclo_id);
create index ix_designacao_avaliador on designacao (avaliador_id);
create unique index ux_designacao_interna on designacao (ciclo_id, avaliado_id, avaliador_id, relacao)
    where avaliador_id is not null;
