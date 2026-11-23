package br.edu.pucgoias.sgc.competencias.web;

import br.edu.pucgoias.sgc.competencias.servico.DadosPergunta;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.EnumSet;

/** Uma linha de pergunta do editor. Os nomes chefe, auto, par, subordinado e externo seguem {@link TipoAvaliador}. */
public class PerguntaForm {

    private Long id;
    private Long competenciaId;
    private String texto;
    private String tipo = "ESCALA";
    private boolean obrigatoria;
    private boolean chefe;
    private boolean auto;
    private boolean par;
    private boolean subordinado;
    private boolean externo;

    DadosPergunta paraDados() {
        EnumSet<TipoAvaliador> av = EnumSet.noneOf(TipoAvaliador.class);
        if (chefe) av.add(TipoAvaliador.CHEFE);
        if (auto) av.add(TipoAvaliador.AUTO);
        if (par) av.add(TipoAvaliador.PAR);
        if (subordinado) av.add(TipoAvaliador.SUBORDINADO);
        if (externo) av.add(TipoAvaliador.EXTERNO);
        return new DadosPergunta(id, competenciaId, texto, tipo, obrigatoria, av);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompetenciaId() { return competenciaId; }
    public void setCompetenciaId(Long competenciaId) { this.competenciaId = competenciaId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isObrigatoria() { return obrigatoria; }
    public void setObrigatoria(boolean obrigatoria) { this.obrigatoria = obrigatoria; }
    public boolean isChefe() { return chefe; }
    public void setChefe(boolean chefe) { this.chefe = chefe; }
    public boolean isAuto() { return auto; }
    public void setAuto(boolean auto) { this.auto = auto; }
    public boolean isPar() { return par; }
    public void setPar(boolean par) { this.par = par; }
    public boolean isSubordinado() { return subordinado; }
    public void setSubordinado(boolean subordinado) { this.subordinado = subordinado; }
    public boolean isExterno() { return externo; }
    public void setExterno(boolean externo) { this.externo = externo; }
}
