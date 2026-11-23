package br.edu.pucgoias.sgc.identidade.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.identidade.dominio.Perfil;
import br.edu.pucgoias.sgc.identidade.dominio.Usuario;
import br.edu.pucgoias.sgc.identidade.repositorio.CargoRepository;
import br.edu.pucgoias.sgc.identidade.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interface do pacote identidade para os demais: pessoas, cargos e estrutura da equipe. Devolve sempre
 * {@link PessoaResumo} e {@link CargoResumo}.
 */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final CargoRepository cargos;

    public UsuarioService(UsuarioRepository usuarios, CargoRepository cargos) {
        this.usuarios = usuarios;
        this.cargos = cargos;
    }

    public PessoaResumo buscar(Long id) {
        return usuarios.findById(id).map(PessoaResumo::de)
                .orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada."));
    }

    public PessoaResumo buscarPorEmail(String email) {
        return usuarios.findByEmailIgnoreCase(email).map(PessoaResumo::de)
                .orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada."));
    }

    public Map<Long, PessoaResumo> buscarPorIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return usuarios.findAllById(ids).stream().map(PessoaResumo::de)
                .collect(Collectors.toMap(PessoaResumo::id, Function.identity()));
    }

    public List<PessoaResumo> listarAtivos() {
        return usuarios.findByAtivoTrueOrderByNome().stream().map(PessoaResumo::de).toList();
    }

    public List<PessoaResumo> listarPorCargo(Long cargoId) {
        return usuarios.findByCargoIdAndAtivoTrueOrderByNome(cargoId).stream().map(PessoaResumo::de).toList();
    }

    public List<PessoaResumo> listarPorArea(String area) {
        return usuarios.findByAreaAndAtivoTrueOrderByNome(area).stream().map(PessoaResumo::de).toList();
    }

    public List<String> areas() {
        return usuarios.findByAtivoTrueOrderByNome().stream().map(Usuario::getArea).distinct().sorted().toList();
    }

    /** Equipe direta: quem tem este gestor como chefe imediato. */
    public List<PessoaResumo> equipeDireta(Long gestorId) {
        return usuarios.findByGestor_IdAndAtivoTrueOrderByNome(gestorId).stream().map(PessoaResumo::de).toList();
    }

    public boolean ehGestorDireto(Long gestorId, Long colaboradorId) {
        return usuarios.findById(colaboradorId)
                .map(u -> Objects.equals(u.getGestorId(), gestorId))
                .orElse(false);
    }

    /** Pares: pessoas com o mesmo gestor direto, sem a própria pessoa. */
    public List<PessoaResumo> pares(Long usuarioId) {
        Usuario u = usuarios.findById(usuarioId).orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada."));
        if (u.getGestorId() == null) {
            return List.of();
        }
        return usuarios.findByGestor_IdAndAtivoTrueOrderByNome(u.getGestorId()).stream()
                .filter(p -> !p.getId().equals(usuarioId))
                .map(PessoaResumo::de).toList();
    }

    public List<PessoaResumo> listarPorPerfil(Perfil perfil) {
        return usuarios.findByAtivoTrueOrderByNome().stream()
                .filter(u -> u.getPerfil() == perfil).map(PessoaResumo::de).toList();
    }

    public List<CargoResumo> cargos() {
        return cargos.findAllByOrderByNome().stream().map(c -> new CargoResumo(c.getId(), c.getNome())).toList();
    }

    public CargoResumo cargo(Long id) {
        return cargos.findById(id).map(c -> new CargoResumo(c.getId(), c.getNome()))
                .orElseThrow(() -> new NaoEncontradoException("Cargo não encontrado."));
    }

    /** Correção de nome a pedido do titular, depois da análise do RH (UC-05, 10a). */
    @Transactional
    public void corrigirNome(Long id, String novoNome) {
        usuarios.findById(id).orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada."))
                .corrigirNome(novoNome);
    }

    /** Exclusão a pedido do titular (UC-05): bloqueia o acesso e substitui nome e e-mail. */
    @Transactional
    public void anonimizar(Long id) {
        usuarios.findById(id).orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada."))
                .anonimizar();
    }
}
