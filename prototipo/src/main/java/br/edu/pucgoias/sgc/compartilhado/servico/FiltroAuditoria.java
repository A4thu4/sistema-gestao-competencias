package br.edu.pucgoias.sgc.compartilhado.servico;

import java.time.LocalDate;

/** Filtros da consulta ao log de auditoria. Campo vazio não filtra. */
public record FiltroAuditoria(String usuario, String acao, LocalDate de, LocalDate ate) {

    public static FiltroAuditoria nenhum() {
        return new FiltroAuditoria(null, null, null, null);
    }

    public boolean vazio() {
        return isBlank(usuario) && isBlank(acao) && de == null && ate == null;
    }

    static boolean isBlank(String texto) {
        return texto == null || texto.isBlank();
    }
}
