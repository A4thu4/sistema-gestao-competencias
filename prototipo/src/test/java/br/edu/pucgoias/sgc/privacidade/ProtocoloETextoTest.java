package br.edu.pucgoias.sgc.privacidade;

import br.edu.pucgoias.sgc.privacidade.dominio.Protocolo;
import br.edu.pucgoias.sgc.privacidade.servico.BlocoTexto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Regras sem banco: formato do protocolo e leitura do texto do termo. */
class ProtocoloETextoTest {

    @Test
    void protocoloTemAnoESeisDigitos() {
        assertThat(Protocolo.formatar(2026, 7)).isEqualTo("LGPD-2026-000007");
        assertThat(Protocolo.formatar(2027, 123456)).isEqualTo("LGPD-2027-123456");
        assertThatThrownBy(() -> Protocolo.formatar(2026, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void textoDoTermoViraTitulosParagrafosEListas() {
        List<BlocoTexto> blocos = BlocoTexto.separar("Intro em\nduas linhas.\n\n## 1. Dados\n- Nome\n- Cargo\nFim.");
        assertThat(blocos).extracting(BlocoTexto::tipo)
                .containsExactly("paragrafo", "titulo", "lista", "paragrafo");
        assertThat(blocos.get(0).texto()).isEqualTo("Intro em duas linhas.");
        assertThat(blocos.get(1).texto()).isEqualTo("1. Dados");
        assertThat(blocos.get(2).itens()).containsExactly("Nome", "Cargo");
    }
}
