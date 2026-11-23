package br.edu.pucgoias.sgc.privacidade.servico;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Datas e horas mostradas ao titular no horário de Brasília. */
public final class Datas {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Datas() {
    }

    public static String dataHora(OffsetDateTime quando) {
        return quando == null ? "—" : quando.atZoneSameInstant(ZONA).format(DATA_HORA);
    }

    public static String data(OffsetDateTime quando) {
        return quando == null ? "—" : quando.atZoneSameInstant(ZONA).format(DATA);
    }

    public static int anoAtual() {
        return OffsetDateTime.now(ZONA).getYear();
    }
}
