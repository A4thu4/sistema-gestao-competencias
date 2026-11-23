package br.edu.pucgoias.sgc.avaliacoes.servico;

/**
 * Resposta de uma pergunta, como o avaliador informou: nota (escala), "não tenho como avaliar" (RF-06) ou texto
 * (pergunta aberta).
 */
public record RespostaInformada(Integer nota, boolean naoAvalia, String texto) {

    public static final RespostaInformada VAZIA = new RespostaInformada(null, false, null);

    public boolean vazia() {
        return nota == null && !naoAvalia && (texto == null || texto.isBlank());
    }
}
