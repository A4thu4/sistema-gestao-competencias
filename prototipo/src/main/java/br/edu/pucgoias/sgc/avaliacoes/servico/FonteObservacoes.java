package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.privacidade.servico.FonteDadosPessoais;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Observações do gestor na cópia dos dados pessoais do titular (UC-05, 7a). O colaborador não vê essas anotações na
 * tela do UC-08, mas elas são dados pessoais dele e entram na cópia da LGPD: data do fato, tipo e descrição.
 */
@Component
@Order(20)
public class FonteObservacoes implements FonteDadosPessoais {

    private final ObservacaoService observacoes;

    public FonteObservacoes(ObservacaoService observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String titulo() {
        return "Observações registradas pelo gestor";
    }

    @Override
    public List<String> itens(Long usuarioId) {
        return observacoes.sobreOTitular(usuarioId).stream()
                .map(o -> o.dataFatoTexto() + " · " + o.tipoRotulo() + " · " + o.descricao())
                .toList();
    }
}
