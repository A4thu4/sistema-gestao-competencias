package br.edu.pucgoias.sgc.compartilhado.dominio;

/** Regra do caso de uso violada. A mensagem vai para a tela, então deve estar em linguagem simples (RNF-09). */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
