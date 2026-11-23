package br.edu.pucgoias.sgc.compartilhado.dominio;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Evento de domínio que pede um registro no log de auditoria (RNF-05, DA-08).
 *
 * <p>Os serviços dos pacotes publicam este evento com {@code ApplicationEventPublisher}; quem grava é o
 * {@code AuditoriaService} (padrão Observer). Assim nenhum pacote de negócio depende do log.
 */
public record EventoAuditoria(String usuario, String acao, String detalhe, String motivo) {

    /** Cria o evento com o usuário logado (ou "sistema", quando não há usuário, como nas rotinas agendadas). */
    public static EventoAuditoria de(String acao, String detalhe, String motivo) {
        return new EventoAuditoria(usuarioAtual(), acao, detalhe, motivo);
    }

    public static EventoAuditoria de(String acao, String detalhe) {
        return de(acao, detalhe, null);
    }

    private static String usuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return "sistema";
        }
        return auth.getName();
    }
}
