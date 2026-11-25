-- Pacote resultados (UC-04, UC-06).

-- Médias consolidadas no encerramento do ciclo (RF-19). Guardam o número de avaliações de cada grupo para aplicar
-- o mínimo de 3 (RNF-02) e a escala usada, para comparar ciclos (RF-26, ERS 3.4 regra 9). São derivadas das
-- respostas e podem ser recalculadas a qualquer momento com o mesmo resultado (RNF-12).
create table resultado_consolidado (
    id             bigint generated always as identity primary key,
    ciclo_id       bigint       not null references ciclo (id),
    avaliado_id    bigint       not null references usuario (id),
    competencia_id bigint       not null references competencia (id),
    grupo          varchar(15)  not null check (grupo in ('CHEFE', 'AUTO', 'PAR', 'SUBORDINADO', 'EXTERNO')),
    media          numeric(4, 2),
    num_respostas  integer      not null,
    escala_max     smallint     not null,
    unique (ciclo_id, avaliado_id, competencia_id, grupo)
);

create index ix_resultado_avaliado on resultado_consolidado (avaliado_id, ciclo_id);

-- Plano de desenvolvimento (UC-06, RF-27, RF-28).
create table meta_desenvolvimento (
    id             bigint generated always as identity primary key,
    colaborador_id bigint        not null references usuario (id),
    gestor_id      bigint        not null references usuario (id),
    ciclo_id       bigint references ciclo (id),
    competencia_id bigint        not null references competencia (id),
    descricao      varchar(300)  not null,
    acoes          varchar(1000) not null,
    prazo          date          not null,
    situacao       varchar(15)   not null default 'NAO_INICIADA'
                   check (situacao in ('NAO_INICIADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')),
    criada_em      timestamp with time zone not null default now(),
    ultimo_lembrete_em timestamp with time zone
);

create index ix_meta_colaborador on meta_desenvolvimento (colaborador_id);

create table meta_atualizacao (
    id         bigint generated always as identity primary key,
    meta_id    bigint        not null references meta_desenvolvimento (id) on delete cascade,
    autor_id   bigint        not null references usuario (id),
    situacao   varchar(15)   not null check (situacao in ('NAO_INICIADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')),
    progresso  varchar(1000) not null,
    criada_em  timestamp with time zone not null default now()
);
