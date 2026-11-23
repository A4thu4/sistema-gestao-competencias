package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.UUID;

/**
 * Uma resposta de escala pronta para a consolidação, sem autor (RNF-01). {@code avaliacaoId} é o id aleatório da
 * avaliação enviada, usado só para contar quantas avaliações de cada grupo deram nota à competência.
 */
public record NotaParaConsolidar(UUID avaliacaoId, Long avaliadoId, TipoAvaliador grupo, Long competenciaId,
                                 Integer nota, boolean naoAvalia) {

    /** Entra na média: tem nota e não é "não tenho como avaliar" (RF-06). */
    public boolean contaNaMedia() {
        return !naoAvalia && nota != null;
    }
}
