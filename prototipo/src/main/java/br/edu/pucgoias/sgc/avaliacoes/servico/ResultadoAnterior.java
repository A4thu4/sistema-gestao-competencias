package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.util.Optional;

/**
 * Inversão de dependência (RF-18, UC-03 passo 4): o gestor vê o resultado do ciclo anterior do avaliado enquanto o
 * avalia, mas quem calcula resultados é o pacote resultados, que depende deste. O resultados implementa esta
 * interface com um {@code @Component}; sem implementação, a tela mostra "sem resultado anterior" (extensão 4b).
 */
public interface ResultadoAnterior {

    /**
     * Resultado consolidado mais recente do avaliado num ciclo encerrado anterior a {@code cicloAtualId}, ou vazio
     * quando não há (4b). Só médias agregadas, nunca respostas individuais (RNF-01).
     */
    Optional<ResumoResultadoAnterior> doAvaliado(Long avaliadoId, Long cicloAtualId);
}
