package br.edu.pucgoias.sgc.ciclos.servico;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** "Hoje" e formatação de datas no horário de Brasília, para abrir, encerrar e lembrar nos dias certos. */
public final class Datas {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private Datas() {
    }

    public static LocalDate hoje() {
        return LocalDate.now(ZONA);
    }

    public static String data(LocalDate d) {
        return d == null ? "—" : d.format(DATA);
    }

    public static String dataHora(OffsetDateTime quando) {
        return quando == null ? "—" : quando.atZoneSameInstant(ZONA).format(DATA_HORA);
    }

    public static boolean ehHoje(OffsetDateTime quando) {
        return quando != null && quando.atZoneSameInstant(ZONA).toLocalDate().equals(hoje());
    }
}
