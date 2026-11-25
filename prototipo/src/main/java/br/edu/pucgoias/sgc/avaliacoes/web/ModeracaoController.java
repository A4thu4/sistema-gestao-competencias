package br.edu.pucgoias.sgc.avaliacoes.web;

import br.edu.pucgoias.sgc.avaliacoes.servico.ComentarioParaModeracao;
import br.edu.pucgoias.sgc.avaliacoes.servico.EnviadaParaReabrir;
import br.edu.pucgoias.sgc.avaliacoes.servico.ModeracaoService;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * UC-09 Reabrir avaliação e moderar comentários (só RH). As URLs usam o id da designação (reabrir) e o id da
 * resposta (comentário); o verificador e o id da avaliação enviada nunca aparecem (RNF-01).
 */
@Controller
@PreAuthorize("hasRole('RH')")
@RequestMapping("/moderacao")
public class ModeracaoController {

    private final ModeracaoService moderacao;

    public ModeracaoController(ModeracaoService moderacao) {
        this.moderacao = moderacao;
    }

    @GetMapping
    public String reabertura(@RequestParam(required = false) Long ciclo, Model model) {
        List<CicloResumo> ciclos = moderacao.ciclosParaReabrir();
        CicloResumo escolhido = escolher(ciclos, ciclo);
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("ciclo", escolhido);
        model.addAttribute("enviadas", escolhido == null ? List.of() : moderacao.enviadas(escolhido.id()));
        model.addAttribute("aba", "reabrir");
        return "avaliacoes/moderacao-reabrir";
    }

    @GetMapping("/reabrir/{designacaoId}")
    public String confirmarReabertura(@PathVariable Long designacaoId, Model model) {
        model.addAttribute("e", moderacao.paraReabrir(designacaoId));
        return "avaliacoes/moderacao-confirmar";
    }

    @PostMapping("/reabrir/{designacaoId}")
    public String reabrir(@PathVariable Long designacaoId, @RequestParam(required = false) String motivo, Model model,
                          RedirectAttributes ra) {
        EnviadaParaReabrir e = moderacao.paraReabrir(designacaoId);
        try {
            moderacao.reabrir(designacaoId, motivo);
            ra.addFlashAttribute("sucesso", "Avaliação de " + e.avaliadorNome() + " sobre " + e.avaliadoNome()
                    + " reaberta. O avaliador foi avisado por e-mail.");
            return "redirect:/moderacao?ciclo=" + e.designacao().cicloId();
        } catch (RegraDeNegocioException ex) {
            model.addAttribute("e", e);
            model.addAttribute("motivo", motivo);
            model.addAttribute("erro", ex.getMessage());
            return "avaliacoes/moderacao-confirmar";
        }
    }

    @GetMapping("/comentarios")
    public String comentarios(@RequestParam(required = false) Long ciclo, Model model) {
        List<CicloResumo> ciclos = moderacao.ciclosParaModerar();
        CicloResumo escolhido = escolher(ciclos, ciclo);
        var lista = escolhido == null ? List.<ComentarioParaModeracao>of()
                : moderacao.comentarios(escolhido.id());
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("ciclo", escolhido);
        model.addAttribute("comentarios", lista);
        model.addAttribute("ocultos", lista.stream().filter(c -> c.oculto()).count());
        model.addAttribute("aguardando", escolhido == null ? List.<String>of()
                : moderacao.aguardandoAvaliacoes(escolhido.id()));
        model.addAttribute("aba", "comentarios");
        return "avaliacoes/moderacao-comentarios";
    }

    @PostMapping("/comentarios/{respostaId}/ocultar")
    public String ocultar(@PathVariable Long respostaId, @RequestParam Long ciclo,
                          @RequestParam(required = false) String motivo, RedirectAttributes ra) {
        try {
            moderacao.ocultar(respostaId, motivo);
            ra.addFlashAttribute("sucesso", "Comentário ocultado. Ele não aparece mais nos resultados.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/moderacao/comentarios?ciclo=" + ciclo + "#r-" + respostaId;
    }

    @PostMapping("/comentarios/{respostaId}/exibir")
    public String exibir(@PathVariable Long respostaId, @RequestParam Long ciclo, RedirectAttributes ra) {
        moderacao.exibir(respostaId);
        ra.addFlashAttribute("sucesso", "Comentário voltou a aparecer nos resultados.");
        return "redirect:/moderacao/comentarios?ciclo=" + ciclo + "#r-" + respostaId;
    }

    private static CicloResumo escolher(List<CicloResumo> ciclos, Long cicloId) {
        return ciclos.stream().filter(c -> c.id().equals(cicloId)).findFirst()
                .orElse(ciclos.isEmpty() ? null : ciclos.get(0));
    }
}
