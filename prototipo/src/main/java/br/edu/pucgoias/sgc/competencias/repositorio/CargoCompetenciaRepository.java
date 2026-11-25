package br.edu.pucgoias.sgc.competencias.repositorio;

import br.edu.pucgoias.sgc.competencias.dominio.CargoCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargoCompetenciaRepository extends JpaRepository<CargoCompetencia, CargoCompetencia.Chave> {

    List<CargoCompetencia> findByCargoId(Long cargoId);
}
