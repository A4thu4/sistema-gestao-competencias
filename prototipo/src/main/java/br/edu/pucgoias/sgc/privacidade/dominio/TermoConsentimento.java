package br.edu.pucgoias.sgc.privacidade.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Versão do termo de consentimento publicada pelo RH. A vigente é a mais recente: quando o RH publica uma nova,
 * todos precisam aceitar de novo.
 */
@Entity
@Table(name = "termo_consentimento")
public class TermoConsentimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String versao;

    @Column(columnDefinition = "text")
    private String texto;
    private String resumoMudancas;
    private OffsetDateTime vigenteDesde;

    protected TermoConsentimento() {
    }

    public TermoConsentimento(String versao, String texto, String resumoMudancas, OffsetDateTime vigenteDesde) {
        this.versao = versao;
        this.texto = texto;
        this.resumoMudancas = resumoMudancas;
        this.vigenteDesde = vigenteDesde;
    }

    public Long getId() { return id; }
    public String getVersao() { return versao; }
    public String getTexto() { return texto; }
    public String getResumoMudancas() { return resumoMudancas; }
    public OffsetDateTime getVigenteDesde() { return vigenteDesde; }
}
