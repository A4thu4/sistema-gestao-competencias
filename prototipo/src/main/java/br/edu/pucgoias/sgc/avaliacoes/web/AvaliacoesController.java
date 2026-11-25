package br.edu.pucgoias.sgc.avaliacoes.web;

import br.edu.pucgoias.sgc.avaliacoes.servico.AvaliacaoComPendenciasException;
import br.edu.pucgoias.sgc.avaliacoes.servico.FormularioAvaliacao;
import br.edu.pucgoias.sgc.avaliacoes.servico.RespostaService;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/** Avaliador interno (qualquer perfil logado). O serviço confere que a designação é do usuário. */
@Controller
@RequestMapping("/avaliacoes")
public class AvaliacoesController {

    static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final RespostaService respostas;

    public AvaliacoesController(RespostaService respostas) {
        this.respostas = respostas;
    }

    @GetMapping
    public String lista(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        model.addAttribute("grupos", respostas.minhas(eu));
        return "avaliacoes/lista";
    }

    @GetMapping("/{id}")
    public String formulario(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long id, Model model) {
        return tela(respostas.abrir(eu, id), model);
    }

    @PostMapping("/{id}/resposta")
    @ResponseBody
    public ResponseEntity<Map<String, String>> salvarUma(@AuthenticationPrincipal UsuarioAutenticado eu,
                                                         @PathVariable Long id, @RequestParam Long perguntaId,
                                                         @RequestParam(required = false) String nota,
                                                         @RequestParam(required = false) String texto) {
        try {
            OffsetDateTime quando = respostas.salvar(eu, id, perguntaId, RespostasDoFormulario.uma(nota, texto));
            return salvo(quando);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{id}/rascunho")
    public String salvarRascunho(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long id,
                                 @RequestParam Map<String, String> parametros, RedirectAttributes ra) {
        try {
            respostas.salvarTodas(eu, id, RespostasDoFormulario.ler(parametros));
            ra.addFlashAttribute("sucesso", "Rascunho salvo. Você pode continuar depois de onde parou.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/avaliacoes/" + id;
    }

    @PostMapping("/{id}/enviar")
    public String enviar(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long id,
                         @RequestParam Map<String, String> parametros,
                         @RequestParam(defaultValue = "false") boolean confirmado, Model model, RedirectAttributes ra) {
        try {
            respostas.salvarTodas(eu, id, RespostasDoFormulario.ler(parametros));
            if (!confirmado) {
                FormularioAvaliacao f = respostas.abrir(eu, id);
                if (f.pendentes().isEmpty()) {
                    model.addAttribute("pedirConfirmacao", true);
                } else {
                    model.addAttribute("erroEnvio", new AvaliacaoComPendenciasException(f.pendentes()).getMessage());
                }
                return tela(f, model);
            }
            String nome = respostas.abrir(eu, id).avaliado().nome();
            respostas.enviarAvaliacao(eu, id);
            ra.addFlashAttribute("sucesso", "Avaliação de " + nome + " enviada. Obrigado!");
            return "redirect:/avaliacoes";
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erroEnvio", e.getMessage());
            return tela(respostas.abrir(eu, id), model);
        }
    }

    private static String tela(FormularioAvaliacao f, Model model) {
        model.addAttribute("f", f);
        model.addAttribute("acao", "/avaliacoes/" + f.designacao().id());
        return "avaliacoes/responder";
    }

    static ResponseEntity<Map<String, String>> salvo(OffsetDateTime quando) {
        return ResponseEntity.ok(Map.of("salvoEm", quando.atZoneSameInstant(ZONA).format(HORA)));
    }
}
