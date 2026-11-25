package br.edu.pucgoias.sgc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/** Sistema de Gestão de Competências. As rotinas agendadas abrem e encerram ciclos e enviam lembretes. */
@SpringBootApplication
@EnableScheduling
public class SgcApplication {

    // Datas e horas no fuso do Brasil, inclusive quando o servidor ou o contêiner está em UTC
    // (abertura e encerramento dos ciclos dependem do "hoje" local).
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SgcApplication.class, args);
    }
}
