package br.edu.pucgoias.sgc.compartilhado.repositorio;

import br.edu.pucgoias.sgc.compartilhado.dominio.EmailEnviado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailEnviadoRepository extends JpaRepository<EmailEnviado, Long> {

    Page<EmailEnviado> findAllByOrderByCriadoEmDescIdDesc(Pageable pagina);

    List<EmailEnviado> findByParaOrderByIdDesc(String para);

    long countByTipo(String tipo);
}
