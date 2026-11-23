package br.edu.pucgoias.sgc.resultados.servico;

/**
 * O resultado pedido ainda não pode ser mostrado, por um motivo que o usuário pode entender (UC-04, 2b e 2c). A tela
 * mostra a mensagem sem nenhum dado do resultado.
 */
public class ResultadoIndisponivelException extends RuntimeException {

    private final String titulo;

    public ResultadoIndisponivelException(String titulo, String mensagem) {
        super(mensagem);
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }
}
