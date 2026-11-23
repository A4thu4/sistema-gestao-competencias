package br.edu.pucgoias.sgc.avaliacoes.repositorio;

import br.edu.pucgoias.sgc.avaliacoes.dominio.RascunhoResposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RascunhoRespostaRepository extends JpaRepository<RascunhoResposta, Long> {

    List<RascunhoResposta> findByDesignacaoId(Long designacaoId);

    Optional<RascunhoResposta> findByDesignacaoIdAndPerguntaId(Long designacaoId, Long perguntaId);

    boolean existsByPerguntaIdIn(Collection<Long> perguntaIds);
}
