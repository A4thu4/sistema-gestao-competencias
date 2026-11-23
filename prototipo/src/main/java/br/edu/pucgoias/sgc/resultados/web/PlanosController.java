package br.edu.pucgoias.sgc.resultados.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.resultados.servico.PlanoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * UC-06 Gerenciar plano de desenvolvimento. Quem pode ver e alterar cada plano é conferido no
 * {@link PlanoService} (RNF-03).
 *
 * <ul>
 *   <li>{@code GET /planos}: gestor, a equipe direta; RH, todas as pessoas; colaborador vai para o próprio plano;</li>
 *   <li>{@code GET /planos/{pessoa}}: metas, sugestões e linha do tempo;</li>
 *   <li>{@code POST /planos/{pessoa}/metas}: nova meta (gestor direto);</li>
 *   <li>{@code POST /planos/metas/{meta}/atualizacoes}: progresso e situação (a pessoa ou o gestor direto);</li>
 *   <li>{@code POST /planos/{pessoa}/lembrete}: lembrete de revisão agora (gestor direto).</li>
 * </ul>
 */
@Controller
@RequestMapping("/planos")
public class PlanosController {

    private final PlanoService planos;

    public PlanosController(PlanoService planos) {
        this.planos = planos;
    }

    @GetMapping
    public String inicio(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        if (!eu.ehRh() && !eu.ehGestor()) {
            return "redirect:/planos/" + eu.id();
        }
        model.addAttribute("pessoas", planos.pessoas(eu));
        model.addAttribute("meuPlanoAbertas", planos.metasAbertasDe(eu.id()));
        return "resultados/planos";
    }

    @GetMapping("/{colaboradorId}")
    public String plano(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                        Model model) {
        return tela(eu, colaboradorId, new MetaForm(), model);
    }

    @PostMapping("/{colaboradorId}/metas")
    @PreAuthorize("hasRole('GESTOR')")
    public String criar(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                        @ModelAttribute("form") MetaForm form, BindingResult binding, Model model,
                        RedirectAttributes ra) {
        if (binding.hasFieldErrors("prazo")) {
            form.setPrazo(null);
        }
        if (binding.hasFieldErrors("competenciaId")) {
            form.setCompetenciaId(null);
        }
        try {
            planos.criarMeta(eu, colaboradorId, form.paraDados());
            ra.addFlashAttribute("sucesso", "Meta registrada no plano. A pessoa foi avisada por e-mail.");
            return "redirect:/planos/" + colaboradorId;
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("abrirFormulario", true);
            return tela(eu, colaboradorId, form, model);
        }
    }

    @PostMapping("/metas/{metaId}/atualizacoes")
    public String atualizar(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long metaId,
                            @RequestParam(required = false) String situacao,
                            @RequestParam(required = false) String progresso,
                            @RequestParam(required = false) Long colaboradorId, RedirectAttributes ra) {
        try {
            Long dono = planos.atualizarMeta(eu, metaId, situacao, progresso);
            ra.addFlashAttribute("sucesso", "Atualização registrada na linha do tempo da meta.");
            return "redirect:/planos/" + dono + "#meta-" + metaId;
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/planos/" + (colaboradorId != null ? colaboradorId : eu.id()) + "#meta-" + metaId;
        }
    }

    @PostMapping("/{colaboradorId}/lembrete")
    @PreAuthorize("hasRole('GESTOR')")
    public String lembrete(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                           RedirectAttributes ra) {
        try {
            int metas = planos.lembrarAgora(eu, colaboradorId);
            ra.addFlashAttribute("sucesso", "Lembrete de revisão enviado por e-mail a você e à pessoa ("
                    + metas + (metas == 1 ? " meta aberta)." : " metas abertas)."));
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("aviso", e.getMessage());
        }
        return "redirect:/planos/" + colaboradorId;
    }

    private String tela(UsuarioAutenticado eu, Long colaboradorId, MetaForm form, Model model) {
        model.addAttribute("plano", planos.plano(eu, colaboradorId));
        model.addAttribute("form", form);
        model.addAttribute("situacoes", PlanoService.situacoes());
        model.addAttribute("diasLembrete", PlanoService.DIAS_LEMBRETE);
        return "resultados/plano";
    }
}
