package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.resultados.dominio.ResultadoConsolidado;
import br.edu.pucgoias.sgc.resultados.repositorio.ResultadoConsolidadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consolidação dos resultados de um ciclo (RF-19, contrato CO-03 {@code consolidarResultados(Long cicloId)}).
 * Ponto único: lê as respostas, calcula com {@link CalculoConsolidacao}, apaga o consolidado do ciclo e grava de
 * novo. O mesmo conjunto de respostas gera sempre o mesmo resultado (RNF-12).
 */
@Service
public class ConsolidacaoService {

    private static final int ESCALA_PADRAO = 5;

    private final ResultadoConsolidadoRepository resultados;
    private final CicloService ciclos;
    private final QuestionarioService questionarios;

    public ConsolidacaoService(ResultadoConsolidadoRepository resultados, CicloService ciclos,
                               QuestionarioService questionarios) {
        this.resultados = resultados;
        this.ciclos = ciclos;
        this.questionarios = questionarios;
    }

    /** Recalcula o consolidado do ciclo a partir das respostas armazenadas. Devolve quantas linhas gravou. */
    @Transactional
    public int consolidarResultados(Long cicloId) {
        ciclos.ciclo(cicloId);
        Map<Long, Integer> escalas = escalas(cicloId);
        return gravar(cicloId, CalculoConsolidacao.consolidar(respostasDoCiclo(cicloId),
                avaliado -> escalas.getOrDefault(avaliado, ESCALA_PADRAO)));
    }

    /** Substitui o consolidado do ciclo pelas linhas informadas. */
    @Transactional
    public int gravar(Long cicloId, List<LinhaConsolidada> linhas) {
        resultados.apagarDoCiclo(cicloId);
        resultados.saveAll(linhas.stream().map(l -> new ResultadoConsolidado(cicloId, l.avaliadoId(),
                l.competenciaId(), l.grupo(), l.media(), l.numRespostas(), l.escalaMax())).toList());
        return linhas.size();
    }

    /**
     * Respostas de escala do ciclo, sem autor. Será ligada a {@code avaliacoes.respostasDoCiclo} quando o pacote
     * avaliacoes oferecer as respostas (UC-03); até lá o ciclo não tem respostas para consolidar.
     */
    List<NotaParaConsolidar> respostasDoCiclo(Long cicloId) {
        return List.of();
    }

    /** Nota máxima do questionário usado para cada pessoa avaliada no ciclo. */
    private Map<Long, Integer> escalas(Long cicloId) {
        Map<Long, Integer> escalas = new HashMap<>();
        Map<Long, Integer> porQuestionario = new HashMap<>();
        for (Long avaliado : ciclos.avaliadosDoCiclo(cicloId)) {
            Long questionarioId = ciclos.questionarioDoAvaliado(cicloId, avaliado);
            if (questionarioId == null) {
                throw new NaoEncontradoException("Pessoa avaliada sem questionário no ciclo.");
            }
            escalas.put(avaliado, porQuestionario.computeIfAbsent(questionarioId,
                    id -> questionarios.resumo(id).escalaMax()));
        }
        return escalas;
    }
}
