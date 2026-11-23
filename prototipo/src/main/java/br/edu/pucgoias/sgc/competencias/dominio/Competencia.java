package br.edu.pucgoias.sgc.competencias.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Competência do catálogo (RF-01). É desativada, nunca apagada, para preservar o histórico (ERS 3.4, regra 1). */
@Entity
@Table(name = "competencia")
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoCompetencia tipo;

    private boolean ativa = true;

    protected Competencia() {
    }

    public Competencia(String nome, String descricao, TipoCompetencia tipo) {
        alterar(nome, descricao, tipo);
    }

    public void alterar(String nome, String descricao, TipoCompetencia tipo) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public void desativar() {
        this.ativa = false;
    }

    public void reativar() {
        this.ativa = true;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public TipoCompetencia getTipo() { return tipo; }
    public boolean isAtiva() { return ativa; }
}
