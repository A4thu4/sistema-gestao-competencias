package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

/** Pessoa que o usuário pode consultar no ciclo escolhido (UC-04, passo 3). {@code propria} = o próprio usuário. */
public record PessoaComResultado(PessoaResumo pessoa, boolean propria, boolean aguardandoLiberacao) {
}
