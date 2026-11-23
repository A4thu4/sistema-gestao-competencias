package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.util.List;
import java.util.Map;

/**
 * Pessoa avaliada no ciclo com o questionário do cargo e os avaliadores por tipo (UC-02, passos 8 a 10).
 * {@code pendencias} impedem o agendamento (10b, 9a); {@code alertas} só avisam (10a).
 */
public record AvaliadoNoCiclo(Long id, String nome, String email, String cargoNome, String area, Long questionarioId,
                              String questionarioNome, boolean temEquipe,
                              Map<TipoAvaliador, List<AvaliadorNoCiclo>> porTipo,
                              List<String> pendencias, List<String> alertas) {

    public List<AvaliadorNoCiclo> avaliadores(TipoAvaliador tipo) {
        return porTipo.getOrDefault(tipo, List.of());
    }

    public List<AvaliadorNoCiclo> externos() {
        return avaliadores(TipoAvaliador.EXTERNO);
    }

    public boolean pendente() {
        return !pendencias.isEmpty();
    }

    public String iniciais() {
        String[] partes = nome.trim().split("\\s+");
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (partes[0].substring(0, 1) + ultima).toUpperCase();
    }
}
