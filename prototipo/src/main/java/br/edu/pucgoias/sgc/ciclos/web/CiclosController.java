package br.edu.pucgoias.sgc.ciclos.web;

import br.edu.pucgoias.sgc.ciclos.servico.CicloComPendenciasException;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.ConviteService;
import br.edu.pucgoias.sgc.ciclos.servico.Datas;
import br.edu.pucgoias.sgc.ciclos.servico.DadosInvalidosException;
import br.edu.pucgoias.sgc.ciclos.servico.MontagemCiclo;
import br.edu.pucgoias.sgc.ciclos.servico.MontagemCicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Criação e manutenção de ciclos, só para o RH (conferido no servidor), com os botões "Abrir agora" e
 * "Encerrar agora" da demonstração.
 */
@Controller
@PreAuthorize("hasRole('RH')")
@RequestMapping("/ciclos")
public class CiclosController {

    private static final String VOLTOU_A_RASCUNHO =
            "O ciclo estava agendado e voltou a rascunho por causa da alteração. Confira o resumo e agende de novo.";

    private final MontagemCicloService montagem;
    private final CicloService ciclos;
    private final UsuarioService usuarios;

    public CiclosController(MontagemCicloService montagem, CicloService ciclos, UsuarioService usuarios) {
        this.montagem = montagem;
        this.ciclos = ciclos;
        this.usuarios = usuarios;
    }

    // ==== Passo 2 ====

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("ciclos", montagem.listar());
        return "ciclos/lista";
    }

    // ==== Passos 4 a 6 ====

    @GetMapping("/novo")
    public String novo(Model model) {
        prepararDados(model, CicloForm.novo(), null, Map.of());
        return "ciclos/dados";
    }

    @PostMapping("/novo")
    public String criar(@ModelAttribute("form") CicloForm form, BindingResult binding, Model model,
                        RedirectAttributes ra) {
        try {
            Long id = montagem.criar(form.paraDados());
            ra.addFlashAttribute("sucesso", "Rascunho do ciclo criado. Agora escolha quem será avaliado.");
            return "redirect:/ciclos/" + id + "/avaliados";
        } catch (DadosInvalidosException e) {
            model.addAttribute("erro", "Confira os campos destacados.");
            prepararDados(model, form, null, e.erros());
            return "ciclos/dados";
        }
    }

    @GetMapping("/{id}/dados")
    public String dados(@PathVariable Long id, Model model) {
        CicloResumo c = ciclos.ciclo(id);
        prepararDados(model, CicloForm.de(c), c, Map.of());
        return "ciclos/dados";
    }

    @PostMapping("/{id}/dados")
    public String alterarDados(@PathVariable Long id, @ModelAttribute("form") CicloForm form, BindingResult binding,
                               Model model, RedirectAttributes ra) {
        CicloResumo c = ciclos.ciclo(id);
        try {
            if (c.aberto()) {
                montagem.alterarEncerramento(id, form.getEncerramento());
                ra.addFlashAttribute("sucesso", "Data de encerramento alterada para "
                        + Datas.data(form.getEncerramento()) + ".");
                return "redirect:/ciclos/" + id;
            }
            boolean voltou = montagem.alterarDados(id, form.paraDados());
            ra.addFlashAttribute("sucesso", "Dados do ciclo salvos.");
            avisarSeVoltou(ra, voltou);
            return "redirect:/ciclos/" + id + "/avaliados";
        } catch (DadosInvalidosException e) {
            model.addAttribute("erro", "Confira os campos destacados.");
            if (c.aberto()) {
                CicloForm original = CicloForm.de(c);
                original.setEncerramento(form.getEncerramento());
                form = original;
            }
            prepararDados(model, form, c, e.erros());
            return "ciclos/dados";
        }
    }

    private void prepararDados(Model model, CicloForm form, CicloResumo ciclo, Map<String, String> erros) {
        model.addAttribute("form", form);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("erros", erros);
        model.addAttribute("tiposDisponiveis", List.of(TipoAvaliador.CHEFE, TipoAvaliador.AUTO, TipoAvaliador.PAR,
                TipoAvaliador.SUBORDINADO));
        model.addAttribute("hoje", Datas.hoje());
    }

    // ==== Passos 7 e 8 ====

    @GetMapping("/{id}/avaliados")
    public String avaliados(@PathVariable Long id, Model model) {
        MontagemCiclo m = montagem.montagem(id);
        if (!m.ciclo().editavel()) {
            return "redirect:/ciclos/" + id;
        }
        List<PessoaResumo> pessoas = usuarios.listarAtivos();
        model.addAttribute("m", m);
        model.addAttribute("porArea", pessoas.stream().collect(Collectors.groupingBy(PessoaResumo::area,
                TreeMap::new, Collectors.toList())));
        model.addAttribute("cargos", usuarios.cargos());
        model.addAttribute("selecionados", m.avaliados().stream().map(a -> a.id()).collect(Collectors.toSet()));
        return "ciclos/avaliados";
    }

    @PostMapping("/{id}/avaliados")
    public String salvarAvaliados(@PathVariable Long id,
                                  @RequestParam(name = "pessoas", required = false) List<Long> pessoas,
                                  @RequestParam(name = "cargos", required = false) List<Long> cargos,
                                  @RequestParam(name = "areas", required = false) List<String> areas,
                                  @RequestParam MultiValueMap<String, String> parametros,
                                  @RequestParam(defaultValue = "avaliadores") String proximo,
                                  RedirectAttributes ra) {
        Map<Long, Long> escolha = new HashMap<>();
        parametros.forEach((nome, valores) -> {
            if (nome.startsWith("questionario-") && !valores.isEmpty() && !valores.get(0).isBlank()) {
                try {
                    escolha.put(Long.valueOf(nome.substring("questionario-".length())), Long.valueOf(valores.get(0)));
                } catch (NumberFormatException ignorado) {
                    // parâmetro inválido: mantém a escolha atual
                }
            }
        });
        boolean voltou = montagem.salvarAvaliados(id, pessoas, cargos, areas, escolha);
        MontagemCiclo m = montagem.montagem(id);
        avisarSeVoltou(ra, voltou);
        if (!m.verificacao().semQuestionario().isEmpty() || !m.verificacao().semPerguntas().isEmpty()) {
            ra.addFlashAttribute("erro", "Há cargos sem questionário pronto para este ciclo. Veja as opções abaixo.");
            return "redirect:/ciclos/" + id + "/avaliados";
        }
        ra.addFlashAttribute("sucesso", m.avaliados().size() + " pessoa(s) no ciclo. Os avaliadores foram sugeridos "
                + "pela estrutura das equipes; revise e ajuste.");
        return "ficar".equals(proximo) ? "redirect:/ciclos/" + id + "/avaliados" : "redirect:/ciclos/" + id + "/avaliadores";
    }

    @PostMapping("/{id}/avaliados/remover-cargo")
    public String removerCargo(@PathVariable Long id, @RequestParam Long cargoId, RedirectAttributes ra) {
        avisarSeVoltou(ra, montagem.removerCargo(id, cargoId));
        ra.addFlashAttribute("sucesso", "As pessoas do cargo " + usuarios.cargo(cargoId).nome() + " saíram do ciclo.");
        return "redirect:/ciclos/" + id + "/avaliados";
    }

    @PostMapping("/{id}/tipos/retirar")
    public String retirarTipo(@PathVariable Long id, @RequestParam TipoAvaliador tipo, RedirectAttributes ra) {
        try {
            avisarSeVoltou(ra, montagem.retirarTipo(id, tipo));
            ra.addFlashAttribute("sucesso", "O tipo " + tipo.rotulo() + " foi retirado do ciclo.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ciclos/" + id + "/avaliados";
    }

    // ==== Passo 9 ====

    @GetMapping("/{id}/avaliadores")
    public String avaliadores(@PathVariable Long id, Model model) {
        MontagemCiclo m = montagem.montagem(id);
        if (m.ciclo().encerrado()) {
            return "redirect:/ciclos/" + id;
        }
        model.addAttribute("m", m);
        model.addAttribute("pessoas", usuarios.listarAtivos().stream()
                .sorted(Comparator.comparing(PessoaResumo::nome)).toList());
        return "ciclos/avaliadores";
    }

    @PostMapping("/{id}/avaliadores/incluir")
    public String incluirAvaliador(@PathVariable Long id, @RequestParam Long avaliadoId,
                                   @RequestParam TipoAvaliador tipo,
                                   @RequestParam(required = false) Long avaliadorId, RedirectAttributes ra) {
        try {
            boolean voltou = montagem.incluirAvaliador(id, avaliadoId, tipo, avaliadorId);
            avisarSeVoltou(ra, voltou);
            ra.addFlashAttribute("sucesso", ciclos.ciclo(id).aberto()
                    ? "Avaliador incluído. O convite foi enviado agora por e-mail." : "Avaliador incluído.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return voltarAoAvaliado(id, avaliadoId);
    }

    @PostMapping("/{id}/avaliadores/externo")
    public String incluirExterno(@PathVariable Long id, @RequestParam Long avaliadoId,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String email, RedirectAttributes ra) {
        try {
            boolean voltou = montagem.incluirExterno(id, avaliadoId, nome, email);
            avisarSeVoltou(ra, voltou);
            ra.addFlashAttribute("sucesso", ciclos.ciclo(id).aberto()
                    ? "Avaliador externo incluído. O link individual foi enviado agora por e-mail."
                    : "Avaliador externo incluído. O link individual será enviado por e-mail na abertura do ciclo.");
        } catch (DadosInvalidosException e) {
            ra.addFlashAttribute("erro", String.join(" ", e.erros().values()));
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return voltarAoAvaliado(id, avaliadoId);
    }

    @PostMapping("/{id}/avaliadores/{designacaoId}/remover")
    public String removerAvaliador(@PathVariable Long id, @PathVariable Long designacaoId,
                                   @RequestParam(required = false) Long avaliadoId, RedirectAttributes ra) {
        try {
            avisarSeVoltou(ra, montagem.removerDesignacao(id, designacaoId));
            ra.addFlashAttribute("sucesso", "Avaliador removido.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return voltarAoAvaliado(id, avaliadoId);
    }

    @PostMapping("/{id}/avaliadores/sugerir")
    public String refazerSugestao(@PathVariable Long id, @RequestParam(required = false) Long avaliadoId,
                                  RedirectAttributes ra) {
        avisarSeVoltou(ra, montagem.refazerSugestao(id, avaliadoId));
        ra.addFlashAttribute("sucesso", avaliadoId == null
                ? "Avaliadores sugeridos de novo para todas as pessoas, pela estrutura das equipes."
                : "Avaliadores sugeridos de novo para esta pessoa.");
        return voltarAoAvaliado(id, avaliadoId);
    }

    private static String voltarAoAvaliado(Long id, Long avaliadoId) {
        return "redirect:/ciclos/" + id + "/avaliadores" + (avaliadoId == null ? "" : "#avaliado-" + avaliadoId);
    }

    // ==== Passos 10 a 14 ====

    @GetMapping("/{id}")
    public String resumo(@PathVariable Long id, Model model) {
        model.addAttribute("m", montagem.montagem(id));
        model.addAttribute("hoje", Datas.hoje());
        return "ciclos/resumo";
    }

    @PostMapping("/{id}/agendar")
    public String agendar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            MontagemCicloService.ResultadoAgendamento r = montagem.agendar(id);
            if (r.abriu()) {
                ra.addFlashAttribute("sucesso", "Ciclo agendado e aberto agora, porque a abertura é hoje. "
                        + r.envio().enviados() + " convite(s) enviados por e-mail.");
                if (r.envio().falhas() > 0) {
                    ra.addFlashAttribute("aviso", r.envio().falhas() + " convite(s) não puderam ser enviados. "
                            + "O sistema tenta de novo; você também pode reenviar abaixo.");
                }
            } else {
                ra.addFlashAttribute("sucesso", "Ciclo agendado. Ele abre sozinho em " + Datas.data(r.abertura())
                        + " e os convites saem nesse dia.");
            }
        } catch (CicloComPendenciasException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ciclos/" + id;
    }

    @PostMapping("/{id}/rascunho")
    public String salvarRascunho(@PathVariable Long id, RedirectAttributes ra) {
        boolean desagendou = montagem.salvarRascunho(id);
        ra.addFlashAttribute("sucesso", desagendou
                ? "O ciclo voltou a rascunho e não abrirá sozinho. Nenhum convite foi enviado."
                : "Rascunho salvo. Nenhum convite foi enviado; agende quando estiver pronto.");
        return "redirect:/ciclos";
    }

    @PostMapping("/{id}/abrir-agora")
    public String abrirAgora(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ConviteService.ResultadoEnvio r = ciclos.abrir(id, true);
            ra.addFlashAttribute("sucesso", "Ciclo aberto. " + r.enviados() + " convite(s) enviados por e-mail.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ciclos/" + id;
    }

    @PostMapping("/{id}/encerrar-agora")
    public String encerrarAgora(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ciclos.encerrar(id, true);
            ra.addFlashAttribute("sucesso", "Ciclo encerrado. Novas respostas não são mais aceitas e os resultados "
                    + "foram consolidados.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ciclos/" + id;
    }

    @PostMapping("/{id}/reenviar-convites")
    public String reenviarConvites(@PathVariable Long id, RedirectAttributes ra) {
        ConviteService.ResultadoEnvio r = ciclos.reenviarConvites(id);
        if (r.falhas() > 0) {
            ra.addFlashAttribute("aviso", r.enviados() + " convite(s) reenviados; " + r.falhas()
                    + " ainda falharam.");
        } else {
            ra.addFlashAttribute("sucesso", r.enviados() == 0 ? "Não há convites pendentes."
                    : r.enviados() + " convite(s) reenviados.");
        }
        return "redirect:/ciclos/" + id;
    }

    private static void avisarSeVoltou(RedirectAttributes ra, boolean voltou) {
        if (voltou) {
            ra.addFlashAttribute("aviso", VOLTOU_A_RASCUNHO);
        }
    }
}
