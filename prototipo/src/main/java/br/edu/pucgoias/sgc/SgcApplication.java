package br.edu.pucgoias.sgc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Sistema de Gestão de Competências. As rotinas agendadas abrem e encerram ciclos e enviam lembretes. */
@SpringBootApplication
@EnableScheduling
public class SgcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgcApplication.class, args);
    }
}
