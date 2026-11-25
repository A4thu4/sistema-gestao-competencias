package br.edu.pucgoias.sgc.avaliacoes.repositorio;

import br.edu.pucgoias.sgc.avaliacoes.dominio.AvaliacaoEnviada;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoEnviadaRepository extends JpaRepository<AvaliacaoEnviada, UUID> {

    List<AvaliacaoEnviada> findByCicloId(Long cicloId);

    List<AvaliacaoEnviada> findByCicloIdAndAvaliadoId(Long cicloId, Long avaliadoId);

    long countByCicloIdAndAvaliadoIdAndRelacao(Long cicloId, Long avaliadoId, TipoAvaliador relacao);

    Optional<AvaliacaoEnviada> findByVerificador(String verificador);

    boolean existsByQuestionarioId(Long questionarioId);
}
