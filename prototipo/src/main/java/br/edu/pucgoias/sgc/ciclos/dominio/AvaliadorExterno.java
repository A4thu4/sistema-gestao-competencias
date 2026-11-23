package br.edu.pucgoias.sgc.ciclos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Avaliador externo (cliente, fornecedor ou parceiro) convidado para o ciclo: só nome e e-mail (RNF-06, RF-34). */
@Entity
@Table(name = "avaliador_externo")
public class AvaliadorExterno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cicloId;
    private String nome;
    private String email;

    protected AvaliadorExterno() {
    }

    public AvaliadorExterno(Long cicloId, String nome, String email) {
        this.cicloId = cicloId;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public Long getCicloId() { return cicloId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
