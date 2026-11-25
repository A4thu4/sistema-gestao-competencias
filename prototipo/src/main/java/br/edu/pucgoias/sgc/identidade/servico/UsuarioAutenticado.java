package br.edu.pucgoias.sgc.identidade.servico;

import br.edu.pucgoias.sgc.identidade.dominio.Perfil;
import br.edu.pucgoias.sgc.identidade.dominio.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Usuário da sessão. Os controllers recebem com {@code @AuthenticationPrincipal UsuarioAutenticado eu} e
 * usam {@link #id()} e {@link #perfil()} para conferir o acesso no servidor.
 */
public class UsuarioAutenticado implements UserDetails {

    private final Long id;
    private final String nome;
    private final String email;
    private final String senhaHash;
    private final Perfil perfil;
    private final String cargo;
    private final boolean ativo;

    UsuarioAutenticado(Usuario u) {
        this.id = u.getId();
        this.nome = u.getNome();
        this.email = u.getEmail();
        this.senhaHash = u.getSenhaHash();
        this.perfil = u.getPerfil();
        this.cargo = u.getCargo().getNome();
        this.ativo = u.isAtivo();
    }

    public Long id() { return id; }
    public String nome() { return nome; }
    public String email() { return email; }
    public Perfil perfil() { return perfil; }
    public String cargo() { return cargo; }

    public boolean ehRh() { return perfil == Perfil.RH; }
    public boolean ehGestor() { return perfil == Perfil.GESTOR; }

    /** Para o Thymeleaf: {@code ${#authentication.principal.nome}}. */
    public String getNome() { return nome; }
    public String getPerfilRotulo() { return perfil.getRotulo(); }
    public String getIniciais() {
        String[] partes = nome.trim().split("\\s+");
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (partes[0].substring(0, 1) + ultima).toUpperCase();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() { return senhaHash; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isEnabled() { return ativo; }
}
