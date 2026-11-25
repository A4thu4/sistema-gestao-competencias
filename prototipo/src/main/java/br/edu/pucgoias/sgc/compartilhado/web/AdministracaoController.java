package br.edu.pucgoias.sgc.compartilhado.web;

import br.edu.pucgoias.sgc.compartilhado.servico.AuditoriaService;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.compartilhado.servico.FiltroAuditoria;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

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
    public String auditoria(@RequestParam(defaultValue = "0") int pagina,
                            @RequestParam(required = false) String usuario,
                            @RequestParam(required = false) String acao,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
                            Model model) {
        FiltroAuditoria filtro = new FiltroAuditoria(usuario, acao, de, ate);
        model.addAttribute("registros", auditoria.listar(filtro, pagina));
        model.addAttribute("filtro", filtro);
        model.addAttribute("acoes", auditoria.acoes());
        return "compartilhado/auditoria";
    }

    @GetMapping("/emails")
    public String emails(@RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("emails", emails.listar(pagina));
        return "compartilhado/emails";
    }
}
