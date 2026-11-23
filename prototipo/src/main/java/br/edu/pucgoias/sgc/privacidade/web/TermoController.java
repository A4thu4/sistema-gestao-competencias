package br.edu.pucgoias.sgc.privacidade.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.privacidade.servico.ConsentimentoService;
import br.edu.pucgoias.sgc.privacidade.servico.TermoResumo;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/** UC-05, passos 1 a 4 e extensões 1a, 2a e 3a: tela do termo de consentimento (TL-05). */
@Controller
public class TermoController {

    private final ConsentimentoService consentimento;

    public TermoController(ConsentimentoService consentimento) {
        this.consentimento = consentimento;
    }

    @GetMapping("/privacidade/termo")
    public String termo(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        Optional<TermoResumo> vigente = consentimento.termoVigente();
        if (vigente.isEmpty()) {
            return "redirect:/";
        }
        TermoResumo termo = vigente.get();
        boolean jaAceitou = consentimento.aceitou(eu.id(), termo.id());
        boolean novaVersao = !jaAceitou && consentimento.ultimoAceite(eu.id()).isPresent();
        model.addAttribute("termo", termo);
        model.addAttribute("jaAceitou", jaAceitou);
        model.addAttribute("novaVersao", novaVersao && termo.temMudancas());
        model.addAttribute("ultimoAceite", consentimento.ultimoAceite(eu.id()).orElse(null));
        return "privacidade/termo";
    }

    @PostMapping("/privacidade/termo")
    public String decidir(@AuthenticationPrincipal UsuarioAutenticado eu,
                          @RequestParam String decisao,
                          @RequestParam(required = false) Long termoId,
                          @RequestParam(defaultValue = "false") boolean concordo,
                          HttpSession sessao, RedirectAttributes redirect) {
        if (!"aceitar".equals(decisao)) {
            consentimento.registrarRecusa(eu);
            redirect.addFlashAttribute("recusado", true);
            return "redirect:/privacidade/termo";
        }
        if (!concordo || termoId == null) {
            redirect.addFlashAttribute("erro", "Marque a caixa \"Li e aceito\" para continuar.");
            return "redirect:/privacidade/termo";
        }
        try {
            consentimento.aceitar(eu, termoId);
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/privacidade/termo";
        }
        sessao.setAttribute(BarreiraDoTermo.TERMO_ACEITO, termoId);
        redirect.addFlashAttribute("sucesso", "Obrigado! Seu aceite foi registrado e o sistema está liberado.");
        return "redirect:/";
    }
}
