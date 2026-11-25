package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.time.LocalDate;
import java.util.Set;

public record DadosCiclo(String nome, Set<TipoAvaliador> tipos, LocalDate abertura, LocalDate encerramento) {

    public DadosCiclo {
        tipos = tipos == null ? Set.of() : Set.copyOf(tipos);
    }
}
