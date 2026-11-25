package br.edu.pucgoias.sgc.resultados.repositorio;

import br.edu.pucgoias.sgc.resultados.dominio.ResultadoConsolidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ResultadoConsolidadoRepository extends JpaRepository<ResultadoConsolidado, Long> {

    List<ResultadoConsolidado> findByCicloIdAndAvaliadoId(Long cicloId, Long avaliadoId);

    List<ResultadoConsolidado> findByCicloIdAndAvaliadoIdIn(Long cicloId, Collection<Long> avaliadoIds);

    List<ResultadoConsolidado> findByAvaliadoId(Long avaliadoId);

    boolean existsByCicloId(Long cicloId);

    boolean existsByCicloIdAndAvaliadoId(Long cicloId, Long avaliadoId);

    @Query("select distinct r.avaliadoId from ResultadoConsolidado r where r.cicloId = :cicloId")
    List<Long> avaliadosComResultado(@Param("cicloId") Long cicloId);

    @Query("select distinct r.cicloId from ResultadoConsolidado r where r.avaliadoId = :avaliadoId")
    List<Long> ciclosDoAvaliado(@Param("avaliadoId") Long avaliadoId);

    @Query("select distinct r.cicloId from ResultadoConsolidado r")
    List<Long> ciclosConsolidados();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ResultadoConsolidado r where r.cicloId = :cicloId")
    int apagarDoCiclo(@Param("cicloId") Long cicloId);
}
