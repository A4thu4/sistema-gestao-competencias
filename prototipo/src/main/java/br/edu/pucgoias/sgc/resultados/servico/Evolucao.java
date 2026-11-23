package br.edu.pucgoias.sgc.resultados.servico;

import java.math.BigDecimal;
import java.util.List;

/**
 * Evolução da nota final por competência nos ciclos encerrados da pessoa (RF-26), do mais antigo para o mais
 * recente. Quando os ciclos usaram escalas diferentes, os valores vêm em percentual da escala
 * ({@code normalizada}).
 */
public record Evolucao(List<String> ciclos, List<SerieGrafico> series, boolean normalizada) {

    /** Extensão 6a: só um ciclo, nada para comparar. */
    public boolean temHistorico() {
        return ciclos.size() > 1;
    }

    public String unidade() {
        return normalizada ? "% da escala" : "nota";
    }

    public String legenda() {
        return normalizada
                ? "Os ciclos usaram escalas diferentes; as notas aparecem em percentual da nota máxima de cada ciclo."
                : "Nota final de cada competência em cada ciclo encerrado.";
    }

    public static String texto(BigDecimal valor, boolean normalizada) {
        if (valor == null) {
            return Formatos.SEM_NOTA;
        }
        return normalizada ? valor.toPlainString().replace('.', ',') + "%" : Formatos.nota(valor);
    }

    public String texto(BigDecimal valor) {
        return texto(valor, normalizada);
    }
}
