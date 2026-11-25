package br.edu.pucgoias.sgc.resultados;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.resultados.servico.Formatos;
import br.edu.pucgoias.sgc.resultados.servico.LinhaCompetencia;
import br.edu.pucgoias.sgc.resultados.servico.LinhaConsolidada;
import br.edu.pucgoias.sgc.resultados.servico.MontagemPainel;
import br.edu.pucgoias.sgc.resultados.servico.PainelIndividual;
import br.edu.pucgoias.sgc.resultados.servico.RegrasDeExibicao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.AUTO;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.CHEFE;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.EXTERNO;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.PAR;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.SUBORDINADO;
import static org.assertj.core.api.Assertions.assertThat;

/** Mínimo de 3 (RNF-02), nota final, média geral, ponto forte e foco de desenvolvimento. */
class RegrasDeExibicaoTest {

    @Test
    void minimoDeTresValeSoParaParesSubordinadosEExternos() {
        for (int n = 0; n < 3; n++) {
            assertThat(RegrasDeExibicao.exibivel(PAR, n)).isFalse();
            assertThat(RegrasDeExibicao.exibivel(SUBORDINADO, n)).isFalse();
            assertThat(RegrasDeExibicao.exibivel(EXTERNO, n)).isFalse();
        }
        assertThat(RegrasDeExibicao.exibivel(PAR, 3)).isTrue();
        assertThat(RegrasDeExibicao.exibivel(SUBORDINADO, 3)).isTrue();
        assertThat(RegrasDeExibicao.exibivel(EXTERNO, 4)).isTrue();
        assertThat(RegrasDeExibicao.exibivel(CHEFE, 1)).isTrue();
        assertThat(RegrasDeExibicao.exibivel(AUTO, 1)).isTrue();
    }

    @Test
    void grupoComMenosDeTresSomeDaLinhaENaoEntraNaNotaFinal() {
        MontagemPainel.Montagem m = MontagemPainel.montar(List.of(
                linha(1L, AUTO, "5.00", 1),
                linha(1L, CHEFE, "4.00", 1),
                linha(1L, PAR, "1.37", 2),
                linha(1L, SUBORDINADO, "4.50", 3)), Map.of(1L, "Comunicação"));

        LinhaCompetencia l = m.linhas().get(0);
        assertThat(l.grupo(PAR).media()).isNull();
        assertThat(l.grupo(PAR).insuficiente()).isTrue();
        assertThat(l.grupo(PAR).texto()).isEqualTo("—");
        assertThat(l.grupo(SUBORDINADO).media()).isEqualByComparingTo("4.50");
        // sem autoavaliação e sem pares: (4,00 + 4,50) / 2
        assertThat(l.notaFinal()).isEqualByComparingTo("4.25");
        assertThat(m.gruposInsuficientes()).containsExactly(PAR);
    }

    @Test
    void avisoDeAnonimatoListaAsCompetenciasDeCadaGrupo() {
        MontagemPainel.Montagem m = MontagemPainel.montar(List.of(
                linha(1L, CHEFE, "4.00", 1), linha(1L, PAR, "1.37", 2), linha(1L, SUBORDINADO, "4.00", 1),
                linha(2L, CHEFE, "3.00", 1), linha(2L, PAR, "4.00", 3), linha(2L, SUBORDINADO, "3.00", 2),
                linha(3L, CHEFE, "5.00", 1), linha(3L, PAR, "2.00", 1), linha(3L, SUBORDINADO, "5.00", 2)),
                Map.of(1L, "Comunicação", 2L, "Liderança", 3L, "Trabalho em equipe"));
        PainelIndividual p = new PainelIndividual(null, null, 5, List.of(CHEFE, PAR, SUBORDINADO), m.linhas(),
                m.mediaGeral(), m.pontoForte(), m.foco(), List.of(PAR, SUBORDINADO), null, List.of(), List.of(), false, null);

        assertThat(p.avisoInsuficientes()).startsWith(
                "Pares em Comunicação e Trabalho em equipe; Subordinados em todas as competências: "
                        + "respostas insuficientes para preservar o anonimato");
        assertThat(m.linhas().get(1).grupo(PAR).texto()).isEqualTo("4,0");
    }

    @Test
    void notaFinalIgnoraAAutoavaliacaoESemOutroGrupoFicaVazia() {
        assertThat(RegrasDeExibicao.notaFinal(Map.of(AUTO, new BigDecimal("5.00")))).isNull();
        assertThat(RegrasDeExibicao.notaFinal(Map.of(AUTO, new BigDecimal("1.00"), CHEFE, new BigDecimal("3.00"))))
                .isEqualByComparingTo("3.00");
    }

    @Test
    void mediaGeralPontoForteEFoco() {
        MontagemPainel.Montagem m = MontagemPainel.montar(List.of(
                linha(1L, CHEFE, "4.50", 1), linha(1L, PAR, "4.75", 3),
                linha(2L, CHEFE, "4.00", 1), linha(2L, PAR, "1.37", 2),
                linha(3L, CHEFE, "3.20", 1), linha(3L, PAR, "3.60", 4)),
                Map.of(1L, "Atendimento ao cliente", 2L, "Comunicação", 3L, "Trabalho em equipe"));

        assertThat(m.linhas()).extracting(LinhaCompetencia::competencia)
                .containsExactly("Atendimento ao cliente", "Comunicação", "Trabalho em equipe");
        assertThat(m.linhas()).extracting(LinhaCompetencia::notaFinal).containsExactly(
                new BigDecimal("4.63"), new BigDecimal("4.00"), new BigDecimal("3.40"));
        assertThat(m.mediaGeral()).isEqualByComparingTo("4.01");
        assertThat(m.pontoForte()).isEqualTo("Atendimento ao cliente");
        assertThat(m.foco()).isEqualTo("Trabalho em equipe");
        assertThat(m.linhas().get(0).corNotaFinal()).isEqualTo("nota-maior");
        assertThat(m.linhas().get(2).corNotaFinal()).isEqualTo("nota-menor");
        assertThat(m.linhas().get(1).corNotaFinal()).isEqualTo("nota-normal");
        assertThat(m.escalaMax()).isEqualTo(5);
    }

    @Test
    void formatoDaNotaEPercentualDaEscala() {
        assertThat(Formatos.nota(new BigDecimal("4.25"))).isEqualTo("4,3");
        assertThat(Formatos.nota(null)).isEqualTo("—");
        assertThat(RegrasDeExibicao.percentualDaEscala(new BigDecimal("4.00"), 5)).isEqualByComparingTo("80.0");
        assertThat(RegrasDeExibicao.percentualDaEscala(new BigDecimal("8.00"), 10)).isEqualByComparingTo("80.0");
    }

    private static LinhaConsolidada linha(Long competencia, TipoAvaliador grupo, String media, int respostas) {
        return new LinhaConsolidada(99L, competencia, grupo, new BigDecimal(media), respostas, 5);
    }
}
