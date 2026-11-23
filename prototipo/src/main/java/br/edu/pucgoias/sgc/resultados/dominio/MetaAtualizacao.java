package br.edu.pucgoias.sgc.resultados.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Registro de progresso de uma meta (UC-06, RF-28): quem atualizou, quando, a situação escolhida e o texto. Forma a
 * linha do tempo da meta; nunca é alterado depois de gravado.
 */
@Entity
@Table(name = "meta_atualizacao")
public class MetaAtualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long metaId;
    private Long autorId;

    @Enumerated(EnumType.STRING)
    private SituacaoMeta situacao;

    private String progresso;
    private OffsetDateTime criadaEm;

    protected MetaAtualizacao() {
    }

    public MetaAtualizacao(Long metaId, Long autorId, SituacaoMeta situacao, String progresso,
                           OffsetDateTime criadaEm) {
        this.metaId = metaId;
        this.autorId = autorId;
        this.situacao = situacao;
        this.progresso = progresso;
        this.criadaEm = criadaEm;
    }

    public Long getId() { return id; }
    public Long getMetaId() { return metaId; }
    public Long getAutorId() { return autorId; }
    public SituacaoMeta getSituacao() { return situacao; }
    public String getProgresso() { return progresso; }
    public OffsetDateTime getCriadaEm() { return criadaEm; }
}
