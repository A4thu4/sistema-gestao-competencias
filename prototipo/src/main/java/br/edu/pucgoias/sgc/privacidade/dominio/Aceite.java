package br.edu.pucgoias.sgc.privacidade.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/** Aceite de uma versão do termo, com data e hora (RF-29, ERS 3.4 regra 7). O usuário fica só como id. */
@Entity
@Table(name = "aceite")
public class Aceite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "termo_id")
    private TermoConsentimento termo;

    private OffsetDateTime aceitoEm;

    protected Aceite() {
    }

    public Aceite(Long usuarioId, TermoConsentimento termo, OffsetDateTime aceitoEm) {
        this.usuarioId = usuarioId;
        this.termo = termo;
        this.aceitoEm = aceitoEm;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public TermoConsentimento getTermo() { return termo; }
    public OffsetDateTime getAceitoEm() { return aceitoEm; }
}
