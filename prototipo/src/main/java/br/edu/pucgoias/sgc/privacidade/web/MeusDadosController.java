package br.edu.pucgoias.sgc.privacidade.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.servico.ConsentimentoService;
import br.edu.pucgoias.sgc.privacidade.servico.DadosPessoaisService;
import br.edu.pucgoias.sgc.privacidade.servico.SolicitacaoResumo;
import br.edu.pucgoias.sgc.privacidade.servico.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

/**
 * UC-05, passos 5 a 10 e extensões 6a, 7a, 7b e 7c: área "Meus dados", para todos os perfis. Tudo é da pessoa
 * logada; o id nunca vem da tela (RNF-03).
 */
@Controller
public class MeusDadosController {

    private final ConsentimentoService consentimento;
    private final SolicitacaoService solicitacoes;
    private final DadosPessoaisService dados;

    public MeusDadosController(ConsentimentoService consentimento, SolicitacaoService solicitacoes,
                               DadosPessoaisService dados) {
        this.consentimento = consentimento;
        this.solicitacoes = solicitacoes;
        this.dados = dados;
    }

    @GetMapping("/privacidade/meus-dados")
    public String meusDados(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SolicitacaoForm());
        }
        preparar(eu, model);
        return "privacidade/meus-dados";
    }

    @PostMapping("/privacidade/meus-dados/solicitacoes")
    public String solicitar(@AuthenticationPrincipal UsuarioAutenticado eu,
                            @Valid @ModelAttribute("form") SolicitacaoForm form, BindingResult erros,
                            Model model, RedirectAttributes redirect) {
        if (form.getTipo() == TipoSolicitacao.CORRECAO) {
            if (vazio(form.getDadoIncorreto())) {
                erros.rejectValue("dadoIncorreto", "obrigatorio", "Escolha qual dado está errado.");
            }
            if (vazio(form.getValorCorreto())) {
                erros.rejectValue("valorCorreto", "obrigatorio", "Escreva o valor correto.");
            }
        }
        if (form.getTipo() == TipoSolicitacao.EXCLUSAO && vazio(form.getDescricao())) {
            erros.rejectValue("descricao", "obrigatorio", "Conte, em poucas palavras, o motivo do pedido.");
        }
        if (erros.hasErrors()) {
            preparar(eu, model);
            return "privacidade/meus-dados";
        }
        // 7c: exclusão durante um ciclo em andamento pede confirmação antes de registrar.
        if (form.getTipo() == TipoSolicitacao.EXCLUSAO && !form.isConfirmado()
                && solicitacoes.exclusaoPrecisaDeConfirmacao(eu.id())) {
            model.addAttribute("pedirConfirmacao", true);
            preparar(eu, model);
            return "privacidade/meus-dados";
        }
        SolicitacaoResumo s;
        try {
            s = solicitacoes.registrar(eu, form.paraServico());
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            preparar(eu, model);
            return "privacidade/meus-dados";
        }
        if (s.tipo() == TipoSolicitacao.ACESSO) {
            redirect.addFlashAttribute("sucesso", "Pedido " + s.protocolo()
                    + " atendido. Esta é a cópia dos seus dados; você também recebeu a confirmação por e-mail.");
            return "redirect:/privacidade/meus-dados/copia";
        }
        redirect.addFlashAttribute("sucesso", "Solicitação registrada com o protocolo " + s.protocolo()
                + ". Enviamos a confirmação para o seu e-mail; o RH vai analisar o pedido.");
        return "redirect:/privacidade/meus-dados";
    }

    @GetMapping("/privacidade/meus-dados/solicitacoes/{id}")
    public String solicitacao(@AuthenticationPrincipal UsuarioAutenticado eu, @PathVariable Long id, Model model) {
        model.addAttribute("s", solicitacoes.buscarDoTitular(eu, id));
        model.addAttribute("modoRh", false);
        return "privacidade/solicitacao";
    }

    /** 7a: cópia dos dados na tela. */
    @GetMapping("/privacidade/meus-dados/copia")
    public String copia(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        model.addAttribute("copia", dados.gerarCopia(eu));
        model.addAttribute("notaAnonimato", DadosPessoaisService.NOTA_ANONIMATO);
        return "privacidade/copia-dados";
    }

    /** 7a: a mesma cópia em PDF. */
    @GetMapping("/privacidade/meus-dados/copia.pdf")
    public ResponseEntity<byte[]> copiaPdf(@AuthenticationPrincipal UsuarioAutenticado eu) {
        byte[] pdf = dados.gerarPdf(eu);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("meus-dados.pdf").build().toString())
                .body(pdf);
    }

    private void preparar(UsuarioAutenticado eu, Model model) {
        model.addAttribute("aceite", consentimento.ultimoAceite(eu.id()).orElse(null));
        model.addAttribute("termo", consentimento.termoVigente().orElse(null));
        model.addAttribute("dados", dados.dadosCadastrais(eu));
        model.addAttribute("solicitacoes", solicitacoes.doTitular(eu));
        model.addAttribute("tipos", TipoSolicitacao.values());
        model.addAttribute("dadosCorrigiveis", SolicitacaoService.DADOS_CORRIGIVEIS);
        model.addAttribute("canal", SolicitacaoService.CANAL_ENCARREGADO);
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
