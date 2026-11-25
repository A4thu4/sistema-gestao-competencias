package br.edu.pucgoias.sgc.privacidade.servico;

import java.util.ArrayList;
import java.util.List;

/**
 * Trecho do termo pronto para a tela: título de seção, parágrafo ou lista. O texto do termo é guardado com
 * marcações simples: linha que começa com "## " é título, linha que começa com "- " é item de lista e linha em
 * branco separa parágrafos. Assim o RH escreve sem HTML e a tela nunca interpreta o texto como código.
 */
public record BlocoTexto(String tipo, String texto, List<String> itens) {

    public static final String TITULO = "titulo";
    public static final String PARAGRAFO = "paragrafo";
    public static final String LISTA = "lista";

    public static List<BlocoTexto> separar(String texto) {
        List<BlocoTexto> blocos = new ArrayList<>();
        if (texto == null) {
            return blocos;
        }
        StringBuilder paragrafo = new StringBuilder();
        List<String> itens = new ArrayList<>();
        for (String bruta : texto.replace("\r", "").split("\n")) {
            String linha = bruta.strip();
            boolean item = linha.startsWith("- ");
            boolean titulo = linha.startsWith("## ");
            if (linha.isEmpty() || item || titulo) {
                fecharParagrafo(blocos, paragrafo);
            }
            if (!item) {
                fecharLista(blocos, itens);
            }
            if (titulo) {
                blocos.add(new BlocoTexto(TITULO, linha.substring(3).strip(), List.of()));
            } else if (item) {
                itens.add(linha.substring(2).strip());
            } else if (!linha.isEmpty()) {
                if (!paragrafo.isEmpty()) {
                    paragrafo.append(' ');
                }
                paragrafo.append(linha);
            }
        }
        fecharParagrafo(blocos, paragrafo);
        fecharLista(blocos, itens);
        return blocos;
    }

    private static void fecharParagrafo(List<BlocoTexto> blocos, StringBuilder paragrafo) {
        if (!paragrafo.isEmpty()) {
            blocos.add(new BlocoTexto(PARAGRAFO, paragrafo.toString(), List.of()));
            paragrafo.setLength(0);
        }
    }

    private static void fecharLista(List<BlocoTexto> blocos, List<String> itens) {
        if (!itens.isEmpty()) {
            blocos.add(new BlocoTexto(LISTA, null, List.copyOf(itens)));
            itens.clear();
        }
    }

    public boolean ehTitulo() { return TITULO.equals(tipo); }
    public boolean ehParagrafo() { return PARAGRAFO.equals(tipo); }
    public boolean ehLista() { return LISTA.equals(tipo); }
}
