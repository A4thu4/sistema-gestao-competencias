package br.edu.pucgoias.sgc.competencias.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Descrição de um nível da escala: por exemplo, 4 = "Frequentemente". */
@Entity
@Table(name = "nivel_escala")
public class NivelEscala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionario_id")
    private Questionario questionario;

    private short valor;
    private String descricao;

    protected NivelEscala() {
    }

    NivelEscala(Questionario questionario, int valor, String descricao) {
        this.questionario = questionario;
        this.valor = (short) valor;
        this.descricao = descricao;
    }

    void descrever(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public int getValor() { return valor; }
    public String getDescricao() { return descricao; }
}
