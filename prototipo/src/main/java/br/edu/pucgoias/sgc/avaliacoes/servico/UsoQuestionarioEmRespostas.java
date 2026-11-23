package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.repositorio.AvaliacaoEnviadaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RascunhoRespostaRepository;
import br.edu.pucgoias.sgc.competencias.servico.ConsultaUsoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementa {@code ConsultaUsoQuestionario.possuiRespostas} para o UC-01 (extensão 2b): o questionário já tem
 * avaliação enviada ou rascunho de resposta. O {@code QuestionarioService} vem por {@link ObjectProvider} porque ele
 * mesmo recebe esta consulta (evita a dependência circular na criação dos beans).
 */
@Component
public class UsoQuestionarioEmRespostas implements ConsultaUsoQuestionario {

    private final AvaliacaoEnviadaRepository avaliacoes;
    private final RascunhoRespostaRepository rascunhos;
    private final ObjectProvider<QuestionarioService> questionarios;

    public UsoQuestionarioEmRespostas(AvaliacaoEnviadaRepository avaliacoes, RascunhoRespostaRepository rascunhos,
                                      ObjectProvider<QuestionarioService> questionarios) {
        this.avaliacoes = avaliacoes;
        this.rascunhos = rascunhos;
        this.questionarios = questionarios;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean possuiRespostas(Long questionarioId) {
        if (avaliacoes.existsByQuestionarioId(questionarioId)) {
            return true;
        }
        List<Long> perguntas = questionarios.getObject().detalhar(questionarioId).perguntas().stream()
                .map(PerguntaResumo::id).toList();
        return !perguntas.isEmpty() && rascunhos.existsByPerguntaIdIn(perguntas);
    }
}
