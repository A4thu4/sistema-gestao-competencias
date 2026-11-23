package br.edu.pucgoias.sgc.avaliacoes.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Resposta de uma pergunta numa avaliação enviada. Comentários de perguntas abertas podem ser ocultados pelo RH, com
 * motivo (UC-09, RF-22); o texto continua guardado, mas some dos resultados.
 */
@Entity
@Table(name = "resposta")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID avaliacaoEnviadaId;
    private Long perguntaId;
    private Short nota;
    private boolean naoAvalia;
    private String texto;
    private boolean comentarioOculto;
    private String motivoOcultacao;

    protected Resposta() {
    }

    public Resposta(UUID avaliacaoEnviadaId, Long perguntaId, Integer nota, boolean naoAvalia, String texto) {
        this.avaliacaoEnviadaId = avaliacaoEnviadaId;
        this.perguntaId = perguntaId;
        this.nota = nota == null ? null : nota.shortValue();
        this.naoAvalia = naoAvalia;
        this.texto = texto;
    }

    public void ocultar(String motivo) {
        this.comentarioOculto = true;
        this.motivoOcultacao = motivo;
    }

    public void exibir() {
        this.comentarioOculto = false;
        this.motivoOcultacao = null;
    }

    public boolean temComentario() {
        return texto != null && !texto.isBlank();
    }

    public Long getId() { return id; }
    public UUID getAvaliacaoEnviadaId() { return avaliacaoEnviadaId; }
    public Long getPerguntaId() { return perguntaId; }
    public Integer getNota() { return nota == null ? null : nota.intValue(); }
    public boolean isNaoAvalia() { return naoAvalia; }
    public String getTexto() { return texto; }
    public boolean isComentarioOculto() { return comentarioOculto; }
    public String getMotivoOcultacao() { return motivoOcultacao; }
}
