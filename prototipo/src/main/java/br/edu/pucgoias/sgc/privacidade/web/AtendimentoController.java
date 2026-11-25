package br.edu.pucgoias.sgc.privacidade.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.privacidade.dominio.SituacaoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.servico.ConsentimentoService;
import br.edu.pucgoias.sgc.privacidade.servico.SolicitacaoResumo;
import br.edu.pucgoias.sgc.privacidade.servico.SolicitacaoService;
import br.edu.pucgoias.sgc.privacidade.servico.TermoResumo;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/** Telas do RH: atendimento das solicitações dos titulares e publicação de nova versão do termo. */
@Controller
@PreAuthorize("hasRole('RH')")
public class AtendimentoController {

    private final SolicitacaoService solicitacoes;
    private final ConsentimentoService consentimento;
    public AtendimentoController(SolicitacaoService solicitacoes, ConsentimentoService consentimento) {
        this.solicitacoes = solicitacoes;
        this.consentimento = consentimento;
    }

    @GetMapping("/privacidade/solicitacoes")
    public String lista(@AuthenticationPrincipal UsuarioAutenticado eu,
                        @RequestParam(required = false) SituacaoSolicitacao situacao, Model model) {
        List<SolicitacaoResumo> lista = solicitacoes.listar(eu, situacao);
        model.addAttribute("lista", lista);
        model.addAttribute("pessoas", pessoas(lista.stream().map(SolicitacaoResumo::usuarioId)));
        model.addAttribute("situacao", situacao);
        model.addAttribute("situacoes", SituacaoSolicitacao.values());
        model.addAttribute("emAnalise", solicitacoes.emAnalise());
        return "privacidade/solicitacoes";
    }

    @GetMapping("/privacidade/solicitacoes/{id}")
    public String detalhe(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long id, Model model) {
        SolicitacaoResumo s = solicitacoes.buscarParaAtendimento(eu, id);
        model.addAttribute("s", s);
        model.addAttribute("modoRh", true);
        model.addAttribute("pessoas", pessoas(Stream.of(s.usuarioId(), s.concluidaPor())));
        model.addAttribute("decisoes", List.of(SituacaoSolicitacao.ATENDIDA, SituacaoSolicitacao.ATENDIDA_PARCIAL,
                SituacaoSolicitacao.NEGADA));
        if (!model.containsAttribute("justificativa")) {
            model.addAttribute("justificativa", switch (s.tipo()) {
                case EXCLUSAO -> SolicitacaoService.justificativaPadraoExclusao();
                case CORRECAO -> "Nome".equalsIgnoreCase(s.dadoIncorreto())
                        ? "Corrigimos o seu nome no cadastro para \"" + s.valorCorreto() + "\"." : "";
                default -> "";
            });
        }
        return "privacidade/solicitacao";
    }

    @PostMapping("/privacidade/solicitacoes/{id}/concluir")
    public String concluir(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long id,
                           @RequestParam(required = false) SituacaoSolicitacao decisao,
                           @RequestParam(required = false) String justificativa, RedirectAttributes redirect) {
        try {
            SolicitacaoResumo s = solicitacoes.concluir(eu, id, decisao, justificativa);
            redirect.addFlashAttribute("sucesso", "Solicitação " + s.protocolo() + " concluída como \""
                    + s.situacao().getRotulo() + "\". O titular foi avisado por e-mail.");
            return "redirect:/privacidade/solicitacoes";
        } catch (RegraDeNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            redirect.addFlashAttribute("justificativa", justificativa);
            return "redirect:/privacidade/solicitacoes/" + id;
        }
    }

    @GetMapping("/privacidade/termos")
    public String termos(Model model) {
        if (!model.containsAttribute("form")) {
            NovaVersaoForm form = new NovaVersaoForm();
            consentimento.termoVigente().ifPresent(t -> form.setTexto(t.texto()));
            model.addAttribute("form", form);
        }
        prepararTermos(model);
        return "privacidade/termos";
    }

    @PostMapping("/privacidade/termos")
    public String publicar(@AuthenticationPrincipal UsuarioAutenticado eu,
                           @Valid @ModelAttribute("form") NovaVersaoForm form, BindingResult erros,
                           Model model, RedirectAttributes redirect) {
        if (consentimento.termoVigente().isPresent()
                && (form.getResumoMudancas() == null || form.getResumoMudancas().isBlank())) {
            erros.rejectValue("resumoMudancas", "obrigatorio",
                    "Conte o que mudou: as pessoas vão ver este resumo antes de aceitar de novo.");
        }
        if (!erros.hasErrors()) {
            try {
                TermoResumo t = consentimento.publicar(eu, form.getVersao(), form.getTexto(), form.getResumoMudancas());
                redirect.addFlashAttribute("sucesso", "Versão " + t.versao()
                        + " publicada. Todas as pessoas vão ver o resumo das mudanças e aceitar de novo no próximo acesso.");
                return "redirect:/privacidade/termos";
            } catch (RegraDeNegocioException e) {
                erros.rejectValue("versao", "regra", e.getMessage());
            }
        }
        prepararTermos(model);
        return "privacidade/termos";
    }

    private void prepararTermos(Model model) {
        model.addAttribute("versoes", consentimento.versoes());
        model.addAttribute("vigente", consentimento.termoVigente().orElse(null));
        model.addAttribute("aceitesVigente", consentimento.aceitesDoVigente());
        model.addAttribute("ativos", consentimento.pessoasAtivas());
    }

    private Map<Long, PessoaResumo> pessoas(Stream<Long> ids) {
        List<Long> lista = new ArrayList<>();
        ids.filter(java.util.Objects::nonNull).distinct().forEach(lista::add);
        return solicitacoes.pessoas(lista);
    }
}
