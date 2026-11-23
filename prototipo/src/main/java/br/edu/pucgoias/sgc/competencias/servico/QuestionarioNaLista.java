package br.edu.pucgoias.sgc.competencias.servico;

import java.util.List;

/** Linha da lista de questionários do RH (UC-01, passo 2). */
public record QuestionarioNaLista(Long id, String nome, String cargoNome, String situacao, int competencias,
                                  int perguntas, String origemNome, List<String> ciclos, boolean bloqueado) {

    public boolean publicado() {
        return "PUBLICADO".equals(situacao);
    }

    public String situacaoRotulo() {
        return publicado() ? "Publicado" : "Rascunho";
    }
}
