package br.edu.pucgoias.sgc.competencias.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Pergunta de uma competência do questionário. As marcações {@code para*} dizem a quais tipos de avaliador ela
 * aparece: chefe, autoavaliação, par, subordinado e externo.
 */
@Entity
@Table(name = "pergunta")
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionario_id")
    private Questionario questionario;

    private Long competenciaId;
    private String texto;

    @Enumerated(EnumType.STRING)
    private TipoPergunta tipo;

    private boolean obrigatoria;
    private short ordem;
    private boolean paraChefe;
    private boolean paraAuto;
    private boolean paraPar;
    private boolean paraSubordinado;
    private boolean paraExterno;

    protected Pergunta() {
    }

    Pergunta(Questionario questionario, Long competenciaId) {
        this.questionario = questionario;
        this.competenciaId = competenciaId;
    }

    /** Altera o conteúdo da pergunta. A ordem é a posição dela dentro da competência (1, 2, 3...). */
    public void definir(Long competenciaId, String texto, TipoPergunta tipo, boolean obrigatoria, int ordem,
                        boolean chefe, boolean auto, boolean par, boolean subordinado, boolean externo) {
        this.competenciaId = competenciaId;
        this.texto = texto;
        this.tipo = tipo;
        this.obrigatoria = obrigatoria;
        this.ordem = (short) ordem;
        this.paraChefe = chefe;
        this.paraAuto = auto;
        this.paraPar = par;
        this.paraSubordinado = subordinado;
        this.paraExterno = externo;
    }

    Pergunta copiarPara(Questionario outro) {
        Pergunta p = new Pergunta(outro, competenciaId);
        p.definir(competenciaId, texto, tipo, obrigatoria, ordem, paraChefe, paraAuto, paraPar, paraSubordinado, paraExterno);
        return p;
    }

    public boolean seAplicaAAlguem() {
        return paraChefe || paraAuto || paraPar || paraSubordinado || paraExterno;
    }

    public Long getId() { return id; }
    public Long getCompetenciaId() { return competenciaId; }
    public String getTexto() { return texto; }
    public TipoPergunta getTipo() { return tipo; }
    public boolean isObrigatoria() { return obrigatoria; }
    public int getOrdem() { return ordem; }
    public boolean isParaChefe() { return paraChefe; }
    public boolean isParaAuto() { return paraAuto; }
    public boolean isParaPar() { return paraPar; }
    public boolean isParaSubordinado() { return paraSubordinado; }
    public boolean isParaExterno() { return paraExterno; }
}
