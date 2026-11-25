package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.avaliacoes.servico.ConsultaRespostasService;
import br.edu.pucgoias.sgc.ciclos.servico.CicloEncerradoEvento;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.resultados.dominio.ResultadoConsolidado;
import br.edu.pucgoias.sgc.resultados.repositorio.ResultadoConsolidadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ponto único da consolidação: lê as respostas, calcula com {@link CalculoConsolidacao}, apaga o consolidado do ciclo
 * e grava de novo. O mesmo conjunto de respostas gera sempre o mesmo resultado.
 *
 * <p>Roda sozinha no encerramento do ciclo ({@link CicloEncerradoEvento}, na mesma transação) e pelo botão
 * "Recalcular" do RH.
 */
@Service
public class ConsolidacaoService {

    private static final int ESCALA_PADRAO = 5;
    private static final Logger log = LoggerFactory.getLogger(ConsolidacaoService.class);

    private final ResultadoConsolidadoRepository resultados;
    private final CicloService ciclos;
    private final QuestionarioService questionarios;
    private final ConsultaRespostasService respostas;
    private final ApplicationEventPublisher eventos;

    public ConsolidacaoService(ResultadoConsolidadoRepository resultados, CicloService ciclos,
                               QuestionarioService questionarios, ConsultaRespostasService respostas,
                               ApplicationEventPublisher eventos) {
        this.resultados = resultados;
        this.ciclos = ciclos;
        this.questionarios = questionarios;
        this.respostas = respostas;
        this.eventos = eventos;
    }

    /** Consolida assim que o ciclo é encerrado (pela rotina ou por "Encerrar agora"). */
    @EventListener
    @Transactional
    public void aoEncerrar(CicloEncerradoEvento evento) {
        int linhas = consolidarResultados(evento.cicloId());
        CicloResumo c = ciclos.ciclo(evento.cicloId());
        eventos.publishEvent(EventoAuditoria.de("RESULTADO_CONSOLIDADO", ResultadoService.descricao(c) + ": "
                + linhas + " médias calculadas no encerramento"));
        log.info("Ciclo {} consolidado no encerramento: {} médias", evento.cicloId(), linhas);
    }

    /** Botão "Recalcular" do RH: refaz o consolidado do ciclo encerrado a partir das respostas. */
    @Transactional
    public int recalcular(UsuarioAutenticado eu, Long cicloId) {
        if (!eu.ehRh()) {
            throw new AccessDeniedException("Só o RH recalcula resultados.");
        }
        CicloResumo c = ciclos.ciclo(cicloId);
        if (!c.encerrado()) {
            throw new RegraDeNegocioException("Só um ciclo encerrado tem resultado para recalcular.");
        }
        int linhas = consolidarResultados(cicloId);
        eventos.publishEvent(EventoAuditoria.de("RESULTADO_RECALCULADO", ResultadoService.descricao(c) + ": "
                + linhas + " médias recalculadas a partir das respostas armazenadas"));
        return linhas;
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

    /** Respostas de escala das avaliações enviadas do ciclo, sem autor (pacote avaliacoes). */
    List<NotaParaConsolidar> respostasDoCiclo(Long cicloId) {
        return respostas.respostasDoCiclo(cicloId).stream()
                .map(r -> new NotaParaConsolidar(r.avaliacaoId(), r.avaliadoId(), r.relacao(), r.competenciaId(),
                        r.nota(), r.naoAvalia()))
                .toList();
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
