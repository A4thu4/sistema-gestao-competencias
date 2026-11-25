package br.edu.pucgoias.sgc.compartilhado.dominio;

/** O registro pedido não existe. Vira a página 404. */
public class NaoEncontradoException extends RuntimeException {

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
