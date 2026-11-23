package br.edu.pucgoias.sgc.avaliacoes.repositorio;

import br.edu.pucgoias.sgc.avaliacoes.dominio.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    List<Resposta> findByAvaliacaoEnviadaId(UUID avaliacaoEnviadaId);

    List<Resposta> findByAvaliacaoEnviadaIdIn(Collection<UUID> avaliacaoEnviadaIds);
}
