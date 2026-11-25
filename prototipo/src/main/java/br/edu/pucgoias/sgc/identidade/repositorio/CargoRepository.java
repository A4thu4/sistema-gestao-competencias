package br.edu.pucgoias.sgc.identidade.repositorio;

import br.edu.pucgoias.sgc.identidade.dominio.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    List<Cargo> findAllByOrderByNome();

    Optional<Cargo> findByNome(String nome);
}
