package br.edu.pucgoias.sgc.compartilhado.dominio;

/**
 * Evento de domínio que pede um registro no log de auditoria (RNF-05, DA-08).
 *
 * <p>Os serviços dos pacotes publicam este evento com {@code ApplicationEventPublisher}; quem grava é o
 * {@code AuditoriaService} (padrão Observer). Assim nenhum pacote de negócio depende do log. Sem {@code usuario},
 * o {@code AuditoriaService} registra o usuário logado (ou "sistema", nas rotinas agendadas).
 */
public record EventoAuditoria(String usuario, String acao, String detalhe, String motivo) {

    public static EventoAuditoria de(String acao, String detalhe, String motivo) {
        return new EventoAuditoria(null, acao, detalhe, motivo);
    }

    public static EventoAuditoria de(String acao, String detalhe) {
        return de(acao, detalhe, null);
    }

    public EventoAuditoria comUsuario(String nome) {
        return new EventoAuditoria(nome, acao, detalhe, motivo);
    }
}
