package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Cartões da página inicial: resultado liberado (para quem foi avaliado), metas da equipe em andamento (gestor),
 * metas abertas do próprio plano e ciclos encerrados esperando liberação (RH).
 */
@Component
public class PainelResultados implements ContribuicaoPainel {

    private final ResultadoService resultados;
    private final PlanoService planos;

    public PainelResultados(ResultadoService resultados, PlanoService planos) {
        this.resultados = resultados;
        this.planos = planos;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado eu) {
        List<CartaoPainel> cartoes = new ArrayList<>();
        resultados.ultimoLiberado(eu.id()).ifPresent(c -> cartoes.add(new CartaoPainel(40, "Seu resultado",
                c.nome(), "Seu resultado do " + c.nome() + " foi liberado. Veja as médias e a sua evolução.",
                "/resultados/" + c.id() + "/pessoas/" + eu.id(), "success")));
        long minhasAbertas = planos.metasAbertasDe(eu.id());
        if (minhasAbertas > 0) {
            cartoes.add(new CartaoPainel(45, "Meu plano de desenvolvimento", String.valueOf(minhasAbertas),
                    minhasAbertas == 1 ? "meta aberta no seu plano" : "metas abertas no seu plano",
                    "/planos/" + eu.id(), "primary"));
        }
        if (eu.ehGestor()) {
            long emAndamento = planos.metasDaEquipeEmAndamento(eu.id());
            cartoes.add(new CartaoPainel(46, "Metas da equipe", String.valueOf(emAndamento),
                    emAndamento == 1 ? "meta em andamento nos planos da sua equipe"
                            : "metas em andamento nos planos da sua equipe", "/planos", "primary"));
        }
        if (eu.ehRh()) {
            List<CicloResumo> aguardando = resultados.aguardandoLiberacao();
            cartoes.add(new CartaoPainel(41, "Resultados para liberar", String.valueOf(aguardando.size()),
                    aguardando.isEmpty() ? "nenhum ciclo encerrado esperando liberação"
                            : "ciclo(s) encerrado(s) sem liberação: " + String.join(", ",
                            aguardando.stream().map(CicloResumo::nome).toList()),
                    "/resultados", aguardando.isEmpty() ? "secondary" : "warning"));
        }
        return cartoes;
    }
}
