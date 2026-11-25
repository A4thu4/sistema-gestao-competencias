package br.edu.pucgoias.sgc.avaliacoes;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Objects;

/** Apoio dos testes do UC-03 e do UC-09: acha ciclos e designações dos dados de exemplo e monta formulários. */
final class ApoioAvaliacoes {

    private final CicloService ciclos;
    private final QuestionarioService questionarios;
    private final UsuarioService usuarios;

    ApoioAvaliacoes(CicloService ciclos, QuestionarioService questionarios, UsuarioService usuarios) {
        this.ciclos = ciclos;
        this.questionarios = questionarios;
        this.usuarios = usuarios;
    }

    /** "Ciclo 2026.2", aberto. */
    CicloResumo aberto() {
        return ciclos.abertos().stream().filter(c -> c.nome().equals("Ciclo 2026.2")).findFirst().orElseThrow();
    }

    /** "Ciclo 2026.1", encerrado. */
    CicloResumo encerrado() {
        return ciclos.encerrados().stream().filter(c -> c.nome().equals("Ciclo 2026.1")).findFirst().orElseThrow();
    }

    Long id(String email) {
        return usuarios.buscarPorEmail(email).id();
    }

    DesignacaoResumo designacao(CicloResumo ciclo, String avaliador, String avaliado, TipoAvaliador relacao) {
        Long avaliadorId = id(avaliador);
        Long avaliadoId = id(avaliado);
        return ciclos.designacoesDoCiclo(ciclo.id()).stream()
                .filter(d -> Objects.equals(d.avaliadorId(), avaliadorId) && d.avaliadoId().equals(avaliadoId)
                        && d.relacao() == relacao)
                .findFirst().orElseThrow();
    }

    QuestionarioCompleto questionario(DesignacaoResumo d) {
        return questionarios.detalharPara(d.questionarioId(), d.relacao());
    }

    PerguntaResumo primeiraDeEscala(DesignacaoResumo d) {
        return questionario(d).perguntas().stream().filter(PerguntaResumo::escala).findFirst().orElseThrow();
    }

    /** Preenche o formulário inteiro: nota 4 nas perguntas de escala e um comentário nas abertas. */
    MockHttpServletRequestBuilder tudoRespondido(MockHttpServletRequestBuilder req, DesignacaoResumo d) {
        for (PerguntaResumo p : questionario(d).perguntas()) {
            if (p.escala()) {
                req.param("nota_" + p.id(), "4");
            } else {
                req.param("texto_" + p.id(), "Comentário de teste sobre o período.");
            }
        }
        return req;
    }
}
