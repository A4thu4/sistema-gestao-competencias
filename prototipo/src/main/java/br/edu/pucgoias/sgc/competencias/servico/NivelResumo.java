package br.edu.pucgoias.sgc.competencias.servico;

public record NivelResumo(int valor, String descricao) {

    /** Parte da descrição antes dos dois-pontos ("Sempre: ..." vira "Sempre"), para rótulos curtos. */
    public String rotuloCurto() {
        int i = descricao == null ? -1 : descricao.indexOf(':');
        return i > 0 ? descricao.substring(0, i) : descricao;
    }
}
