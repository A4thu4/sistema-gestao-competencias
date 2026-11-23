package br.edu.pucgoias.sgc.ciclos.dominio;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Quem avalia quem no ciclo, e em que relação (RF-10, RF-11). Guarda só o andamento (RF-13): a avaliação enviada
 * não aponta para a designação (RNF-01, DA-06). O avaliador é uma pessoa da empresa ({@code avaliadorId}) ou um
 * avaliador externo ({@code avaliadorExternoId}, com {@code token} do link individual, RF-34).
 */
@Entity
@Table(name = "designacao")
public class Designacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cicloId;
    private Long avaliadoId;
    private Long avaliadorId;
    private Long avaliadorExternoId;

    @Enumerated(EnumType.STRING)
    private TipoAvaliador relacao;

    @Enumerated(EnumType.STRING)
    private SituacaoDesignacao situacao = SituacaoDesignacao.NAO_INICIADA;

    private String token;
    private OffsetDateTime conviteEnviadoEm;
    private OffsetDateTime ultimoLembreteEm;

    protected Designacao() {
    }

    public static Designacao interna(Long cicloId, Long avaliadoId, Long avaliadorId, TipoAvaliador relacao) {
        if (relacao == TipoAvaliador.EXTERNO) {
            throw new IllegalArgumentException("Avaliador externo usa Designacao.externa");
        }
        if (relacao == TipoAvaliador.AUTO && !Objects.equals(avaliadoId, avaliadorId)) {
            throw new RegraDeNegocioException("Na autoavaliação, quem avalia é a própria pessoa.");
        }
        if (relacao != TipoAvaliador.AUTO && Objects.equals(avaliadoId, avaliadorId)) {
            throw new RegraDeNegocioException("Ninguém avalia a si mesmo fora da autoavaliação.");
        }
        Designacao d = new Designacao();
        d.cicloId = cicloId;
        d.avaliadoId = avaliadoId;
        d.avaliadorId = avaliadorId;
        d.relacao = relacao;
        return d;
    }

    public static Designacao externa(Long cicloId, Long avaliadoId, Long externoId, String token) {
        Designacao d = new Designacao();
        d.cicloId = cicloId;
        d.avaliadoId = avaliadoId;
        d.avaliadorExternoId = externoId;
        d.relacao = TipoAvaliador.EXTERNO;
        d.token = token;
        return d;
    }

    public boolean isExterna() {
        return avaliadorExternoId != null;
    }

    public boolean isPendente() {
        return situacao != SituacaoDesignacao.ENVIADA;
    }

    public void marcarEmPreenchimento() {
        if (situacao == SituacaoDesignacao.ENVIADA) {
            throw new RegraDeNegocioException("Esta avaliação já foi enviada.");
        }
        situacao = SituacaoDesignacao.EM_PREENCHIMENTO;
    }

    public void marcarEnviada() {
        if (situacao == SituacaoDesignacao.ENVIADA) {
            throw new RegraDeNegocioException("Esta avaliação já foi enviada.");
        }
        situacao = SituacaoDesignacao.ENVIADA;
    }

    /** UC-09: o RH reabre uma avaliação enviada, a pedido do avaliador. */
    public void reabrir() {
        if (situacao != SituacaoDesignacao.ENVIADA) {
            throw new RegraDeNegocioException("Só uma avaliação enviada pode ser reaberta.");
        }
        situacao = SituacaoDesignacao.EM_PREENCHIMENTO;
    }

    public void registrarConvite(OffsetDateTime quando) {
        conviteEnviadoEm = quando;
    }

    public void registrarLembrete(OffsetDateTime quando) {
        ultimoLembreteEm = quando;
    }

    /** Usado só pela carga de exemplo. */
    public void definirSituacao(SituacaoDesignacao nova) {
        situacao = nova;
    }

    public Long getId() { return id; }
    public Long getCicloId() { return cicloId; }
    public Long getAvaliadoId() { return avaliadoId; }
    public Long getAvaliadorId() { return avaliadorId; }
    public Long getAvaliadorExternoId() { return avaliadorExternoId; }
    public TipoAvaliador getRelacao() { return relacao; }
    public SituacaoDesignacao getSituacao() { return situacao; }
    public String getToken() { return token; }
    public OffsetDateTime getConviteEnviadoEm() { return conviteEnviadoEm; }
    public OffsetDateTime getUltimoLembreteEm() { return ultimoLembreteEm; }
}
