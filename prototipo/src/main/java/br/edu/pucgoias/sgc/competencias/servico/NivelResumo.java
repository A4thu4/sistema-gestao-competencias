package br.edu.pucgoias.sgc.competencias.servico;

/** Nível da escala com a sua descrição (RF-04, RF-25). */
public record NivelResumo(int valor, String descricao) {

    /** Parte da descrição antes dos dois-pontos ("Sempre: ..." vira "Sempre"), para rótulos curtos. */
    public String rotuloCurto() {
        int i = descricao == null ? -1 : descricao.indexOf(':');
        return i > 0 ? descricao.substring(0, i) : descricao;
    }
}
