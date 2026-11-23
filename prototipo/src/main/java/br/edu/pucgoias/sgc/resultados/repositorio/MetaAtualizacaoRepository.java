package br.edu.pucgoias.sgc.resultados.repositorio;

import br.edu.pucgoias.sgc.resultados.dominio.MetaAtualizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MetaAtualizacaoRepository extends JpaRepository<MetaAtualizacao, Long> {

    List<MetaAtualizacao> findByMetaIdInOrderByCriadaEmDescIdDesc(Collection<Long> metaIds);
}
