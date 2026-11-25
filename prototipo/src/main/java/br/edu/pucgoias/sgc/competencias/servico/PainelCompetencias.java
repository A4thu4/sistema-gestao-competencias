package br.edu.pucgoias.sgc.competencias.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.List;

/** Cartão do RH na página inicial: questionários em rascunho. */
@Component
public class PainelCompetencias implements ContribuicaoPainel {

    private final QuestionarioService questionarios;

    public PainelCompetencias(QuestionarioService questionarios) {
        this.questionarios = questionarios;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado usuario) {
        if (!usuario.ehRh()) {
            return List.of();
        }
        long rascunhos = questionarios.contarRascunhos();
        if (rascunhos == 0) {
            return List.of();
        }
        return List.of(new CartaoPainel(41, "Questionários em rascunho", String.valueOf(rascunhos),
                rascunhos == 1 ? "falta publicar" : "faltam publicar", "/competencias", "warning"));
    }
}
