package br.edu.pucgoias.sgc.competencias.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;

/** Já existe competência com esse nome (UC-01, extensão 9b). Traz a existente, para a tela sugerir usá-la. */
public class CompetenciaDuplicadaException extends RegraDeNegocioException {

    private final CompetenciaResumo existente;

    public CompetenciaDuplicadaException(CompetenciaResumo existente) {
        super("Já existe a competência “" + existente.nome() + "” no catálogo"
                + (existente.ativa() ? "." : ", mas ela está desativada.")
                + (existente.ativa() ? " Use a existente em vez de cadastrar outra." : " Reative-a em vez de cadastrar outra."));
        this.existente = existente;
    }

    public CompetenciaResumo existente() {
        return existente;
    }
}
