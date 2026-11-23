package br.edu.pucgoias.sgc.identidade.repositorio;

import br.edu.pucgoias.sgc.identidade.dominio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findByGestor_IdAndAtivoTrueOrderByNome(Long gestorId);

    List<Usuario> findByAtivoTrueOrderByNome();

    List<Usuario> findByCargoIdAndAtivoTrueOrderByNome(Long cargoId);

    List<Usuario> findByAreaAndAtivoTrueOrderByNome(String area);

    List<Usuario> findByIdInOrderByNome(Collection<Long> ids);
}
