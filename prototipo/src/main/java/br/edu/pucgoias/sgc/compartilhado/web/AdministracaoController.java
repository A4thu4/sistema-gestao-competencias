package br.edu.pucgoias.sgc.compartilhado.web;

import br.edu.pucgoias.sgc.compartilhado.servico.AuditoriaService;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Telas do RH para consultar o log de auditoria e a caixa de saída de e-mails. */
@Controller
@PreAuthorize("hasRole('RH')")
public class AdministracaoController {

    private final AuditoriaService auditoria;
    private final EmailService emails;

    public AdministracaoController(AuditoriaService auditoria, EmailService emails) {
        this.auditoria = auditoria;
        this.emails = emails;
    }

    @GetMapping("/auditoria")
    public String auditoria(@RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("registros", auditoria.listar(pagina));
        return "compartilhado/auditoria";
    }

    @GetMapping("/emails")
    public String emails(@RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("emails", emails.listar(pagina));
        return "compartilhado/emails";
    }
}
