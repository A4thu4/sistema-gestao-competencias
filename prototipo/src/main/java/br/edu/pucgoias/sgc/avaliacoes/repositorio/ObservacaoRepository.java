package br.edu.pucgoias.sgc.avaliacoes.repositorio;

import br.edu.pucgoias.sgc.avaliacoes.dominio.Observacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ObservacaoRepository extends JpaRepository<Observacao, Long> {

    List<Observacao> findByColaboradorIdOrderByDataFatoDescCriadaEmDesc(Long colaboradorId);

    List<Observacao> findByColaboradorIdAndDataFatoBetweenOrderByDataFatoDescCriadaEmDesc(
            Long colaboradorId, LocalDate inicio, LocalDate fim);

    List<Observacao> findByColaboradorIdIn(Collection<Long> colaboradorIds);
}
