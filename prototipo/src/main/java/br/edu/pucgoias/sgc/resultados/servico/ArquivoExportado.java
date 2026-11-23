package br.edu.pucgoias.sgc.resultados.servico;

/** Arquivo gerado para download (RF-23). */
public record ArquivoExportado(String nome, String tipoConteudo, byte[] conteudo) {

    public static final String PDF = "application/pdf";
    public static final String EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
}
