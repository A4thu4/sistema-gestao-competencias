package br.edu.pucgoias.sgc.compartilhado.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/** Linha do log de auditoria (RNF-05): quem, o quê, quando e por quê. */
@Entity
@Table(name = "registro_auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String usuario;
    private String acao;
    private String detalhe;
    private String motivo;
    private OffsetDateTime ocorridoEm;

    protected RegistroAuditoria() {
    }

    public RegistroAuditoria(EventoAuditoria evento) {
        this.usuario = evento.usuario();
        this.acao = evento.acao();
        this.detalhe = cortar(evento.detalhe());
        this.motivo = cortar(evento.motivo());
        this.ocorridoEm = OffsetDateTime.now();
    }

    private static String cortar(String texto) {
        return texto == null || texto.length() <= 500 ? texto : texto.substring(0, 500);
    }

    public Long getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getAcao() { return acao; }
    public String getDetalhe() { return detalhe; }
    public String getMotivo() { return motivo; }
    public OffsetDateTime getOcorridoEm() { return ocorridoEm; }
}
