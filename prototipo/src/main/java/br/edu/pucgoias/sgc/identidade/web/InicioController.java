package br.edu.pucgoias.sgc.identidade.web;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

/** Página inicial com as funcionalidades do perfil (UC-10, passo final). */
@Controller
public class InicioController {

    private final ObjectProvider<ContribuicaoPainel> contribuicoes;

    public InicioController(ObjectProvider<ContribuicaoPainel> contribuicoes) {
        this.contribuicoes = contribuicoes;
    }

    @GetMapping("/")
    public String inicio(@AuthenticationPrincipal UsuarioAutenticado eu, Model model) {
        List<CartaoPainel> cartoes = contribuicoes.orderedStream()
                .flatMap(c -> c.cartoes(eu).stream())
                .sorted(Comparator.comparingInt(CartaoPainel::ordem))
                .toList();
        model.addAttribute("cartoes", cartoes);
        return "identidade/inicio";
    }
}
