package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;

import java.util.List;

/** Extensão 10b (e 8a, 8b): o ciclo não pode ser agendado enquanto houver pendências. */
public class CicloComPendenciasException extends RegraDeNegocioException {

    private final transient List<String> pendencias;

    public CicloComPendenciasException(List<String> pendencias) {
        super("O ciclo ainda tem " + pendencias.size() + (pendencias.size() == 1 ? " pendência" : " pendências")
                + " e continua como rascunho. Resolva o que está listado no resumo e tente de novo.");
        this.pendencias = List.copyOf(pendencias);
    }

    public List<String> pendencias() {
        return pendencias;
    }
}
