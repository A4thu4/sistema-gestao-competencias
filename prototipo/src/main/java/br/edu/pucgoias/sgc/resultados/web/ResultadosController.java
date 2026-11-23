package br.edu.pucgoias.sgc.resultados.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.resultados.servico.ArquivoExportado;
import br.edu.pucgoias.sgc.resultados.servico.CicloDisponivel;
import br.edu.pucgoias.sgc.resultados.servico.ConsolidacaoService;
import br.edu.pucgoias.sgc.resultados.servico.RegrasDeExibicao;
import br.edu.pucgoias.sgc.resultados.servico.RelatorioEquipe;
import br.edu.pucgoias.sgc.resultados.servico.ResultadoIndisponivelException;
import br.edu.pucgoias.sgc.resultados.servico.ResultadoService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * UC-04 Consultar resultados. Todos os perfis entram; quem vê o quê é conferido no {@link ResultadoService}
 * (RNF-03). Liberação só RH; relatório de equipe só gestor e RH.
 *
 * <ul>
 *   <li>{@code GET /resultados}: ciclos com resultado para o perfil; com {@code ?ciclo=}, as pessoas do ciclo;</li>
 *   <li>{@code GET /resultados/{ciclo}/pessoas/{pessoa}}: painel individual (TL-04), {@code .../pdf} e
 *       {@code .../excel};</li>
 *   <li>{@code POST /resultados/{ciclo}/liberar}: liberação pelo RH (2a);</li>
 *   <li>{@code POST /resultados/{ciclo}/recalcular}: o RH refaz a consolidação (RNF-12);</li>
 *   <li>{@code GET /resultados/{ciclo}/equipe?gestor=} ou {@code ?area=}: relatório (3a), com PDF e Excel.</li>
 * </ul>
 */
@Controller
@RequestMapping("/resultados")
public class ResultadosController {

    private final ResultadoService resultados;
    private final ConsolidacaoService consolidacao;

    public ResultadosController(ResultadoService resultados, ConsolidacaoService consolidacao) {
        this.resultados = resultados;
        this.consolidacao = consolidacao;
    }

    @GetMapping
    public String lista(@AuthenticationPrincipal UsuarioAutenticado eu, @RequestParam(required = false) Long ciclo,
                        Model model) {
        List<CicloDisponivel> ciclos = resultados.ciclosDisponiveis(eu);
        CicloDisponivel escolhido = null;
        if (ciclo != null) {
            escolhido = ciclos.stream().filter(c -> c.ciclo().id().equals(ciclo)).findFirst().orElse(null);
        } else if (eu.ehRh() || eu.ehGestor()) {
            escolhido = ciclos.stream().filter(CicloDisponivel::consolidado).findFirst().orElse(null);
        }
        model.addAttribute("ciclos", ciclos);
        model.addAttribute("escolhido", escolhido);
        if (escolhido != null && escolhido.consolidado() && (eu.ehRh() || eu.ehGestor())) {
            model.addAttribute("pessoas", resultados.pessoasDoCiclo(eu, escolhido.ciclo().id()));
        }
        if (eu.ehRh()) {
            model.addAttribute("gestores", resultados.gestoresComEquipe());
            model.addAttribute("areas", resultados.areas());
        }
        model.addAttribute("minimo", RegrasDeExibicao.MINIMO_RESPOSTAS);
        model.addAttribute("ehColaborador", !eu.ehRh() && !eu.ehGestor());
        return "resultados/lista";
    }

    @GetMapping("/{cicloId}/pessoas/{pessoaId}")
    public String painel(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long cicloId,
                         @PathVariable Long pessoaId, Model model) {
        model.addAttribute("painel", resultados.painel(eu, cicloId, pessoaId));
        return "resultados/painel";
    }

    @GetMapping("/{cicloId}/pessoas/{pessoaId}/{formato:pdf|excel}")
    public ResponseEntity<byte[]> exportarPainel(@AuthenticationPrincipal UsuarioAutenticado eu,
                                                 @PathVariable Long cicloId, @PathVariable Long pessoaId,
                                                 @PathVariable String formato) {
        return download(resultados.exportarPainel(eu, cicloId, pessoaId, formato));
    }

    @PostMapping("/{cicloId}/liberar")
    @PreAuthorize("hasRole('RH')")
    public String liberar(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long cicloId,
                          RedirectAttributes ra) {
        try {
            int avisados = resultados.liberar(eu, cicloId);
            ra.addFlashAttribute("sucesso", "Resultado liberado. " + avisados
                    + (avisados == 1 ? " pessoa foi avisada" : " pessoas foram avisadas") + " por e-mail.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/resultados?ciclo=" + cicloId;
    }

    /** RNF-12: refaz o consolidado do ciclo encerrado a partir das respostas armazenadas. */
    @PostMapping("/{cicloId}/recalcular")
    @PreAuthorize("hasRole('RH')")
    public String recalcular(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long cicloId,
                             RedirectAttributes ra) {
        try {
            int medias = consolidacao.recalcular(eu, cicloId);
            ra.addFlashAttribute("sucesso", "Resultado recalculado a partir das respostas armazenadas: " + medias
                    + " médias. O mesmo conjunto de respostas gera sempre o mesmo resultado.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/resultados?ciclo=" + cicloId;
    }

    @GetMapping("/{cicloId}/equipe")
    @PreAuthorize("hasAnyRole('RH','GESTOR')")
    public String equipe(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long cicloId,
                         @RequestParam(required = false) Long gestor, @RequestParam(required = false) String area,
                         Model model) {
        RelatorioEquipe r = area != null && !area.isBlank() && gestor == null
                ? resultados.relatorioArea(eu, cicloId, area)
                : resultados.relatorioEquipe(eu, cicloId, gestor != null ? gestor : eu.id());
        model.addAttribute("relatorio", r);
        model.addAttribute("gestor", area != null && !area.isBlank() && gestor == null ? null
                : (gestor != null ? gestor : eu.id()));
        model.addAttribute("area", gestor == null ? area : null);
        if (eu.ehRh()) {
            model.addAttribute("gestores", resultados.gestoresComEquipe());
            model.addAttribute("areas", resultados.areas());
        }
        return "resultados/equipe";
    }

    @GetMapping("/{cicloId}/equipe/{formato:pdf|excel}")
    @PreAuthorize("hasAnyRole('RH','GESTOR')")
    public ResponseEntity<byte[]> exportarEquipe(@AuthenticationPrincipal UsuarioAutenticado eu,
                                                 @PathVariable Long cicloId, @PathVariable String formato,
                                                 @RequestParam(required = false) Long gestor,
                                                 @RequestParam(required = false) String area) {
        boolean porArea = area != null && !area.isBlank() && gestor == null;
        return download(resultados.exportarRelatorio(eu, cicloId, porArea ? null : (gestor != null ? gestor : eu.id()),
                porArea ? area : null, formato));
    }

    /** 2b e 2c: mensagem sem nenhum dado do resultado. */
    @ExceptionHandler(ResultadoIndisponivelException.class)
    public ModelAndView indisponivel(ResultadoIndisponivelException e) {
        ModelAndView mv = new ModelAndView("resultados/indisponivel");
        mv.addObject("titulo", e.getTitulo());
        mv.addObject("mensagem", e.getMessage());
        return mv;
    }

    private static ResponseEntity<byte[]> download(ArquivoExportado arquivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.tipoConteudo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(arquivo.nome(), StandardCharsets.UTF_8).build().toString())
                .body(arquivo.conteudo());
    }
}
