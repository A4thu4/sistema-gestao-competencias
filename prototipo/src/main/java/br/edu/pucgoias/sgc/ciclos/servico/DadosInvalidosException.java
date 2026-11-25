package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;

import java.util.Map;

/** Cada campo com problema e a mensagem em linguagem simples, para destacar no formulário. */
public class DadosInvalidosException extends RegraDeNegocioException {

    private final transient Map<String, String> erros;

    public DadosInvalidosException(Map<String, String> erros) {
        super("Confira os campos destacados: " + String.join(" ", erros.values()));
        this.erros = Map.copyOf(erros);
    }

    public Map<String, String> erros() {
        return erros;
    }
}
