package br.edu.pucgoias.sgc.identidade.web;

import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** RH: consulta de pessoas, cargos e estrutura das equipes (cadastro mantido pelo RH, suposição da ERS). */
@Controller
public class PessoasController {

    private final UsuarioService usuarios;

    public PessoasController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping("/pessoas")
    public String pessoas(Model model) {
        var pessoas = usuarios.listarAtivos();
        model.addAttribute("pessoas", pessoas);
        model.addAttribute("porId", usuarios.buscarPorIds(pessoas.stream().map(p -> p.id()).toList()));
        model.addAttribute("cargos", usuarios.cargos());
        return "identidade/pessoas";
    }
}
