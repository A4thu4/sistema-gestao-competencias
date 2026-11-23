package br.edu.pucgoias.sgc.avaliacoes.servico;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

/**
 * Verificador da avaliação enviada (DA-06): HMAC-SHA256 do id da designação com a chave do servidor
 * ({@code sgc.chave-verificador}), em hexadecimal. Quem não tem a chave não consegue ligar a avaliação enviada à
 * designação; o RH usa só para reabrir a avaliação a pedido do avaliador (UC-09). Nunca vai para tela, URL ou log.
 */
@Component
class Verificador {

    private final SecretKeySpec chave;

    Verificador(@Value("${sgc.chave-verificador}") String chave) {
        this.chave = new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    String daDesignacao(Long designacaoId) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(chave);
            return HexFormat.of().formatHex(mac.doFinal(String.valueOf(designacaoId).getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível calcular o verificador.", e);
        }
    }
}
