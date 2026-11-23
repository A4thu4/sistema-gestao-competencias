-- Pacotes privacidade (UC-05) e compartilhado (log de auditoria e caixa de saída de e-mails).

create table termo_consentimento (
    id             bigint generated always as identity primary key,
    versao         varchar(20)  not null unique,
    texto          text         not null,
    resumo_mudancas varchar(1000),          -- o que mudou em relação à versão anterior (UC-05, 2a)
    vigente_desde  timestamp with time zone not null default now()
);

-- Aceite com versão, data e hora (RF-29, ERS 3.4 regra 7).
create table aceite (
    id         bigint generated always as identity primary key,
    usuario_id bigint not null references usuario (id),
    termo_id   bigint not null references termo_consentimento (id),
    aceito_em  timestamp with time zone not null default now(),
    unique (usuario_id, termo_id)
);

-- Solicitações do titular (RF-30).
create table solicitacao_titular (
    id            bigint generated always as identity primary key,
    usuario_id    bigint        not null references usuario (id),
    protocolo     varchar(20)   not null unique,
    tipo          varchar(10)   not null check (tipo in ('ACESSO', 'CORRECAO', 'EXCLUSAO')),
    descricao     varchar(1000) not null,
    dado_incorreto varchar(200),
    valor_correto varchar(200),
    situacao      varchar(20)   not null default 'EM_ANALISE'
                  check (situacao in ('EM_ANALISE', 'ATENDIDA', 'ATENDIDA_PARCIAL', 'NEGADA')),
    justificativa varchar(1000),
    criada_em     timestamp with time zone not null default now(),
    concluida_em  timestamp with time zone,
    concluida_por bigint references usuario (id)
);

-- Log de auditoria (RNF-05, DA-08). Guarda o e-mail, e não a chave do usuário, para continuar legível mesmo
-- depois de uma exclusão de dados.
create table registro_auditoria (
    id          bigint generated always as identity primary key,
    usuario     varchar(160) not null,
    acao        varchar(60)  not null,
    detalhe     varchar(500),
    motivo      varchar(500),
    ocorrido_em timestamp with time zone not null default now()
);

create index ix_auditoria_ocorrido on registro_auditoria (ocorrido_em desc);

-- O protótipo não envia e-mail de verdade (fora do escopo): cada mensagem fica nesta caixa de saída,
-- visível ao RH, e no log da aplicação.
create table email_enviado (
    id        bigint generated always as identity primary key,
    para      varchar(160)  not null,
    assunto   varchar(200)  not null,
    corpo     varchar(4000) not null,
    tipo      varchar(30)   not null,
    criado_em timestamp with time zone not null default now()
);
