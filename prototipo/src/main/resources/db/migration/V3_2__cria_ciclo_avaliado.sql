-- Pacote ciclos (UC-02): quem é avaliado em cada ciclo e o cargo da pessoa no momento da escolha.
-- Sem esta tabela, uma pessoa avaliada que ainda não tem avaliador (extensão 9a) sumiria do ciclo, e uma troca de
-- cargo depois da criação mudaria o questionário usado. O questionário vem de ciclo_questionario pelo cargo daqui.
create table ciclo_avaliado (
    ciclo_id    bigint not null references ciclo (id) on delete cascade,
    avaliado_id bigint not null references usuario (id),
    cargo_id    bigint not null references cargo (id),
    primary key (ciclo_id, avaliado_id)
);
