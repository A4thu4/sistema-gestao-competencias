package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.List;

/** Cartão "Avaliações pendentes" na página inicial de quem tem avaliações a responder (UC-03). */
@Component
public class PainelAvaliacoes implements ContribuicaoPainel {

    private final RespostaService respostas;

    public PainelAvaliacoes(RespostaService respostas) {
        this.respostas = respostas;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado eu) {
        long pendentes = respostas.pendentes(eu);
        if (pendentes == 0) {
            return List.of();
        }
        return List.of(new CartaoPainel(5, "Avaliações pendentes", String.valueOf(pendentes),
                pendentes == 1 ? "avaliação para responder no ciclo aberto"
                        : "avaliações para responder no ciclo aberto",
                "/avaliacoes", "warning"));
    }
}
