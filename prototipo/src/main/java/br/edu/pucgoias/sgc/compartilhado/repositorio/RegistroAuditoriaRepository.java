package br.edu.pucgoias.sgc.compartilhado.repositorio;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    Page<RegistroAuditoria> findAllByOrderByOcorridoEmDescIdDesc(Pageable pagina);

    boolean existsByAcaoAndDetalheContaining(String acao, String trecho);
}
