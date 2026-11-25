package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

/** Pessoa que o usuário pode consultar no ciclo escolhido. {@code propria} = o próprio usuário. */
public record PessoaComResultado(PessoaResumo pessoa, boolean propria, boolean aguardandoLiberacao) {
}
