package br.edu.pucgoias.sgc.ciclos.servico;

/**
 * Publicado quando um ciclo é encerrado (pela rotina ou por "Encerrar agora"), de forma síncrona e na mesma
 * transação. O pacote resultados escuta com {@code @EventListener} para consolidar as médias (RF-19).
 */
public record CicloEncerradoEvento(Long cicloId) {
}
