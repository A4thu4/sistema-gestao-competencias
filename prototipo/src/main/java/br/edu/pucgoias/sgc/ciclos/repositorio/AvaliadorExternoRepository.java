package br.edu.pucgoias.sgc.ciclos.repositorio;

import br.edu.pucgoias.sgc.ciclos.dominio.AvaliadorExterno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliadorExternoRepository extends JpaRepository<AvaliadorExterno, Long> {

    List<AvaliadorExterno> findByCicloId(Long cicloId);
}
