package br.edu.pucgoias.sgc.identidade.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/** Pessoa da organização. A equipe direta de um gestor são os usuários que apontam para ele em {@code gestor}. */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestor_id")
    private Usuario gestor;

    private String area;
    private boolean ativo = true;
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    protected Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash, Perfil perfil, Cargo cargo, Usuario gestor, String area) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.cargo = cargo;
        this.gestor = gestor;
        this.area = area;
    }

    /** Correção de dado a pedido do titular (UC-05, 10a). */
    public void corrigirNome(String nome) {
        this.nome = nome;
    }

    /** Exclusão a pedido do titular (UC-05): o acesso é bloqueado e os dados pessoais são substituídos. */
    public void anonimizar() {
        this.nome = "Titular excluído #" + id;
        this.email = "excluido-" + id + "@anonimizado.invalid";
        this.senhaHash = "{noop}!";
        this.ativo = false;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public Perfil getPerfil() { return perfil; }
    public Cargo getCargo() { return cargo; }
    public Usuario getGestor() { return gestor; }
    public Long getGestorId() { return gestor == null ? null : gestor.getId(); }
    public String getArea() { return area; }
    public boolean isAtivo() { return ativo; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }

    public String getIniciais() {
        String[] partes = nome.trim().split("\\s+");
        String primeira = partes[0].substring(0, 1);
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (primeira + ultima).toUpperCase();
    }
}
