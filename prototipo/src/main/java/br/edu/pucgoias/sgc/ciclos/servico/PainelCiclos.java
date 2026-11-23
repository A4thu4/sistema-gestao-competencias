package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Cartões da página inicial: RH vê os ciclos abertos e o % respondido; gestor vê os pendentes da equipe. */
@Component
public class PainelCiclos implements ContribuicaoPainel {

    private final MontagemCicloService montagem;
    private final AndamentoService andamento;

    public PainelCiclos(MontagemCicloService montagem, AndamentoService andamento) {
        this.montagem = montagem;
        this.andamento = andamento;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado usuario) {
        List<CartaoPainel> cartoes = new ArrayList<>();
        if (usuario.ehRh()) {
            int ordem = 10;
            List<CicloNaLista> todos = montagem.listar();
            List<CicloNaLista> abertos = todos.stream().filter(c -> c.ciclo().aberto()).toList();
            for (CicloNaLista c : abertos) {
                cartoes.add(new CartaoPainel(ordem++, c.ciclo().nome() + " aberto",
                        c.percentualRespondido() + "%",
                        c.enviadas() + " de " + c.designacoes() + " avaliações enviadas · encerra em "
                                + Datas.data(c.ciclo().dataEncerramento()),
                        "/ciclos/andamento?ciclo=" + c.ciclo().id(), "primary"));
            }
            if (abertos.isEmpty()) {
                long agendados = todos.stream().filter(c -> c.ciclo().agendado()).count();
                cartoes.add(new CartaoPainel(10, "Ciclos abertos", "0",
                        agendados == 0 ? "nenhum ciclo em andamento" : agendados + " agendado(s) para abrir",
                        "/ciclos", "secondary"));
            }
        } else if (usuario.ehGestor()) {
            long pendentes = andamento.pendentesDaEquipe(usuario);
            cartoes.add(new CartaoPainel(15, "Pendentes da equipe", String.valueOf(pendentes),
                    pendentes == 0 ? "sua equipe está em dia com as avaliações"
                            : pendentes == 1 ? "avaliação ainda não enviada" : "avaliações ainda não enviadas",
                    "/ciclos/andamento", pendentes == 0 ? "success" : "warning"));
        }
        return cartoes;
    }
}
