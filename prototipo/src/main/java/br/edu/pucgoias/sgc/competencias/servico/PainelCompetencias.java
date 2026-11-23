package br.edu.pucgoias.sgc.competencias.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.List;

/** Cartões da página inicial do RH: questionários publicados e rascunhos (UC-01). */
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
        long publicados = questionarios.contarPublicados();
        long rascunhos = questionarios.contarRascunhos();
        return List.of(
                new CartaoPainel(40, "Questionários publicados", String.valueOf(publicados),
                        "prontos para usar nos ciclos de avaliação", "/competencias", "primary"),
                new CartaoPainel(41, "Questionários em rascunho", String.valueOf(rascunhos),
                        rascunhos == 0 ? "nenhum aguardando publicação" : "aguardando revisão e publicação",
                        "/competencias", rascunhos == 0 ? "secondary" : "warning"));
    }
}
