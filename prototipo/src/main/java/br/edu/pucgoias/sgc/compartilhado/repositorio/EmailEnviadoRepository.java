package br.edu.pucgoias.sgc.compartilhado.repositorio;

import br.edu.pucgoias.sgc.compartilhado.dominio.EmailEnviado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface EmailEnviadoRepository extends JpaRepository<EmailEnviado, Long> {

    Page<EmailEnviado> findAllByOrderByCriadoEmDescIdDesc(Pageable pagina);

    List<EmailEnviado> findByParaOrderByIdDesc(String para);

    long countByTipo(String tipo);

    @Query("select coalesce(max(e.id), 0) from EmailEnviado e")
    long ultimoId();

    @Modifying(flushAutomatically = true)
    @Query("update EmailEnviado e set e.criadoEm = :quando where e.id > :id")
    int datarDepoisDe(@Param("id") long id, @Param("quando") OffsetDateTime quando);
}
