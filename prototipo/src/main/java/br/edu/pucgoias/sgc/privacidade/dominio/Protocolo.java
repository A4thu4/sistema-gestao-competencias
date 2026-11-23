package br.edu.pucgoias.sgc.privacidade.dominio;

/** Número de protocolo das solicitações do titular: LGPD-AAAA-NNNNNN (ano e número sequencial com seis dígitos). */
public final class Protocolo {

    private Protocolo() {
    }

    public static String formatar(int ano, long numero) {
        if (numero < 1 || numero > 999_999) {
            throw new IllegalArgumentException("número de protocolo fora da faixa: " + numero);
        }
        return String.format("LGPD-%04d-%06d", ano, numero);
    }
}
