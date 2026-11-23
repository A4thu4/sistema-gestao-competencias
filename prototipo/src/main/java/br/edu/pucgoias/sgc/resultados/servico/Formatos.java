package br.edu.pucgoias.sgc.resultados.servico;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatação usada nas telas e nos arquivos exportados, para que os dois mostrem exatamente o mesmo (RF-23): notas
 * com uma casa decimal e vírgula, datas no horário de Brasília.
 */
public final class Formatos {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    public static final String SEM_NOTA = "—";
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private Formatos() {
    }

    public static LocalDate hoje() {
        return LocalDate.now(ZONA);
    }

    /** Nota com uma casa decimal e vírgula ("4,3"); sem nota, "—". */
    public static String nota(BigDecimal valor) {
        if (valor == null) {
            return SEM_NOTA;
        }
        return valor.setScale(1, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }

    public static String data(LocalDate d) {
        return d == null ? "—" : d.format(DATA);
    }

    public static String dataHora(OffsetDateTime quando) {
        return quando == null ? "—" : quando.atZoneSameInstant(ZONA).format(DATA_HORA);
    }

    /** Texto para nome de arquivo: sem acento, minúsculo, com hífens. */
    public static String paraArquivo(String texto) {
        String semAcento = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String limpo = semAcento.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return limpo.isEmpty() ? "resultado" : limpo;
    }
}
