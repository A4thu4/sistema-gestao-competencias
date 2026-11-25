package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.privacidade.dominio.TermoConsentimento;

import java.time.OffsetDateTime;
import java.util.List;

/** Versão do termo de consentimento para as telas e para os outros pacotes. */
public record TermoResumo(Long id, String versao, String texto, String resumoMudancas, OffsetDateTime vigenteDesde) {

    static TermoResumo de(TermoConsentimento t) {
        return new TermoResumo(t.getId(), t.getVersao(), t.getTexto(), t.getResumoMudancas(), t.getVigenteDesde());
    }

    public List<BlocoTexto> blocos() { return BlocoTexto.separar(texto); }
    public List<BlocoTexto> blocosMudancas() { return BlocoTexto.separar(resumoMudancas); }
    public boolean temMudancas() { return resumoMudancas != null && !resumoMudancas.isBlank(); }
    public String vigenteDesdeTexto() { return Datas.dataHora(vigenteDesde); }
}
