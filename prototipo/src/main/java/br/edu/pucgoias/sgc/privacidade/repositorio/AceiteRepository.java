package br.edu.pucgoias.sgc.privacidade.repositorio;

import br.edu.pucgoias.sgc.privacidade.dominio.Aceite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AceiteRepository extends JpaRepository<Aceite, Long> {

    boolean existsByUsuarioIdAndTermo_Id(Long usuarioId, Long termoId);

    Optional<Aceite> findFirstByUsuarioIdOrderByAceitoEmDescIdDesc(Long usuarioId);

    List<Aceite> findByUsuarioIdOrderByAceitoEmDescIdDesc(Long usuarioId);

    List<Aceite> findByTermo_Id(Long termoId);
}
