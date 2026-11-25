package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;

/** Link inválido, avaliação já enviada ou ciclo encerrado. O questionário não é mostrado. */
public class LinkExternoIndisponivelException extends RegraDeNegocioException {

    private final String titulo;
    private final boolean jaEnviada;

    public LinkExternoIndisponivelException(String titulo, String mensagem, boolean jaEnviada) {
        super(mensagem);
        this.titulo = titulo;
        this.jaEnviada = jaEnviada;
    }

    public String titulo() {
        return titulo;
    }

    public boolean jaEnviada() {
        return jaEnviada;
    }
}
