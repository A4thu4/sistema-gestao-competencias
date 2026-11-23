package br.edu.pucgoias.sgc.competencias.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDuplicadaException;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * UC-01, extensão 2c: catálogo de competências (RF-01). Cadastrar, editar, desativar (com confirmação quando há
 * questionários publicados afetados) e reativar. Nada é apagado.
 */
@Controller
@PreAuthorize("hasRole('RH')")
@RequestMapping("/competencias/catalogo")
public class CompetenciasController {

    private static final String CATALOGO = "redirect:/competencias/catalogo";

    private final CompetenciaService catalogo;

    public CompetenciasController(CompetenciaService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public String catalogo(@RequestParam(required = false) String busca, Model model) {
        List<CompetenciaResumo> lista = catalogo.catalogo(busca);
        model.addAttribute("competencias", lista);
        model.addAttribute("uso", catalogo.usoPorCompetencia());
        model.addAttribute("busca", busca);
        model.addAttribute("totalCompetencias", catalogo.totalNoCatalogo());
        return "competencias/catalogo";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("form", new CompetenciaForm());
        return "competencias/competencia-form";
    }

    @PostMapping("/nova")
    public String cadastrar(@ModelAttribute("form") CompetenciaForm form, Model model, RedirectAttributes ra) {
        try {
            catalogo.cadastrar(form.paraDados());
            ra.addFlashAttribute("sucesso", "Competência “" + form.getNome().trim() + "” cadastrada.");
            return CATALOGO;
        } catch (RegraDeNegocioException e) {
            return voltarAoFormulario(e, model);
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CompetenciaResumo c = catalogo.buscar(id);
        model.addAttribute("competencia", c);
        model.addAttribute("form", CompetenciaForm.de(c));
        return "competencias/competencia-form";
    }

    @PostMapping("/{id}/editar")
    public String alterar(@PathVariable Long id, @ModelAttribute("form") CompetenciaForm form, Model model,
                          RedirectAttributes ra) {
        try {
            catalogo.alterar(id, form.paraDados());
            ra.addFlashAttribute("sucesso", "Competência “" + form.getNome().trim() + "” alterada.");
            return CATALOGO;
        } catch (RegraDeNegocioException e) {
            model.addAttribute("competencia", catalogo.buscar(id));
            return voltarAoFormulario(e, model);
        }
    }

    /** Confirmação: lista os questionários publicados que usam a competência. */
    @GetMapping("/{id}/desativar")
    public String confirmarDesativacao(@PathVariable Long id, Model model) {
        model.addAttribute("competencia", catalogo.buscar(id));
        model.addAttribute("afetados", catalogo.questionariosPublicadosQueUsam(id));
        return "competencias/desativar";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean confirmado,
                            RedirectAttributes ra) {
        if (!confirmado && !catalogo.questionariosPublicadosQueUsam(id).isEmpty()) {
            return "redirect:/competencias/catalogo/" + id + "/desativar";
        }
        catalogo.desativar(id);
        ra.addFlashAttribute("sucesso", "Competência “" + catalogo.buscar(id).nome() + "” desativada. Ela não aparece "
                + "nos novos questionários; o que já foi publicado e respondido continua guardado.");
        return CATALOGO;
    }

    @PostMapping("/{id}/reativar")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        catalogo.reativar(id);
        ra.addFlashAttribute("sucesso", "Competência “" + catalogo.buscar(id).nome() + "” reativada.");
        return CATALOGO;
    }

    private String voltarAoFormulario(RegraDeNegocioException e, Model model) {
        model.addAttribute("erro", e.getMessage());
        if (e instanceof CompetenciaDuplicadaException d) {
            model.addAttribute("duplicada", d.existente());
        }
        return "competencias/competencia-form";
    }
}
