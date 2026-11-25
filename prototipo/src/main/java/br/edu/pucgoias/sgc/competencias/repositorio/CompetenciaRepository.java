package br.edu.pucgoias.sgc.competencias.repositorio;

import br.edu.pucgoias.sgc.competencias.dominio.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    List<Competencia> findAllByOrderByNome();

    List<Competencia> findByAtivaTrueOrderByNome();

}
