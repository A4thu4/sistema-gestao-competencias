package br.edu.pucgoias.sgc.avaliacoes.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Observação datada que o gestor registra sobre um membro da equipe ao longo do ano (UC-08, RF-17). Gestor e
 * colaborador ficam só como id (pessoas são do pacote identidade).
 */
@Entity
@Table(name = "observacao")
public class Observacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long gestorId;
    private Long colaboradorId;

    @Enumerated(EnumType.STRING)
    private TipoObservacao tipo;

    private String descricao;
    private LocalDate dataFato;
    private OffsetDateTime criadaEm;

    protected Observacao() {
    }

    public Observacao(Long gestorId, Long colaboradorId, TipoObservacao tipo, String descricao, LocalDate dataFato,
                      OffsetDateTime criadaEm) {
        this.gestorId = gestorId;
        this.colaboradorId = colaboradorId;
        this.criadaEm = criadaEm;
        alterar(tipo, descricao, dataFato);
    }

    public void alterar(TipoObservacao tipo, String descricao, LocalDate dataFato) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataFato = dataFato;
    }

    public boolean registradaPor(Long usuarioId) {
        return gestorId.equals(usuarioId);
    }

    public Long getId() { return id; }
    public Long getGestorId() { return gestorId; }
    public Long getColaboradorId() { return colaboradorId; }
    public TipoObservacao getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataFato() { return dataFato; }
    public OffsetDateTime getCriadaEm() { return criadaEm; }
}
