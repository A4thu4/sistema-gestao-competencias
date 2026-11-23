package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Uma competência no painel individual: a nota de cada grupo (já com o mínimo de 3) e a nota final.
 * {@code maior} e {@code menor} marcam o ponto forte (verde) e o foco de desenvolvimento (laranja), como na TL-04.
 */
public record LinhaCompetencia(Long competenciaId, String competencia, Map<TipoAvaliador, NotaGrupo> grupos,
                               BigDecimal notaFinal, boolean maior, boolean menor) {

    public NotaGrupo grupo(TipoAvaliador g) {
        return grupos.getOrDefault(g, NotaGrupo.semRespostas(g));
    }

    public String notaFinalTexto() {
        return Formatos.nota(notaFinal);
    }

    /** Classe CSS da nota final. */
    public String corNotaFinal() {
        if (maior) {
            return "nota-maior";
        }
        return menor ? "nota-menor" : "nota-normal";
    }
}
