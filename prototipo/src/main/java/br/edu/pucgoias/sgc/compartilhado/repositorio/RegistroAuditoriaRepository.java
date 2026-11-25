package br.edu.pucgoias.sgc.compartilhado.repositorio;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

import java.util.List;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long>,
        JpaSpecificationExecutor<RegistroAuditoria> {

    @Query("select distinct r.acao from RegistroAuditoria r order by r.acao")
    List<String> acoes();

    Page<RegistroAuditoria> findAllByOrderByOcorridoEmDescIdDesc(Pageable pagina);

    boolean existsByAcaoAndDetalheContaining(String acao, String trecho);

    @Query("select coalesce(max(r.id), 0) from RegistroAuditoria r")
    long ultimoId();

    @Modifying(flushAutomatically = true)
    @Query("update RegistroAuditoria r set r.ocorridoEm = :quando where r.id > :id")
    int datarDepoisDe(@Param("id") long id, @Param("quando") OffsetDateTime quando);
}
