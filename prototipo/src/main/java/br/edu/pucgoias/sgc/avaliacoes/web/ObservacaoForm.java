package br.edu.pucgoias.sgc.avaliacoes.web;

import br.edu.pucgoias.sgc.avaliacoes.servico.DadosObservacao;
import br.edu.pucgoias.sgc.avaliacoes.servico.ObservacaoResumo;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ObservacaoForm {

    private Long colaboradorId;
    private String tipo;
    private String descricao;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFato;

    static ObservacaoForm nova(Long colaboradorId, LocalDate hoje) {
        ObservacaoForm f = new ObservacaoForm();
        f.colaboradorId = colaboradorId;
        f.dataFato = hoje;
        return f;
    }

    static ObservacaoForm de(ObservacaoResumo o) {
        ObservacaoForm f = new ObservacaoForm();
        f.colaboradorId = o.colaboradorId();
        f.tipo = o.tipo();
        f.descricao = o.descricao();
        f.dataFato = o.dataFato();
        return f;
    }

    DadosObservacao paraDados() {
        return new DadosObservacao(colaboradorId, tipo, descricao, dataFato);
    }

    public Long getColaboradorId() { return colaboradorId; }
    public void setColaboradorId(Long colaboradorId) { this.colaboradorId = colaboradorId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataFato() { return dataFato; }
    public void setDataFato(LocalDate dataFato) { this.dataFato = dataFato; }
}
