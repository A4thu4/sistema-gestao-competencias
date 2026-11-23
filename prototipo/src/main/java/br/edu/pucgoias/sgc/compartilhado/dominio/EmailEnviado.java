package br.edu.pucgoias.sgc.compartilhado.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/** Mensagem da caixa de saída. O protótipo não envia e-mail de verdade; o RH vê as mensagens na tela "E-mails". */
@Entity
@Table(name = "email_enviado")
public class EmailEnviado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String para;
    private String assunto;
    private String corpo;
    private String tipo;
    private OffsetDateTime criadoEm;

    protected EmailEnviado() {
    }

    public EmailEnviado(String para, String assunto, String corpo, String tipo) {
        this.para = para;
        this.assunto = assunto;
        this.corpo = corpo.length() > 4000 ? corpo.substring(0, 4000) : corpo;
        this.tipo = tipo;
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getPara() { return para; }
    public String getAssunto() { return assunto; }
    public String getCorpo() { return corpo; }
    public String getTipo() { return tipo; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
}
