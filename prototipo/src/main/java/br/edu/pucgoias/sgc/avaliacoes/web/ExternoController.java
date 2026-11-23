package br.edu.pucgoias.sgc.avaliacoes.web;

import br.edu.pucgoias.sgc.avaliacoes.servico.AvaliacaoComPendenciasException;
import br.edu.pucgoias.sgc.avaliacoes.servico.FormularioAvaliacao;
import br.edu.pucgoias.sgc.avaliacoes.servico.LinkExternoIndisponivelException;
import br.edu.pucgoias.sgc.avaliacoes.servico.RespostaService;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * UC-03, extensão 1a (RF-34): avaliador externo pelo link individual, sem login ({@code /externo/**} é público no
 * filtro; o CSRF continua valendo). Mesmas regras de salvamento e envio, só com as perguntas da relação EXTERNO.
 * Link inválido, avaliação já enviada ou ciclo encerrado: mostra o motivo e não mostra o questionário.
 */
@Controller
@RequestMapping("/externo/{token}")
public class ExternoController {

    private final RespostaService respostas;

    public ExternoController(RespostaService respostas) {
        this.respostas = respostas;
    }

    @GetMapping
    public String formulario(@PathVariable String token, Model model) {
        return tela(token, respostas.abrirExterno(token), model);
    }

    @PostMapping("/resposta")
    @ResponseBody
    public ResponseEntity<Map<String, String>> salvarUma(@PathVariable String token, @RequestParam Long perguntaId,
                                                         @RequestParam(required = false) String nota,
                                                         @RequestParam(required = false) String texto) {
        try {
            OffsetDateTime quando = respostas.salvarExterno(token, perguntaId, RespostasDoFormulario.uma(nota, texto));
            return AvaliacoesController.salvo(quando);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/rascunho")
    public String salvarRascunho(@PathVariable String token, @RequestParam Map<String, String> parametros,
                                 RedirectAttributes ra) {
        try {
            respostas.salvarTodasExterno(token, RespostasDoFormulario.ler(parametros));
            ra.addFlashAttribute("sucesso", "Rascunho salvo. Você pode voltar pelo mesmo link e continuar.");
        } catch (LinkExternoIndisponivelException e) {
            throw e;
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/externo/" + token;
    }

    @PostMapping("/enviar")
    public String enviar(@PathVariable String token, @RequestParam Map<String, String> parametros,
                         @RequestParam(defaultValue = "false") boolean confirmado, Model model) {
        try {
            respostas.salvarTodasExterno(token, RespostasDoFormulario.ler(parametros));
            if (!confirmado) {
                FormularioAvaliacao f = respostas.abrirExterno(token);
                if (f.pendentes().isEmpty()) {
                    model.addAttribute("pedirConfirmacao", true);
                } else {
                    model.addAttribute("erroEnvio", new AvaliacaoComPendenciasException(f.pendentes()).getMessage());
                }
                return tela(token, f, model);
            }
            respostas.enviarExterno(token);
            return "redirect:/externo/" + token;
        } catch (LinkExternoIndisponivelException e) {
            throw e;
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erroEnvio", e.getMessage());
            return tela(token, respostas.abrirExterno(token), model);
        }
    }

    /** Link inválido, já enviado ou ciclo encerrado: só o motivo, sem o questionário (1a.3). */
    @ExceptionHandler(LinkExternoIndisponivelException.class)
    public String indisponivel(LinkExternoIndisponivelException e, Model model) {
        model.addAttribute("titulo", e.titulo());
        model.addAttribute("mensagem", e.getMessage());
        model.addAttribute("jaEnviada", e.jaEnviada());
        return "avaliacoes/externo-indisponivel";
    }

    private static String tela(String token, FormularioAvaliacao f, Model model) {
        model.addAttribute("f", f);
        model.addAttribute("acao", "/externo/" + token);
        return "avaliacoes/externo";
    }
}
