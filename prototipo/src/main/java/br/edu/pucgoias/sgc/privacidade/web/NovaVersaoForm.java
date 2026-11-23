package br.edu.pucgoias.sgc.privacidade.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Formulário do RH para publicar uma nova versão do termo. */
public class NovaVersaoForm {

    @NotBlank(message = "Informe o número da versão, por exemplo 1.1.")
    @Size(max = 20, message = "Use no máximo 20 caracteres.")
    private String versao;

    @NotBlank(message = "Escreva o texto do termo.")
    private String texto;

    @Size(max = 1000, message = "Use no máximo 1000 caracteres.")
    private String resumoMudancas;

    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getResumoMudancas() { return resumoMudancas; }
    public void setResumoMudancas(String resumoMudancas) { this.resumoMudancas = resumoMudancas; }
}
