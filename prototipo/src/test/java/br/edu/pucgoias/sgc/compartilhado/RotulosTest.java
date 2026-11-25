package br.edu.pucgoias.sgc.compartilhado;

import br.edu.pucgoias.sgc.compartilhado.servico.Rotulos;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RotulosTest {

    private final Rotulos rotulos = new Rotulos();

    @Test
    void acaoConhecidaViraTextoDeTela() {
        assertThat(rotulos.acao("RESULTADO_ACESSADO")).isEqualTo("Abriu resultado");
        assertThat(rotulos.tipoEmail("RESULTADO_LIBERADO")).isEqualTo("Resultado liberado");
    }

    @Test
    void codigoDesconhecidoNaoApareceEmMaiusculas() {
        assertThat(rotulos.acao("ALGO_NOVO")).isEqualTo("Algo novo");
        assertThat(rotulos.tipoEmail(null)).isEmpty();
    }
}
