package br.edu.pucgoias.sgc.privacidade.servico;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Cópia dos dados pessoais do titular (UC-05, 7a): cadastro, aceites do termo, solicitações e os dados que os
 * outros pacotes guardam sobre ele. Nunca traz a autoria de respostas de pares e subordinados (RNF-01).
 */
public record CopiaDados(String titular, OffsetDateTime geradaEm, List<ItemDado> dadosCadastrais,
                         List<AceiteResumo> aceites, List<SolicitacaoResumo> solicitacoes,
                         List<SecaoDados> outrasSecoes) {

    public String geradaEmTexto() { return Datas.dataHora(geradaEm); }
}
