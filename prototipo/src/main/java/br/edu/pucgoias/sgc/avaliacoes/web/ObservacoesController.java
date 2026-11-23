package br.edu.pucgoias.sgc.avaliacoes.web;

import br.edu.pucgoias.sgc.avaliacoes.servico.DadosObservacao;
import br.edu.pucgoias.sgc.avaliacoes.servico.ObservacaoResumo;
import br.edu.pucgoias.sgc.avaliacoes.servico.ObservacaoService;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * UC-08 Registrar observações sobre a equipe (RF-17). Só gestor; a equipe é conferida no serviço (RNF-03).
 *
 * <ul>
 *   <li>{@code GET /observacoes}: a equipe direta com o resumo das observações e um atalho para registrar;</li>
 *   <li>{@code GET /observacoes/{colaboradorId}}: lista cronológica e formulário de nova observação;</li>
 *   <li>{@code POST /observacoes}: registra; {@code POST .../{id}/editar} e {@code .../{id}/excluir}: só o autor.</li>
 * </ul>
 */
@Controller
@PreAuthorize("hasRole('GESTOR')")
@RequestMapping("/observacoes")
public class ObservacoesController {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final ObservacaoService observacoes;

    public ObservacoesController(ObservacaoService observacoes) {
        this.observacoes = observacoes;
    }

    @GetMapping
    public String equipe(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", ObservacaoForm.nova(null, hoje()));
        }
        model.addAttribute("equipe", observacoes.equipe(eu));
        model.addAttribute("hoje", hoje());
        model.addAttribute("diasSemObservacao", ObservacaoService.DIAS_SEM_OBSERVACAO);
        return "avaliacoes/observacoes-equipe";
    }

    @GetMapping("/{colaboradorId}")
    public String colaborador(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                              Model model) {
        return telaDoColaborador(eu, colaboradorId, ObservacaoForm.nova(colaboradorId, hoje()), null, model);
    }

    @PostMapping
    public String registrar(@AuthenticationPrincipal UsuarioAutenticado eu,
                            @ModelAttribute("form") ObservacaoForm form, BindingResult binding, Model model,
                            RedirectAttributes ra) {
        try {
            observacoes.registrar(eu, dados(form, binding));
            ra.addFlashAttribute("sucesso", "Observação registrada.");
            return "redirect:/observacoes/" + form.getColaboradorId();
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            if (form.getColaboradorId() == null) {
                model.addAttribute("abrirFormulario", true);
                return equipe(eu, model);
            }
            return telaDoColaborador(eu, form.getColaboradorId(), form, null, model);
        }
    }

    @GetMapping("/{colaboradorId}/{id}/editar")
    public String editar(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                         @PathVariable Long id, Model model) {
        ObservacaoResumo o = observacoes.paraEditar(eu, id);
        return telaDoColaborador(eu, o.colaboradorId(), ObservacaoForm.de(o), id, model);
    }

    @PostMapping("/{colaboradorId}/{id}/editar")
    public String alterar(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                          @PathVariable Long id, @ModelAttribute("form") ObservacaoForm form, BindingResult binding,
                          Model model, RedirectAttributes ra) {
        ObservacaoResumo atual = observacoes.paraEditar(eu, id);
        form.setColaboradorId(atual.colaboradorId());
        try {
            observacoes.alterar(eu, id, dados(form, binding));
            ra.addFlashAttribute("sucesso", "Observação alterada.");
            return "redirect:/observacoes/" + atual.colaboradorId();
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return telaDoColaborador(eu, atual.colaboradorId(), form, id, model);
        }
    }

    @PostMapping("/{colaboradorId}/{id}/excluir")
    public String excluir(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long colaboradorId,
                          @PathVariable Long id, RedirectAttributes ra) {
        Long dono = observacoes.paraEditar(eu, id).colaboradorId();
        observacoes.excluir(eu, id);
        ra.addFlashAttribute("sucesso", "Observação excluída.");
        return "redirect:/observacoes/" + dono;
    }

    private String telaDoColaborador(UsuarioAutenticado eu, Long colaboradorId, ObservacaoForm form, Long editandoId,
                                     Model model) {
        var lista = observacoes.doColaborador(eu, colaboradorId);
        model.addAttribute("colaborador", observacoes.colaborador(eu, colaboradorId));
        model.addAttribute("observacoes", lista);
        model.addAttribute("positivos", lista.stream().filter(o -> o.observacao().positivo()).count());
        model.addAttribute("melhorias", lista.stream().filter(o -> !o.observacao().positivo()).count());
        model.addAttribute("form", form);
        model.addAttribute("editandoId", editandoId);
        model.addAttribute("hoje", hoje());
        return "avaliacoes/observacoes-colaborador";
    }

    /** Data em formato inválido vira data vazia; o serviço explica o que falta. */
    private static DadosObservacao dados(ObservacaoForm form, BindingResult binding) {
        if (binding.hasFieldErrors("dataFato")) {
            form.setDataFato(null);
        }
        if (binding.hasFieldErrors("colaboradorId")) {
            form.setColaboradorId(null);
        }
        return form.paraDados();
    }

    private static LocalDate hoje() {
        return LocalDate.now(ZONA);
    }
}
