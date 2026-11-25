package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.avaliacoes.servico.ResultadoAnterior;
import br.edu.pucgoias.sgc.avaliacoes.servico.ResumoResultadoAnterior;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Resultado do ciclo encerrado anterior, mostrado ao gestor enquanto avalia o subordinado (UC-03 passo 4, RF-18).
 * Implementa a interface declarada pelo pacote avaliacoes (inversão de dependência). Usa a nota final de cada
 * competência, que já segue o mínimo de 3 respostas (RNF-02); nunca respostas individuais (RNF-01).
 */
@Component
public class ResultadoAnteriorDoAvaliado implements ResultadoAnterior {

    private final ResultadoService resultados;
    private final CicloService ciclos;

    public ResultadoAnteriorDoAvaliado(ResultadoService resultados, CicloService ciclos) {
        this.resultados = resultados;
        this.ciclos = ciclos;
    }

    @Override
    public Optional<ResumoResultadoAnterior> doAvaliado(Long avaliadoId, Long cicloAtualId) {
        CicloResumo atual = ciclos.ciclo(cicloAtualId);
        return resultados.ciclosEncerradosComResultado(avaliadoId).stream()
                .filter(c -> !c.id().equals(cicloAtualId) && c.dataAbertura().isBefore(atual.dataAbertura()))
                .findFirst()
                .map(c -> {
                    MontagemPainel.Montagem m = resultados.montagem(c.id(), avaliadoId);
                    return new ResumoResultadoAnterior(c.id(), c.nome(), m.escalaMax(), duplo(m.mediaGeral()),
                            Formatos.nota(m.mediaGeral()),
                            m.linhas().stream().map(l -> new ResumoResultadoAnterior.MediaCompetencia(
                                    l.competenciaId(), l.competencia(), duplo(l.notaFinal()), l.notaFinalTexto()))
                                    .toList());
                });
    }

    private static Double duplo(BigDecimal valor) {
        return valor == null ? null : valor.doubleValue();
    }
}
