package br.edu.pucgoias.sgc.competencias.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDuplicadaException;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.competencias.servico.DadosCompetencia;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.competencias.servico.UsoQuestionario;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Só o RH acessa, conferido no servidor. */
@Controller
@PreAuthorize("hasRole('RH')")
@RequestMapping("/competencias")
public class QuestionariosController {

    private static final String EDITOR = "redirect:/competencias/questionarios/";

    private final QuestionarioService questionarios;
    private final CompetenciaService catalogo;
    private final UsuarioService usuarios;

    public QuestionariosController(QuestionarioService questionarios, CompetenciaService catalogo,
                                   UsuarioService usuarios) {
        this.questionarios = questionarios;
        this.catalogo = catalogo;
        this.usuarios = usuarios;
    }

    @GetMapping
    public String inicio() {
        return "redirect:/competencias/catalogo";
    }

    @GetMapping("/questionarios")
    public String lista(Model model) {
        model.addAttribute("questionarios", questionarios.listar());
        model.addAttribute("totalCompetencias", catalogo.totalNoCatalogo());
        return "competencias/questionarios";
    }

    @GetMapping("/questionarios/novo")
    public String novo(Model model) {
        prepararNovo(model, null, null, 5);
        return "competencias/novo-questionario";
    }

    @PostMapping("/questionarios/novo")
    public String criarEmBranco(@RequestParam(required = false) String nome,
                                @RequestParam(required = false) Long cargoId,
                                @RequestParam(defaultValue = "5") int escalaMax,
                                Model model, RedirectAttributes ra) {
        try {
            Long id = questionarios.criarEmBranco(nome, cargoId, escalaMax);
            ra.addFlashAttribute("sucesso", "Rascunho criado. As competências padrão do cargo já vêm marcadas; "
                    + "ajuste a seleção e adicione as perguntas.");
            return EDITOR + id;
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            prepararNovo(model, nome, cargoId, escalaMax);
            return "competencias/novo-questionario";
        }
    }

    @PostMapping("/questionarios/modelo/{modeloId}")
    public String usarModelo(@PathVariable Long modeloId, RedirectAttributes ra) {
        Long id = questionarios.criarDeModelo(modeloId);
        ra.addFlashAttribute("sucesso", "Rascunho criado a partir do modelo. Escolha o cargo e personalize o que precisar.");
        return EDITOR + id;
    }

    @PostMapping("/questionarios/copiar")
    public String copiar(@RequestParam(required = false) Long origemId, RedirectAttributes ra) {
        if (origemId == null) {
            ra.addFlashAttribute("erro", "Escolha o questionário que será copiado.");
            return "redirect:/competencias/questionarios/novo";
        }
        return duplicar(origemId, ra);
    }

    @PostMapping("/questionarios/{id}/duplicar")
    public String duplicar(@PathVariable Long id, RedirectAttributes ra) {
        Long novo = questionarios.duplicar(id);
        ra.addFlashAttribute("sucesso", "Cópia criada como rascunho, com todas as competências, perguntas e a escala "
                + "do original. Ajuste só o que mudou.");
        return EDITOR + novo;
    }

    @GetMapping("/questionarios/{id}")
    public String editor(@PathVariable Long id, Model model) {
        QuestionarioCompleto q = questionarios.detalhar(id);
        UsoQuestionario uso = questionarios.uso(id);
        if (q.modelo() || uso.bloqueado()) {
            // Modelo pronto e questionário em uso não são editados: a pré-visualização explica e oferece duplicar.
            return "redirect:/competencias/questionarios/" + id + "/previa";
        }
        Set<Long> selecionadas = new HashSet<>(q.competenciaIds());
        Map<Long, CompetenciaResumo> disponiveis = new LinkedHashMap<>();
        catalogo.competenciasAtivas().forEach(c -> disponiveis.put(c.id(), c));
        catalogo.competenciasPorIds(q.competenciaIds()).values().forEach(c -> disponiveis.putIfAbsent(c.id(), c));
        List<CompetenciaResumo> ordenadas = disponiveis.values().stream()
                .sorted(Comparator.comparing(CompetenciaResumo::tecnica).thenComparing(CompetenciaResumo::nome))
                .toList();

        // Blocos de perguntas: primeiro as competências do questionário, na ordem dele; depois as demais.
        List<SecaoEditor> secoes = new ArrayList<>();
        int indice = 0;
        for (var c : q.competencias()) {
            List<SecaoEditor.Linha> linhas = new ArrayList<>();
            for (PerguntaResumo p : c.perguntas()) {
                linhas.add(new SecaoEditor.Linha(indice++, p));
            }
            secoes.add(new SecaoEditor(disponiveis.get(c.id()), true, linhas));
        }
        for (CompetenciaResumo c : ordenadas) {
            if (!selecionadas.contains(c.id())) {
                secoes.add(new SecaoEditor(c, false, List.of()));
            }
        }

        model.addAttribute("q", q);
        model.addAttribute("cargos", usuarios.cargos());
        model.addAttribute("comportamentais", ordenadas.stream().filter(c -> !c.tecnica()).toList());
        model.addAttribute("tecnicas", ordenadas.stream().filter(CompetenciaResumo::tecnica).toList());
        model.addAttribute("selecionadas", selecionadas);
        model.addAttribute("padrao", catalogo.padraoDoCargo(q.cargoId()));
        model.addAttribute("padroesPorCargo", catalogo.padroesPorCargo());
        model.addAttribute("secoes", secoes);
        model.addAttribute("proximoIndice", indice);
        model.addAttribute("tipos", TipoAvaliador.values());
        model.addAttribute("niveis", niveisParaEditor(q));
        return "competencias/editor";
    }

    @PostMapping("/questionarios/{id}")
    public String salvar(@PathVariable Long id, @ModelAttribute QuestionarioForm form,
                         @RequestParam(defaultValue = "salvar") String acao, RedirectAttributes ra) {
        List<Long> ordemAtual = questionarios.detalhar(id).competenciaIds();
        questionarios.salvar(id, form.paraDados(ordemAtual));
        switch (acao) {
            case "previa" -> {
                return "redirect:/competencias/questionarios/" + id + "/previa";
            }
            case "nova-competencia" -> {
                cadastrarCompetencia(id, form, ra);
                return EDITOR + id + "#competencias";
            }
            default -> {
                ra.addFlashAttribute("sucesso", "Rascunho salvo.");
                return EDITOR + id;
            }
        }
    }

    @GetMapping("/questionarios/{id}/previa")
    public String previa(@PathVariable Long id, @RequestParam(required = false) TipoAvaliador como, Model model) {
        QuestionarioCompleto q = como == null ? questionarios.detalhar(id) : questionarios.detalharPara(id, como);
        model.addAttribute("q", q);
        model.addAttribute("como", como);
        model.addAttribute("tipos", TipoAvaliador.values());
        model.addAttribute("cargo", q.cargoId() == null ? null : usuarios.cargo(q.cargoId()).nome());
        model.addAttribute("pendencias", questionarios.pendencias(id));
        model.addAttribute("uso", questionarios.uso(id));
        return "competencias/previa";
    }

    @PostMapping("/questionarios/{id}/publicar")
    public String publicar(@PathVariable Long id, RedirectAttributes ra) {
        List<String> pendencias = questionarios.publicar(id);
        if (!pendencias.isEmpty()) {
            ra.addFlashAttribute("erro", "Ainda não dá para publicar. O rascunho continua salvo; veja o que falta abaixo.");
            ra.addFlashAttribute("pendencias", pendencias);
            return EDITOR + id;
        }
        ra.addFlashAttribute("sucesso", "Questionário “" + questionarios.resumo(id).nome()
                + "” publicado. Ele já pode ser escolhido na criação de ciclos.");
        return "redirect:/competencias/questionarios";
    }

    @PostMapping("/questionarios/{id}/rascunho")
    public String manterRascunho(@PathVariable Long id, RedirectAttributes ra) {
        ra.addFlashAttribute("sucesso", "O questionário “" + questionarios.resumo(id).nome()
                + "” ficou salvo como rascunho. Você pode publicá-lo depois.");
        return "redirect:/competencias/questionarios";
    }

    // ---- apoio ----

    private void cadastrarCompetencia(Long id, QuestionarioForm form, RedirectAttributes ra) {
        try {
            Long nova = catalogo.cadastrar(new DadosCompetencia(
                    form.getNovaNome(), form.getNovaDescricao(), form.getNovaTipo()));
            questionarios.incluirCompetencia(id, nova);
            ra.addFlashAttribute("sucesso", "Competência “" + catalogo.buscar(nova).nome()
                    + "” cadastrada no catálogo e marcada no questionário. Agora adicione as perguntas dela.");
        } catch (CompetenciaDuplicadaException e) {
            CompetenciaResumo existente = e.existente();
            if (existente.ativa()) {
                questionarios.incluirCompetencia(id, existente.id());
                ra.addFlashAttribute("aviso", "Já existe a competência “" + existente.nome()
                        + "” no catálogo. Em vez de cadastrar outra, marcamos a existente no questionário.");
            } else {
                ra.addFlashAttribute("aviso", e.getMessage());
            }
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            ra.addFlashAttribute("novaNome", form.getNovaNome());
            ra.addFlashAttribute("novaDescricao", form.getNovaDescricao());
            ra.addFlashAttribute("abrirNova", true);
        }
    }

    /** Descrições dos níveis 1 a 10 (as que não existem ficam em branco), para trocar de escala sem recarregar. */
    private static List<String> niveisParaEditor(QuestionarioCompleto q) {
        List<String> niveis = new ArrayList<>();
        for (int v = 1; v <= 10; v++) {
            final int valor = v;
            niveis.add(q.niveis().stream().filter(n -> n.valor() == valor).map(n -> n.descricao()).findFirst().orElse(""));
        }
        return niveis;
    }

    private void prepararNovo(Model model, String nome, Long cargoId, int escalaMax) {
        model.addAttribute("cargos", usuarios.cargos());
        model.addAttribute("modelos", questionarios.modelos());
        model.addAttribute("anteriores", questionarios.anteriores());
        model.addAttribute("nome", nome);
        model.addAttribute("cargoId", cargoId);
        model.addAttribute("escalaMax", escalaMax);
    }
}
