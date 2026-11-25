package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.List;

/** Cartão do RH na página inicial: pedidos de titular em análise. */
@Component
public class PainelPrivacidade implements ContribuicaoPainel {

    private final SolicitacaoService solicitacoes;

    public PainelPrivacidade(SolicitacaoService solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado eu) {
        if (!eu.ehRh()) {
            return List.of();
        }
        long emAnalise = solicitacoes.emAnalise();
        return List.of(new CartaoPainel(40, "LGPD em análise", String.valueOf(emAnalise),
                emAnalise == 0 ? "nenhum pedido aberto" : emAnalise == 1 ? "pedido de titular" : "pedidos de titular",
                "/privacidade/solicitacoes?situacao=EM_ANALISE", emAnalise == 0 ? "secondary" : "warning"));
    }
}
