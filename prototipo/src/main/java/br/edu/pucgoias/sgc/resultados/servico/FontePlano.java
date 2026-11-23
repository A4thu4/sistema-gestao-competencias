package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.privacidade.servico.FonteDadosPessoais;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Metas do plano de desenvolvimento e as atualizações na cópia dos dados pessoais (UC-05, 7a). */
@Component
@Order(40)
public class FontePlano implements FonteDadosPessoais {

    private final PlanoService planos;

    public FontePlano(PlanoService planos) {
        this.planos = planos;
    }

    @Override
    public String titulo() {
        return "Plano de desenvolvimento";
    }

    @Override
    public List<String> itens(Long usuarioId) {
        List<String> itens = new ArrayList<>();
        for (MetaResumo m : planos.metasDe(usuarioId)) {
            itens.add("Meta em " + m.competencia() + ": " + m.descricao() + " · ações: " + m.acoes() + " · prazo "
                    + m.prazoTexto() + " · " + m.situacaoRotulo() + " · criada em " + m.criadaEmTexto());
            for (AtualizacaoMeta a : m.historico()) {
                itens.add("Atualização da meta em " + m.competencia() + " (" + a.quando() + ", " + a.autor() + "): "
                        + a.situacaoRotulo() + " · " + a.progresso());
            }
        }
        return itens;
    }
}
