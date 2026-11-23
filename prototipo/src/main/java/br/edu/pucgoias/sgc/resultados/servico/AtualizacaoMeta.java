package br.edu.pucgoias.sgc.resultados.servico;

import java.time.OffsetDateTime;

/** Item da linha do tempo de uma meta: quem, quando, situação escolhida e progresso relatado (RF-28). */
public record AtualizacaoMeta(String autor, String situacao, String situacaoRotulo, String progresso,
                              OffsetDateTime criadaEm) {

    public String quando() {
        return Formatos.dataHora(criadaEm);
    }
}
