package br.edu.pucgoias.sgc.resultados.servico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Rotina diária do lembrete trimestral (RF-28): às 8h (horário de Brasília) procura metas abertas sem atualização
 * há {@value PlanoService#DIAS_LEMBRETE} dias e avisa a pessoa e o gestor por e-mail.
 */
@Component
public class RotinaLembreteMetas {

    private static final Logger log = LoggerFactory.getLogger(RotinaLembreteMetas.class);

    private final PlanoService planos;

    public RotinaLembreteMetas(PlanoService planos) {
        this.planos = planos;
    }

    @Scheduled(cron = "${sgc.lembrete-metas.cron:0 0 8 * * *}", zone = "America/Sao_Paulo")
    public void executar() {
        try {
            int lembradas = planos.enviarLembretesTrimestrais();
            if (lembradas > 0) {
                log.info("Lembrete trimestral do plano de desenvolvimento: {} metas lembradas", lembradas);
            }
        } catch (RuntimeException e) {
            log.warn("Lembrete trimestral do plano de desenvolvimento falhou: {}", e.getMessage());
        }
    }
}
