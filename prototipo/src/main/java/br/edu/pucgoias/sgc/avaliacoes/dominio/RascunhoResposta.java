package br.edu.pucgoias.sgc.avaliacoes.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Resposta em preenchimento, ligada à designação para o salvamento automático (RF-14). Existe só até o envio: aí é
 * copiada para {@link Resposta} (sem designação) e apagada (RNF-01, DA-06).
 */
@Entity
@Table(name = "rascunho_resposta")
public class RascunhoResposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long designacaoId;
    private Long perguntaId;
    private Short nota;
    private boolean naoAvalia;
    private String texto;
    private OffsetDateTime atualizadoEm;

    protected RascunhoResposta() {
    }

    public RascunhoResposta(Long designacaoId, Long perguntaId) {
        this.designacaoId = designacaoId;
        this.perguntaId = perguntaId;
    }

    public void preencher(Integer nota, boolean naoAvalia, String texto, OffsetDateTime quando) {
        this.nota = nota == null ? null : nota.shortValue();
        this.naoAvalia = naoAvalia;
        this.texto = texto;
        this.atualizadoEm = quando;
    }

    public Long getId() { return id; }
    public Long getDesignacaoId() { return designacaoId; }
    public Long getPerguntaId() { return perguntaId; }
    public Integer getNota() { return nota == null ? null : nota.intValue(); }
    public boolean isNaoAvalia() { return naoAvalia; }
    public String getTexto() { return texto; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}
