package br.edu.pucgoias.sgc.ciclos.web;

import br.edu.pucgoias.sgc.ciclos.servico.AndamentoService;
import br.edu.pucgoias.sgc.ciclos.servico.ConviteService;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * UC-07 Acompanhar andamento do ciclo (RF-12, RF-13): RH e gestor. O recorte por equipe é feito no
 * {@link AndamentoService}, conferindo o usuário logado.
 */
@Controller
@PreAuthorize("hasAnyRole('RH','GESTOR')")
@RequestMapping("/ciclos/andamento")
public class AndamentoController {

    private final AndamentoService andamento;

    public AndamentoController(AndamentoService andamento) {
        this.andamento = andamento;
    }

    @GetMapping
    public String painel(@AuthenticationPrincipal UsuarioAutenticado eu,
                         @RequestParam(name = "ciclo", required = false) Long cicloId,
                         @RequestParam(name = "pessoa", required = false) Long pessoaId, Model model) {
        model.addAttribute("ciclos", andamento.ciclosParaAcompanhar());
        try {
            model.addAttribute("a", andamento.andamento(eu, cicloId, pessoaId));
        } catch (NaoEncontradoException e) {
            if (cicloId != null) {
                throw e;
            }
            model.addAttribute("semCiclo", e.getMessage());
        }
        return "ciclos/andamento";
    }

    @PostMapping("/{cicloId}/lembrete")
    public String lembrete(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long cicloId,
                           RedirectAttributes ra) {
        try {
            ConviteService.ResultadoEnvio r = andamento.enviarLembrete(eu, cicloId);
            if (r.enviados() == 0 && r.jaLembradosHoje() == 0) {
                ra.addFlashAttribute("aviso", "Ninguém com avaliação pendente: nenhum lembrete foi enviado.");
            } else if (r.enviados() == 0) {
                ra.addFlashAttribute("aviso", "Os pendentes já receberam lembrete hoje. Para não incomodar, o "
                        + "sistema envia no máximo um por dia.");
            } else {
                ra.addFlashAttribute("sucesso", "Lembrete enviado por e-mail a " + r.enviados()
                        + (r.enviados() == 1 ? " avaliador" : " avaliadores") + " com avaliações pendentes"
                        + (r.jaLembradosHoje() > 0 ? " (" + r.jaLembradosHoje() + " já tinham sido lembrados hoje)." : "."));
            }
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ciclos/andamento?ciclo=" + cicloId;
    }
}
