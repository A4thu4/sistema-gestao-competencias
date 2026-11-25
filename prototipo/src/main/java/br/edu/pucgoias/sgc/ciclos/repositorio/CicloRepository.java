package br.edu.pucgoias.sgc.ciclos.repositorio;

import br.edu.pucgoias.sgc.ciclos.dominio.Ciclo;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoCiclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface CicloRepository extends JpaRepository<Ciclo, Long> {

    List<Ciclo> findAllByOrderByDataAberturaDescIdDesc();

    List<Ciclo> findBySituacaoOrderByDataAberturaDescIdDesc(SituacaoCiclo situacao);

    List<Ciclo> findBySituacaoInOrderByDataAberturaDescIdDesc(Collection<SituacaoCiclo> situacoes);

    List<Ciclo> findBySituacaoAndDataAberturaLessThanEqual(SituacaoCiclo situacao, LocalDate data);

    List<Ciclo> findBySituacaoAndDataEncerramentoBefore(SituacaoCiclo situacao, LocalDate data);

    /** Nomes dos ciclos (de qualquer situação) que usam o questionário (UC-01, extensão 2b). */
    @Query(value = """
            select c.nome from ciclo c join ciclo_questionario cq on cq.ciclo_id = c.id
            where cq.questionario_id = :questionarioId order by c.data_abertura, c.nome""", nativeQuery = true)
    List<String> nomesQueUsamQuestionario(@Param("questionarioId") Long questionarioId);
}
