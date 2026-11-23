package br.edu.pucgoias.sgc.competencias.repositorio;

import br.edu.pucgoias.sgc.competencias.dominio.Questionario;
import br.edu.pucgoias.sgc.competencias.dominio.SituacaoQuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionarioRepository extends JpaRepository<Questionario, Long> {

    List<Questionario> findByModeloFalseOrderByNome();

    List<Questionario> findByModeloTrueOrderByNome();

    List<Questionario> findByModeloFalseAndSituacaoOrderByNome(SituacaoQuestionario situacao);

    List<Questionario> findByModeloFalseAndSituacaoAndCargoIdOrderByNome(SituacaoQuestionario situacao, Long cargoId);

    long countByModeloFalseAndSituacao(SituacaoQuestionario situacao);

    /** Questionários (não modelos) que usam a competência, na situação informada. */
    @Query("""
            select q from Questionario q join q.competencias c
            where c.competenciaId = :competenciaId and q.modelo = false and q.situacao = :situacao
            order by q.nome""")
    List<Questionario> usandoCompetencia(@Param("competenciaId") Long competenciaId,
                                         @Param("situacao") SituacaoQuestionario situacao);

    /** Pares [id da competência, quantidade de questionários (não modelos) que a usam]. */
    @Query("""
            select c.competenciaId, count(q) from Questionario q join q.competencias c
            where q.modelo = false group by c.competenciaId""")
    List<Object[]> contarUsoPorCompetencia();
}
