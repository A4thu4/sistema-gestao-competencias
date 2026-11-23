package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Cartões da página inicial: "Meus dados" para todos e, para o RH, solicitações em análise e aceites do termo. */
@Component
public class PainelPrivacidade implements ContribuicaoPainel {

    private final ConsentimentoService consentimento;
    private final SolicitacaoService solicitacoes;
    private final UsuarioService usuarios;

    public PainelPrivacidade(ConsentimentoService consentimento, SolicitacaoService solicitacoes,
                             UsuarioService usuarios) {
        this.consentimento = consentimento;
        this.solicitacoes = solicitacoes;
        this.usuarios = usuarios;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado eu) {
        List<CartaoPainel> cartoes = new ArrayList<>();
        if (eu.ehRh()) {
            long emAnalise = solicitacoes.emAnalise();
            cartoes.add(new CartaoPainel(40, "Solicitações LGPD em análise", String.valueOf(emAnalise),
                    emAnalise == 0 ? "Nenhum pedido de titular esperando resposta"
                            : "Pedidos de acesso, correção ou exclusão esperando resposta",
                    "/privacidade/solicitacoes?situacao=EM_ANALISE", emAnalise == 0 ? "success" : "warning"));
            consentimento.termoVigente().ifPresent(t -> cartoes.add(new CartaoPainel(91,
                    "Aceites do termo vigente",
                    consentimento.aceitesDoVigente() + " de " + usuarios.listarAtivos().size(),
                    "Pessoas que já aceitaram a versão " + t.versao(), "/privacidade/termos", "primary")));
        }
        String destaque = consentimento.ultimoAceite(eu.id()).map(a -> "Termo v" + a.versao()).orElse("—");
        String descricao = consentimento.ultimoAceite(eu.id())
                .map(a -> "Aceito em " + a.dataHora() + ". Veja seus dados ou faça um pedido.")
                .orElse("Veja seus dados ou faça um pedido.");
        cartoes.add(new CartaoPainel(90, "Meus dados", destaque, descricao, "/privacidade/meus-dados", "secondary"));
        return cartoes;
    }
}
