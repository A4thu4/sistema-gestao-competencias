package br.edu.pucgoias.sgc.ciclos.repositorio;

import br.edu.pucgoias.sgc.ciclos.dominio.Designacao;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoCiclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DesignacaoRepository extends JpaRepository<Designacao, Long> {

    List<Designacao> findByCicloIdOrderById(Long cicloId);

    List<Designacao> findByCicloIdAndAvaliadoIdOrderById(Long cicloId, Long avaliadoId);

    List<Designacao> findByCicloIdAndConviteEnviadoEmIsNullOrderById(Long cicloId);

    Optional<Designacao> findByToken(String token);

    long countByCicloId(Long cicloId);

    /** Designações em que a pessoa é avaliadora, nos ciclos com estas situações. */
    @Query("""
            select d from Designacao d, Ciclo c
            where c.id = d.cicloId and d.avaliadorId = :pessoaId and c.situacao in :situacoes
            order by c.dataAbertura desc, d.id""")
    List<Designacao> doAvaliadorNosCiclos(@Param("pessoaId") Long pessoaId,
                                          @Param("situacoes") Collection<SituacaoCiclo> situacoes);

    /** A pessoa é avaliada ou avaliadora em algum ciclo com esta situação. */
    @Query("""
            select count(d) > 0 from Designacao d, Ciclo c
            where c.id = d.cicloId and c.situacao = :situacao
              and (d.avaliadoId = :pessoaId or d.avaliadorId = :pessoaId)""")
    boolean participaDeCiclo(@Param("pessoaId") Long pessoaId, @Param("situacao") SituacaoCiclo situacao);
}
