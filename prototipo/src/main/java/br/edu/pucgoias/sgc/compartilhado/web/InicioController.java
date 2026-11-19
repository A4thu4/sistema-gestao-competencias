package br.edu.pucgoias.sgc.compartilhado.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página inicial provisória, para conferir que o projeto sobe.
 * Será substituída pelo painel de cada perfil na issue #55.
 */
@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
