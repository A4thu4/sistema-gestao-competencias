package br.edu.pucgoias.sgc.identidade.servico;

import br.edu.pucgoias.sgc.identidade.repositorio.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Carrega o usuário pelo e-mail para o Spring Security conferir a senha. */
@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarios;

    public AutenticacaoService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        return usuarios.findByEmailIgnoreCase(email.trim())
                .map(UsuarioAutenticado::new)
                .orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado"));
    }
}
