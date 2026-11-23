package br.edu.pucgoias.sgc.ciclos.servico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Rotina agendada (UC-02 passo 14, RF-09, RF-12): a cada 5 minutos, e logo depois de a aplicação subir, abre os
 * ciclos agendados cuja data chegou (com os convites), encerra os abertos cujo prazo passou, tenta de novo os
 * convites que falharam (14a) e manda os lembretes automáticos (3 dias antes do encerramento e no último dia).
 * Cada ciclo é tratado na sua própria transação: a falha de um não impede os outros.
 */
@Component
public class RotinaDosCiclos {

    private static final Logger log = LoggerFactory.getLogger(RotinaDosCiclos.class);

    private final CicloService ciclos;

    public RotinaDosCiclos(CicloService ciclos) {
        this.ciclos = ciclos;
    }

    @Scheduled(initialDelayString = "${sgc.rotina-ciclos.atraso-inicial:PT20S}",
            fixedDelayString = "${sgc.rotina-ciclos.intervalo:PT5M}")
    public void executar() {
        for (Long id : ciclos.paraAbrir()) {
            tentar("abrir", id, () -> {
                var r = ciclos.abrir(id, false);
                log.info("Ciclo {} aberto pela rotina: {} convites enviados, {} falhas", id, r.enviados(), r.falhas());
            });
        }
        for (Long id : ciclos.paraEncerrar()) {
            tentar("encerrar", id, () -> {
                ciclos.encerrar(id, false);
                log.info("Ciclo {} encerrado pela rotina", id);
            });
        }
        for (Long id : ciclos.idsAbertos()) {
            tentar("reenviar convites", id, () -> ciclos.reenviarConvites(id));
            tentar("lembrar", id, () -> {
                var r = ciclos.lembreteAutomatico(id);
                if (r.enviados() > 0) {
                    log.info("Ciclo {}: lembrete automático a {} avaliadores", id, r.enviados());
                }
            });
        }
    }

    private static void tentar(String acao, Long cicloId, Runnable passo) {
        try {
            passo.run();
        } catch (RuntimeException e) {
            log.warn("Rotina dos ciclos: não foi possível {} o ciclo {}: {}", acao, cicloId, e.getMessage());
        }
    }
}
