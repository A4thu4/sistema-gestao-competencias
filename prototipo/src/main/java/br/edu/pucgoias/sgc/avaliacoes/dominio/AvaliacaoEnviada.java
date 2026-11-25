package br.edu.pucgoias.sgc.avaliacoes.dominio;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

/**
 * Avaliação enviada, anônima por construção: guarda só ciclo, avaliado, relação e questionário. Não tem designação,
 * avaliador, data nem número sequencial (o id é um UUID aleatório). O {@code verificador} é um HMAC da designação com
 * a chave do servidor; só serve para o RH reabrir a avaliação a pedido do avaliador e nunca aparece em tela, URL, log
 * ou exportação.
 */
@Entity
@Table(name = "avaliacao_enviada")
public class AvaliacaoEnviada implements Persistable<UUID> {

    @Id
    private UUID id;
    private Long cicloId;
    private Long avaliadoId;

    @Enumerated(EnumType.STRING)
    private TipoAvaliador relacao;

    private Long questionarioId;
    private String verificador;

    /** O id é gerado aqui (UUID aleatório); isto evita a consulta extra do Spring Data antes de inserir. */
    @Transient
    private boolean nova = true;

    protected AvaliacaoEnviada() {
    }

    public AvaliacaoEnviada(Long cicloId, Long avaliadoId, TipoAvaliador relacao, Long questionarioId,
                            String verificador) {
        this.id = UUID.randomUUID();
        this.cicloId = cicloId;
        this.avaliadoId = avaliadoId;
        this.relacao = relacao;
        this.questionarioId = questionarioId;
        this.verificador = verificador;
    }

    @PostLoad
    @PostPersist
    void marcarGravada() {
        this.nova = false;
    }

    @Override
    public boolean isNew() { return nova; }

    @Override
    public UUID getId() { return id; }
    public Long getCicloId() { return cicloId; }
    public Long getAvaliadoId() { return avaliadoId; }
    public TipoAvaliador getRelacao() { return relacao; }
    public Long getQuestionarioId() { return questionarioId; }
    public String getVerificador() { return verificador; }
}
