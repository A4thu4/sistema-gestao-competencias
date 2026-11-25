package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.privacidade.dominio.Aceite;

import java.time.OffsetDateTime;

public record AceiteResumo(Long termoId, String versao, OffsetDateTime aceitoEm) {

    static AceiteResumo de(Aceite a) {
        return new AceiteResumo(a.getTermo().getId(), a.getTermo().getVersao(), a.getAceitoEm());
    }

    public String dataHora() { return Datas.dataHora(aceitoEm); }
    public String data() { return Datas.data(aceitoEm); }
}
