package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.dominio.AvaliacaoEnviada;
import br.edu.pucgoias.sgc.avaliacoes.dominio.Resposta;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.AvaliacaoEnviadaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RespostaRepository;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interface do pacote avaliacoes para o pacote resultados (UC-04): respostas das avaliações enviadas, sem nada que
 * identifique quem respondeu (RNF-01), e os comentários que passaram pela moderação (RF-22).
 */
@Service
@Transactional(readOnly = true)
public class ConsultaRespostasService {

    private final AvaliacaoEnviadaRepository avaliacoes;
    private final RespostaRepository respostas;
    private final QuestionarioService questionarios;

    public ConsultaRespostasService(AvaliacaoEnviadaRepository avaliacoes, RespostaRepository respostas,
                                    QuestionarioService questionarios) {
        this.avaliacoes = avaliacoes;
        this.respostas = respostas;
        this.questionarios = questionarios;
    }

    /** Respostas de escala de todas as avaliações enviadas do ciclo (perguntas abertas ficam de fora). */
    public List<RespostaConsolidavel> respostasDoCiclo(Long cicloId) {
        List<AvaliacaoEnviada> enviadas = avaliacoes.findByCicloId(cicloId);
        Map<UUID, AvaliacaoEnviada> porId = enviadas.stream()
                .collect(Collectors.toMap(AvaliacaoEnviada::getId, Function.identity()));
        Map<Long, PerguntaResumo> perguntas = perguntasDos(enviadas);
        List<RespostaConsolidavel> lista = new ArrayList<>();
        for (Resposta r : respostas.findByAvaliacaoEnviadaIdIn(porId.keySet())) {
            PerguntaResumo p = perguntas.get(r.getPerguntaId());
            if (p == null || !p.escala()) {
                continue;
            }
            AvaliacaoEnviada a = porId.get(r.getAvaliacaoEnviadaId());
            lista.add(new RespostaConsolidavel(a.getId(), a.getAvaliadoId(), a.getRelacao(), p.id(),
                    p.competenciaId(), r.getNota(), r.isNaoAvalia()));
        }
        return lista;
    }

    /**
     * Comentários das perguntas abertas sobre a pessoa no ciclo, sem os ocultados na moderação. Sem autor e sem
     * relação, exceto a marca de autoavaliação (a autora é a própria pessoa avaliada); em ordem de competência e
     * pergunta e, dentro delas, por ordem alfabética do texto (a ordem de envio não aparece).
     */
    public List<ComentarioResumo> comentariosVisiveis(Long cicloId, Long avaliadoId) {
        List<AvaliacaoEnviada> enviadas = avaliacoes.findByCicloIdAndAvaliadoId(cicloId, avaliadoId);
        Map<Long, Posicao> posicoes = posicoesDos(enviadas);
        Set<UUID> autoavaliacoes = enviadas.stream().filter(a -> a.getRelacao() == TipoAvaliador.AUTO)
                .map(AvaliacaoEnviada::getId).collect(Collectors.toSet());
        return respostas.findByAvaliacaoEnviadaIdIn(enviadas.stream().map(AvaliacaoEnviada::getId).toList())
                .stream()
                .filter(r -> r.temComentario() && !r.isComentarioOculto() && posicoes.containsKey(r.getPerguntaId()))
                .sorted(Comparator.comparing((Resposta r) -> posicoes.get(r.getPerguntaId()).ordem())
                        .thenComparing(Resposta::getTexto))
                .map(r -> {
                    Posicao p = posicoes.get(r.getPerguntaId());
                    return new ComentarioResumo(r.getTexto().trim(), p.competencia().id(), p.competencia().nome(),
                            p.pergunta().id(), p.pergunta().texto(),
                            autoavaliacoes.contains(r.getAvaliacaoEnviadaId()));
                })
                .toList();
    }

    public int avaliacoesEnviadas(Long cicloId, Long avaliadoId, TipoAvaliador relacao) {
        return (int) avaliacoes.countByCicloIdAndAvaliadoIdAndRelacao(cicloId, avaliadoId, relacao);
    }

    // ---------------------------------------------------------------- apoio

    record Posicao(int ordem, CompetenciaDoQuestionario competencia, PerguntaResumo pergunta) {
    }

    private Map<Long, PerguntaResumo> perguntasDos(List<AvaliacaoEnviada> enviadas) {
        Map<Long, PerguntaResumo> perguntas = new HashMap<>();
        enviadas.stream().map(AvaliacaoEnviada::getQuestionarioId).distinct()
                .forEach(q -> questionarios.detalhar(q).perguntas().forEach(p -> perguntas.put(p.id(), p)));
        return perguntas;
    }

    private Map<Long, Posicao> posicoesDos(List<AvaliacaoEnviada> enviadas) {
        Map<Long, Posicao> posicoes = new HashMap<>();
        enviadas.stream().map(AvaliacaoEnviada::getQuestionarioId).distinct().forEach(q -> {
            QuestionarioCompleto completo = questionarios.detalhar(q);
            int ordem = 0;
            for (CompetenciaDoQuestionario c : completo.competencias()) {
                for (PerguntaResumo p : c.perguntas()) {
                    posicoes.putIfAbsent(p.id(), new Posicao(ordem++, c, p));
                }
            }
        });
        return posicoes;
    }
}
