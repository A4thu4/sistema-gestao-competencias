package br.edu.pucgoias.sgc.identidade.servico;

import br.edu.pucgoias.sgc.identidade.dominio.Perfil;
import br.edu.pucgoias.sgc.identidade.dominio.Usuario;

/**
 * Visão de uma pessoa para os outros pacotes. Eles guardam só o id do usuário e pedem este resumo ao
 * {@link UsuarioService}, sem acessar a entidade {@code Usuario} (regra de dependência do README do protótipo).
 */
public record PessoaResumo(Long id, String nome, String email, Perfil perfil, Long cargoId, String cargoNome,
                           Long gestorId, String area, boolean ativo) {

    static PessoaResumo de(Usuario u) {
        return new PessoaResumo(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getCargo().getId(),
                u.getCargo().getNome(), u.getGestorId(), u.getArea(), u.isAtivo());
    }

    public String iniciais() {
        String[] partes = nome.trim().split("\\s+");
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (partes[0].substring(0, 1) + ultima).toUpperCase();
    }
}
