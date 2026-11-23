package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;

import java.util.List;

/** UC-03, extensão 8a: há perguntas obrigatórias sem resposta; o envio é barrado (RF-15). */
public class AvaliacaoComPendenciasException extends RegraDeNegocioException {

    private final List<PerguntaResumo> pendentes;

    public AvaliacaoComPendenciasException(List<PerguntaResumo> pendentes) {
        super(pendentes.size() == 1
                ? "Falta responder 1 pergunta obrigatória. Ela está marcada em vermelho."
                : "Faltam responder " + pendentes.size() + " perguntas obrigatórias. Elas estão marcadas em vermelho.");
        this.pendentes = List.copyOf(pendentes);
    }

    public List<PerguntaResumo> pendentes() {
        return pendentes;
    }
}
