package br.edu.pucgoias.sgc.resultados.dominio;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Média de um grupo de avaliadores numa competência de uma pessoa avaliada num ciclo (RF-19). Guarda o número de
 * avaliações do grupo, para aplicar o mínimo de 3 (RNF-02), e a escala do questionário, para comparar ciclos
 * (RF-26). É derivada das respostas e recalculada sempre do mesmo jeito (RNF-12). Ciclo, pessoa e competência
 * ficam só como id (são de outros pacotes).
 */
@Entity
@Table(name = "resultado_consolidado")
public class ResultadoConsolidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cicloId;
    private Long avaliadoId;
    private Long competenciaId;

    @Enumerated(EnumType.STRING)
    private TipoAvaliador grupo;

    @Column(precision = 4, scale = 2)
    private BigDecimal media;

    private int numRespostas;
    private short escalaMax;

    protected ResultadoConsolidado() {
    }

    public ResultadoConsolidado(Long cicloId, Long avaliadoId, Long competenciaId, TipoAvaliador grupo,
                                BigDecimal media, int numRespostas, int escalaMax) {
        this.cicloId = cicloId;
        this.avaliadoId = avaliadoId;
        this.competenciaId = competenciaId;
        this.grupo = grupo;
        this.media = media;
        this.numRespostas = numRespostas;
        this.escalaMax = (short) escalaMax;
    }

    public Long getId() { return id; }
    public Long getCicloId() { return cicloId; }
    public Long getAvaliadoId() { return avaliadoId; }
    public Long getCompetenciaId() { return competenciaId; }
    public TipoAvaliador getGrupo() { return grupo; }
    public BigDecimal getMedia() { return media; }
    public int getNumRespostas() { return numRespostas; }
    public int getEscalaMax() { return escalaMax; }
}
