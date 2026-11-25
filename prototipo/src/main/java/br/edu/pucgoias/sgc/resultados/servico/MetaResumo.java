package br.edu.pucgoias.sgc.resultados.servico;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Meta do plano de desenvolvimento com a linha do tempo das atualizações (mais recente primeiro). {@code situacao}
 * é "NAO_INICIADA", "EM_ANDAMENTO", "CONCLUIDA" ou "CANCELADA".
 */
public record MetaResumo(Long id, Long colaboradorId, Long competenciaId, String competencia, String descricao,
                         String acoes, LocalDate prazo, String situacao, String situacaoRotulo, String cicloNome,
                         OffsetDateTime criadaEm, List<AtualizacaoMeta> historico) {

    public boolean aberta() {
        return "NAO_INICIADA".equals(situacao) || "EM_ANDAMENTO".equals(situacao);
    }

    public boolean atrasada() {
        return aberta() && prazo.isBefore(Formatos.hoje());
    }

    public String prazoTexto() {
        return Formatos.data(prazo);
    }

    public String criadaEmTexto() {
        return Formatos.dataHora(criadaEm);
    }

    /** Data da última atividade (criação ou atualização). */
    public OffsetDateTime ultimaAtividade() {
        return historico.isEmpty() ? criadaEm : historico.get(0).criadaEm();
    }

    public String ultimaAtividadeTexto() {
        return Formatos.dataHora(ultimaAtividade());
    }
}
