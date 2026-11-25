package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.util.Optional;

/**
 * Inversão de dependência: o gestor vê o resultado do ciclo anterior do avaliado enquanto o avalia, mas quem calcula
 * resultados é o pacote resultados, que depende deste. Sem implementação, a tela mostra "sem resultado anterior".
 */
public interface ResultadoAnterior {

    /**
     * Resultado consolidado mais recente do avaliado num ciclo encerrado anterior a {@code cicloAtualId}, ou vazio
     * quando não há. Só médias agregadas, nunca respostas individuais.
     */
    Optional<ResumoResultadoAnterior> doAvaliado(Long avaliadoId, Long cicloAtualId);
}
