package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoCiclo;
import br.edu.pucgoias.sgc.ciclos.repositorio.DesignacaoRepository;
import br.edu.pucgoias.sgc.privacidade.servico.ParticipacaoEmCiclo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa, para o pacote privacidade, a pergunta "a pessoa participa de um ciclo aberto?" (UC-05, 7c): ela é
 * avaliada ou avaliadora em algum ciclo ABERTO.
 */
@Component
public class ParticipacaoEmCicloAberto implements ParticipacaoEmCiclo {

    private final DesignacaoRepository designacoes;

    public ParticipacaoEmCicloAberto(DesignacaoRepository designacoes) {
        this.designacoes = designacoes;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean participaDeCicloAberto(Long usuarioId) {
        return usuarioId != null && designacoes.participaDeCiclo(usuarioId, SituacaoCiclo.ABERTO);
    }
}
