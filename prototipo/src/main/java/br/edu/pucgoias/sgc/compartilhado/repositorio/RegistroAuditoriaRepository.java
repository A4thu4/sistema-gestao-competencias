package br.edu.pucgoias.sgc.compartilhado.repositorio;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long>,
        JpaSpecificationExecutor<RegistroAuditoria> {

    @Query("select distinct r.acao from RegistroAuditoria r order by r.acao")
    List<String> acoes();

    Page<RegistroAuditoria> findAllByOrderByOcorridoEmDescIdDesc(Pageable pagina);

    boolean existsByAcaoAndDetalheContaining(String acao, String trecho);
}
