package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.math.BigDecimal;

/**
 * Nota de um grupo numa competência, já com a regra do mínimo de 3 aplicada: quando {@code insuficiente}, a média
 * não vem (é {@code null}) e nunca chega à tela, ao gráfico ou ao arquivo (RNF-02). Sem respostas do grupo, os dois
 * ficam vazios.
 */
public record NotaGrupo(TipoAvaliador grupo, BigDecimal media, boolean insuficiente) {

    public static NotaGrupo semRespostas(TipoAvaliador grupo) {
        return new NotaGrupo(grupo, null, false);
    }

    public String texto() {
        return Formatos.nota(media);
    }

    /** Dica da célula vazia. */
    public String dica() {
        if (insuficiente) {
            return "Menos de " + RegrasDeExibicao.MINIMO_RESPOSTAS + " respostas: " + RegrasDeExibicao.AVISO_ANONIMATO;
        }
        return media == null ? "Sem respostas deste grupo" : null;
    }
}
