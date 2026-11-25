-- Pacote competencias (UC-01): catálogo de competências, questionários, perguntas e escala.

create table competencia (
    id        bigint generated always as identity primary key,
    nome      varchar(100) not null unique,
    descricao varchar(500) not null,
    tipo      varchar(20)  not null check (tipo in ('TECNICA', 'COMPORTAMENTAL')),
    ativa     boolean      not null default true      -- desativada, nunca apagada (ERS 3.4, regra 1)
);

-- Conjunto padrão de competências de cada cargo (RF-02).
create table cargo_competencia (
    cargo_id       bigint not null references cargo (id),
    competencia_id bigint not null references competencia (id),
    primary key (cargo_id, competencia_id)
);

-- Modelos prontos (RF-07) são questionários com modelo = true e sem cargo.
create table questionario (
    id           bigint generated always as identity primary key,
    nome         varchar(120) not null,
    cargo_id     bigint references cargo (id),
    escala_max   smallint     not null check (escala_max in (5, 10)),
    situacao     varchar(20)  not null check (situacao in ('RASCUNHO', 'PUBLICADO')),
    modelo       boolean      not null default false,
    origem_id    bigint references questionario (id), -- questionário copiado (RF-07)
    criado_em    timestamp with time zone not null default now(),
    publicado_em timestamp with time zone,
    check (modelo or cargo_id is not null or situacao = 'RASCUNHO')
);

create table questionario_competencia (
    questionario_id bigint   not null references questionario (id) on delete cascade,
    competencia_id  bigint   not null references competencia (id),
    ordem           smallint not null,
    primary key (questionario_id, competencia_id)
);

-- Perguntas de escala ou abertas (RF-03, RF-05). As colunas para_* dizem a que relação a pergunta se aplica (RF-11).
create table pergunta (
    id               bigint generated always as identity primary key,
    questionario_id  bigint       not null references questionario (id) on delete cascade,
    competencia_id   bigint       not null references competencia (id),
    texto            varchar(500) not null,
    tipo             varchar(10)  not null check (tipo in ('ESCALA', 'ABERTA')),
    obrigatoria      boolean      not null default true,
    ordem            smallint     not null,
    para_chefe       boolean      not null default true,
    para_auto        boolean      not null default true,
    para_par         boolean      not null default true,
    para_subordinado boolean      not null default true,
    para_externo     boolean      not null default true
);

create index ix_pergunta_questionario on pergunta (questionario_id);

-- Descrição de cada nível da escala (RF-04, RF-25).
create table nivel_escala (
    id              bigint generated always as identity primary key,
    questionario_id bigint       not null references questionario (id) on delete cascade,
    valor           smallint     not null,
    descricao       varchar(200) not null,
    unique (questionario_id, valor)
);
