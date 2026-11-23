package br.edu.pucgoias.sgc.resultados.dominio;

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
 * Meta do plano de desenvolvimento do colaborador (UC-06, RF-27): competência, descrição, ações previstas (por
 * exemplo, um curso), prazo e situação. {@code cicloId} é o ciclo cujo resultado embasou a meta.
 * {@code ultimoLembreteEm} evita repetir o lembrete trimestral em menos de 90 dias (RF-28).
 */
@Entity
@Table(name = "meta_desenvolvimento")
public class MetaDesenvolvimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long colaboradorId;
    private Long gestorId;
    private Long cicloId;
    private Long competenciaId;
    private String descricao;
    private String acoes;
    private LocalDate prazo;

    @Enumerated(EnumType.STRING)
    private SituacaoMeta situacao;

    private OffsetDateTime criadaEm;
    private OffsetDateTime ultimoLembreteEm;

    protected MetaDesenvolvimento() {
    }

    public MetaDesenvolvimento(Long colaboradorId, Long gestorId, Long cicloId, Long competenciaId, String descricao,
                               String acoes, LocalDate prazo, SituacaoMeta situacao, OffsetDateTime criadaEm) {
        this.colaboradorId = colaboradorId;
        this.gestorId = gestorId;
        this.cicloId = cicloId;
        this.competenciaId = competenciaId;
        this.descricao = descricao;
        this.acoes = acoes;
        this.prazo = prazo;
        this.situacao = situacao;
        this.criadaEm = criadaEm;
    }

    public void mudarSituacao(SituacaoMeta nova) {
        this.situacao = nova;
    }

    public void registrarLembrete(OffsetDateTime quando) {
        this.ultimoLembreteEm = quando;
    }

    public Long getId() { return id; }
    public Long getColaboradorId() { return colaboradorId; }
    public Long getGestorId() { return gestorId; }
    public Long getCicloId() { return cicloId; }
    public Long getCompetenciaId() { return competenciaId; }
    public String getDescricao() { return descricao; }
    public String getAcoes() { return acoes; }
    public LocalDate getPrazo() { return prazo; }
    public SituacaoMeta getSituacao() { return situacao; }
    public OffsetDateTime getCriadaEm() { return criadaEm; }
    public OffsetDateTime getUltimoLembreteEm() { return ultimoLembreteEm; }
}
