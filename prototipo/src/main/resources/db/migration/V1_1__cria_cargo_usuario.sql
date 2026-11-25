-- Pacote identidade (UC-10): cargos, usuários, perfis e estrutura da equipe.
-- A equipe de um gestor são os usuários cujo gestor_id aponta para ele; a área agrupa equipes nos relatórios.

create table cargo (
    id   bigint generated always as identity primary key,
    nome varchar(80) not null unique
);

create table usuario (
    id         bigint generated always as identity primary key,
    nome       varchar(120) not null,
    email      varchar(160) not null unique,
    senha_hash varchar(100) not null,
    perfil     varchar(20)  not null check (perfil in ('RH', 'GESTOR', 'COLABORADOR')),
    cargo_id   bigint       not null references cargo (id),
    gestor_id  bigint references usuario (id),
    area       varchar(80)  not null,
    ativo      boolean      not null default true,
    criado_em  timestamp with time zone not null default now(),
    check (gestor_id is null or gestor_id <> id)
);

create index ix_usuario_gestor on usuario (gestor_id);
create index ix_usuario_cargo on usuario (cargo_id);
