package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** "Hoje" e datas mostradas na tela, no horário de Brasília. */
final class Calendario {

    static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private Calendario() {
    }

    static LocalDate hoje() {
        return LocalDate.now(ZONA);
    }

    static OffsetDateTime agora() {
        return OffsetDateTime.now(ZONA);
    }

    static String data(LocalDate data) {
        return data == null ? "—" : data.format(DATA);
    }

    static String dataHora(OffsetDateTime quando) {
        return quando == null ? "—" : quando.atZoneSameInstant(ZONA).format(DATA_HORA);
    }
}
