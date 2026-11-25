package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

/** Uma avaliação no painel de andamento: quem é avaliado, a relação e a situação. Nada da resposta (RNF-01). */
public record ItemAndamento(Long designacaoId, Long avaliadoId, String avaliadoNome, TipoAvaliador relacao,
                            String situacao) {

    public boolean enviada() {
        return "ENVIADA".equals(situacao);
    }

    public String situacaoRotulo() {
        return switch (situacao) {
            case "ENVIADA" -> "Enviada";
            case "EM_PREENCHIMENTO" -> "Em preenchimento";
            default -> "Não iniciada";
        };
    }
}
