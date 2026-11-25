package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.privacidade.dominio.SituacaoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.dominio.SolicitacaoTitular;
import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;

import java.time.OffsetDateTime;

public record SolicitacaoResumo(Long id, Long usuarioId, String protocolo, TipoSolicitacao tipo, String descricao,
                                String dadoIncorreto, String valorCorreto, SituacaoSolicitacao situacao,
                                String justificativa, OffsetDateTime criadaEm, OffsetDateTime concluidaEm,
                                Long concluidaPor) {

    static SolicitacaoResumo de(SolicitacaoTitular s) {
        return new SolicitacaoResumo(s.getId(), s.getUsuarioId(), s.getProtocolo(), s.getTipo(), s.getDescricao(),
                s.getDadoIncorreto(), s.getValorCorreto(), s.getSituacao(), s.getJustificativa(), s.getCriadaEm(),
                s.getConcluidaEm(), s.getConcluidaPor());
    }

    public boolean emAnalise() { return situacao == SituacaoSolicitacao.EM_ANALISE; }
    public String criadaEmTexto() { return Datas.dataHora(criadaEm); }
    public String concluidaEmTexto() { return Datas.dataHora(concluidaEm); }
    public String seloCss() { return "selo-" + situacao.name().toLowerCase(); }
}
