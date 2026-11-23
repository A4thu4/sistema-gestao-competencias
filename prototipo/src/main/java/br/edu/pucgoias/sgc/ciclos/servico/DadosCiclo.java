package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.time.LocalDate;
import java.util.Set;

/** O que o RH informa nos passos 4 e 5 do UC-02: nome, tipos de avaliação e período. */
public record DadosCiclo(String nome, Set<TipoAvaliador> tipos, LocalDate abertura, LocalDate encerramento) {

    public DadosCiclo {
        tipos = tipos == null ? Set.of() : Set.copyOf(tipos);
    }
}
