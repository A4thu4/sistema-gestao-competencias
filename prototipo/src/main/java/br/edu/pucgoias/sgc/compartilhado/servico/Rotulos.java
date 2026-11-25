package br.edu.pucgoias.sgc.compartilhado.servico;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/** Nomes de tela para os códigos de ação da auditoria e de tipo de e-mail. */
@Component("rotulos")
public class Rotulos {

    private static final Map<String, String> ACOES = Map.ofEntries(
            Map.entry("RESULTADO_ACESSADO", "Abriu resultado"),
            Map.entry("RESULTADO_EXPORTADO", "Exportou resultado"),
            Map.entry("RELATORIO_ACESSADO", "Abriu relatório"),
            Map.entry("RESULTADO_LIBERADO", "Liberou resultado"),
            Map.entry("RESULTADO_CONSOLIDADO", "Consolidou resultado"),
            Map.entry("RESULTADO_RECALCULADO", "Recalculou resultado"),
            Map.entry("AVALIACAO_REABERTA", "Reabriu avaliação"),
            Map.entry("COMENTARIO_OCULTADO", "Ocultou comentário"),
            Map.entry("COMENTARIO_EXIBIDO", "Exibiu comentário"),
            Map.entry("QUESTIONARIO_PUBLICADO", "Publicou questionário"),
            Map.entry("COMPETENCIA_DESATIVADA", "Desativou competência"),
            Map.entry("COMPETENCIA_REATIVADA", "Reativou competência"),
            Map.entry("CICLO_CRIADO", "Criou ciclo"),
            Map.entry("CICLO_ALTERADO", "Alterou ciclo"),
            Map.entry("CICLO_AGENDADO", "Agendou ciclo"),
            Map.entry("CICLO_ABERTO", "Abriu ciclo"),
            Map.entry("CICLO_ENCERRADO", "Encerrou ciclo"),
            Map.entry("LEMBRETE_ENVIADO", "Enviou lembrete"),
            Map.entry("LEMBRETE_METAS", "Lembrete de metas"),
            Map.entry("META_CRIADA", "Criou meta"),
            Map.entry("OBSERVACAO_REGISTRADA", "Registrou observação"),
            Map.entry("OBSERVACAO_ALTERADA", "Alterou observação"),
            Map.entry("OBSERVACAO_EXCLUIDA", "Excluiu observação"),
            Map.entry("ACEITE_TERMO", "Aceitou o termo"),
            Map.entry("RECUSA_TERMO", "Recusou o termo"),
            Map.entry("PUBLICACAO_TERMO", "Publicou termo"),
            Map.entry("SOLICITACAO_TITULAR", "Pedido LGPD"),
            Map.entry("CONCLUSAO_SOLICITACAO_TITULAR", "Respondeu pedido LGPD"));

    private static final Map<String, String> TIPOS_EMAIL = Map.of(
            "CONVITE", "Convite",
            "LEMBRETE", "Lembrete",
            "LEMBRETE_METAS", "Lembrete de metas",
            "RESULTADO_LIBERADO", "Resultado liberado",
            "REABERTURA", "Reabertura",
            "PLANO_META", "Nova meta",
            "TERMO", "Termo",
            "LGPD", "Pedido LGPD");

    public String acao(String codigo) {
        return codigo == null ? "" : ACOES.getOrDefault(codigo, legivel(codigo));
    }

    public String tipoEmail(String codigo) {
        return codigo == null ? "" : TIPOS_EMAIL.getOrDefault(codigo, legivel(codigo));
    }

    static String legivel(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return "";
        }
        String texto = codigo.replace('_', ' ').toLowerCase(new Locale("pt", "BR"));
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
