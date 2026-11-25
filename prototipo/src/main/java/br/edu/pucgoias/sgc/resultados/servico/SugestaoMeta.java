package br.edu.pucgoias.sgc.resultados.servico;

import java.math.BigDecimal;

/** Competência de menor nota final no último resultado liberado, sugerida para uma meta (UC-06). */
public record SugestaoMeta(Long competenciaId, String competencia, BigDecimal notaFinal) {

    public String notaTexto() {
        return Formatos.nota(notaFinal);
    }
}
