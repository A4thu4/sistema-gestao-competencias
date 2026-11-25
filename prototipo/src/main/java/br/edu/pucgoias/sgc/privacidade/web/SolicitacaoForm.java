package br.edu.pucgoias.sgc.privacidade.web;

import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.servico.NovaSolicitacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SolicitacaoForm {

    @NotNull(message = "Escolha o tipo de solicitação.")
    private TipoSolicitacao tipo;

    @Size(max = 1000, message = "Use no máximo 1000 caracteres.")
    private String descricao;

    @Size(max = 200, message = "Use no máximo 200 caracteres.")
    private String dadoIncorreto;

    @Size(max = 200, message = "Use no máximo 200 caracteres.")
    private String valorCorreto;

    private boolean confirmado;

    NovaSolicitacao paraServico() {
        return new NovaSolicitacao(tipo, descricao, dadoIncorreto, valorCorreto, confirmado);
    }

    public TipoSolicitacao getTipo() { return tipo; }
    public void setTipo(TipoSolicitacao tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDadoIncorreto() { return dadoIncorreto; }
    public void setDadoIncorreto(String dadoIncorreto) { this.dadoIncorreto = dadoIncorreto; }
    public String getValorCorreto() { return valorCorreto; }
    public void setValorCorreto(String valorCorreto) { this.valorCorreto = valorCorreto; }
    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
}
