package br.edu.pucgoias.sgc.competencias.web;

import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.DadosCompetencia;

public class CompetenciaForm {

    private String nome;
    private String descricao;
    private String tipo = "COMPORTAMENTAL";

    static CompetenciaForm de(CompetenciaResumo c) {
        CompetenciaForm f = new CompetenciaForm();
        f.nome = c.nome();
        f.descricao = c.descricao();
        f.tipo = c.tipo();
        return f;
    }

    DadosCompetencia paraDados() {
        return new DadosCompetencia(nome, descricao, tipo);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
