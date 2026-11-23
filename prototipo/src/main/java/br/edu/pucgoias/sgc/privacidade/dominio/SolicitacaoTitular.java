package br.edu.pucgoias.sgc.privacidade.dominio;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Pedido do titular (RF-30): acesso, correção ou exclusão, com protocolo, data, hora e situação. O titular e quem
 * concluiu ficam só como id de usuário.
 */
@Entity
@Table(name = "solicitacao_titular")
public class SolicitacaoTitular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private String protocolo;

    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipo;

    private String descricao;
    private String dadoIncorreto;
    private String valorCorreto;

    @Enumerated(EnumType.STRING)
    private SituacaoSolicitacao situacao = SituacaoSolicitacao.EM_ANALISE;

    private String justificativa;
    private OffsetDateTime criadaEm;
    private OffsetDateTime concluidaEm;
    private Long concluidaPor;

    protected SolicitacaoTitular() {
    }

    public SolicitacaoTitular(Long usuarioId, TipoSolicitacao tipo, String descricao, String dadoIncorreto,
                              String valorCorreto, OffsetDateTime criadaEm) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dadoIncorreto = dadoIncorreto;
        this.valorCorreto = valorCorreto;
        this.criadaEm = criadaEm;
        // Provisório e único: o protocolo definitivo usa o id gerado pelo banco (ver definirProtocolo).
        this.protocolo = "TMP-" + UUID.randomUUID().toString().substring(0, 16);
    }

    /** Protocolo definitivo no formato LGPD-AAAA-NNNNNN, a partir do id gerado. */
    public void definirProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    /** Registra a decisão do RH/encarregado (UC-05, 10a). Só uma solicitação em análise pode ser concluída. */
    public void concluir(SituacaoSolicitacao decisao, String justificativa, Long concluidaPor, OffsetDateTime quando) {
        if (situacao.concluida()) {
            throw new RegraDeNegocioException("Esta solicitação já foi concluída.");
        }
        if (decisao == null || !decisao.concluida()) {
            throw new RegraDeNegocioException("Escolha a decisão: atendida, atendida em parte ou negada.");
        }
        this.situacao = decisao;
        this.justificativa = justificativa;
        this.concluidaPor = concluidaPor;
        this.concluidaEm = quando;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getProtocolo() { return protocolo; }
    public TipoSolicitacao getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public String getDadoIncorreto() { return dadoIncorreto; }
    public String getValorCorreto() { return valorCorreto; }
    public SituacaoSolicitacao getSituacao() { return situacao; }
    public String getJustificativa() { return justificativa; }
    public OffsetDateTime getCriadaEm() { return criadaEm; }
    public OffsetDateTime getConcluidaEm() { return concluidaEm; }
    public Long getConcluidaPor() { return concluidaPor; }
}
