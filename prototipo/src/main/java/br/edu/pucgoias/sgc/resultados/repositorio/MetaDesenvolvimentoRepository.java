package br.edu.pucgoias.sgc.resultados.repositorio;

import br.edu.pucgoias.sgc.resultados.dominio.MetaDesenvolvimento;
import br.edu.pucgoias.sgc.resultados.dominio.SituacaoMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MetaDesenvolvimentoRepository extends JpaRepository<MetaDesenvolvimento, Long> {

    List<MetaDesenvolvimento> findByColaboradorIdOrderByPrazoAscIdAsc(Long colaboradorId);

    List<MetaDesenvolvimento> findByColaboradorIdIn(Collection<Long> colaboradorIds);

    List<MetaDesenvolvimento> findBySituacaoIn(Collection<SituacaoMeta> situacoes);
}
