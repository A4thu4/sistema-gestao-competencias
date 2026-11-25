package br.edu.pucgoias.sgc.resultados.web;

import br.edu.pucgoias.sgc.resultados.servico.DadosMeta;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class MetaForm {

    private Long competenciaId;
    private String descricao;
    private String acoes;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate prazo;
    private String situacao = "NAO_INICIADA";

    public DadosMeta paraDados() {
        return new DadosMeta(competenciaId, descricao, acoes, prazo, situacao);
    }

    public Long getCompetenciaId() { return competenciaId; }
    public void setCompetenciaId(Long competenciaId) { this.competenciaId = competenciaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getAcoes() { return acoes; }
    public void setAcoes(String acoes) { this.acoes = acoes; }
    public LocalDate getPrazo() { return prazo; }
    public void setPrazo(LocalDate prazo) { this.prazo = prazo; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
