package br.edu.pucgoias.sgc.privacidade.web;

import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.privacidade.servico.ConsentimentoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * O usuário autenticado que ainda não aceitou a versão vigente do termo é levado para {@code /privacidade/termo}
 * antes de qualquer outra tela.
 *
 * <p>Para não consultar o banco a cada requisição, a sessão guarda o id do termo aceito; quando o RH publica uma
 * versão nova, o id vigente muda e a conferência volta a ser feita.
 */
public class BarreiraDoTermo implements HandlerInterceptor {

    static final String TERMO_ACEITO = "sgc.privacidade.termoAceito";

    private final ConsentimentoService consentimento;

    public BarreiraDoTermo(ConsentimentoService consentimento) {
        this.consentimento = consentimento;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioAutenticado eu)) {
            return true;
        }
        Optional<Long> vigente = consentimento.idTermoVigente();
        if (vigente.isEmpty()) {
            return true;
        }
        HttpSession sessao = req.getSession(false);
        if (sessao != null && vigente.get().equals(sessao.getAttribute(TERMO_ACEITO))) {
            return true;
        }
        if (consentimento.aceitou(eu.id(), vigente.get())) {
            req.getSession().setAttribute(TERMO_ACEITO, vigente.get());
            return true;
        }
        resp.sendRedirect(req.getContextPath() + "/privacidade/termo");
        return false;
    }
}
