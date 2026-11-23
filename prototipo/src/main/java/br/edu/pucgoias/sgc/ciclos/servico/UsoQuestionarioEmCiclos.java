package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.ciclos.repositorio.CicloRepository;
import br.edu.pucgoias.sgc.competencias.servico.ConsultaUsoQuestionario;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementa, para o pacote competencias, a consulta dos ciclos que usam um questionário (UC-01, 2b): os nomes dos
 * ciclos de qualquer situação. O {@code possuiRespostas} fica com o pacote avaliacoes (inversão de dependência).
 */
@Component
public class UsoQuestionarioEmCiclos implements ConsultaUsoQuestionario {

    private final CicloRepository ciclos;

    public UsoQuestionarioEmCiclos(CicloRepository ciclos) {
        this.ciclos = ciclos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> ciclosQueUsam(Long questionarioId) {
        return ciclos.nomesQueUsamQuestionario(questionarioId).stream().distinct().toList();
    }
}
