package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.dominio.Observacao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Observação do gestor sobre um colaborador (UC-08), para as telas e para o UC-03 (passo 4). {@code tipo} é
 * "POSITIVO" ou "MELHORIA"; {@code dataFato} é o dia em que o fato aconteceu e {@code criadaEm}, quando foi registrado.
 */
public record ObservacaoResumo(Long id, Long colaboradorId, String tipo, String descricao, LocalDate dataFato,
                               OffsetDateTime criadaEm) {

    static ObservacaoResumo de(Observacao o) {
        return new ObservacaoResumo(o.getId(), o.getColaboradorId(), o.getTipo().name(), o.getDescricao(),
                o.getDataFato(), o.getCriadaEm());
    }

    public boolean positivo() {
        return TipoObservacao.POSITIVO.name().equals(tipo);
    }

    public String tipoRotulo() {
        return TipoObservacao.valueOf(tipo).getRotulo();
    }

    public String dataFatoTexto() {
        return Calendario.data(dataFato);
    }

    public String criadaEmTexto() {
        return Calendario.dataHora(criadaEm);
    }
}
