package br.edu.pucgoias.sgc.compartilhado.web;

import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Converte as exceções em páginas com mensagem simples (RNF-09). O acesso negado responde 403 sem mostrar
 * nenhum dado (RNF-03). Erros de validação esperados devem ser tratados no próprio controller, que volta à tela
 * do formulário; este tratamento é a rede de segurança.
 */
@ControllerAdvice
public class TratamentoDeErros {

    @ExceptionHandler(AccessDeniedException.class)
    public String acessoNegado(HttpServletResponse resposta, Model model) {
        resposta.setStatus(HttpServletResponse.SC_FORBIDDEN);
        model.addAttribute("titulo", "Acesso não permitido");
        model.addAttribute("mensagem", "Você não tem permissão para ver esta página ou estes dados.");
        return "erro";
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public String naoEncontrado(NaoEncontradoException e, HttpServletResponse resposta, Model model) {
        resposta.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("titulo", "Não encontrado");
        model.addAttribute("mensagem", e.getMessage());
        return "erro";
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public String regra(RegraDeNegocioException e, HttpServletResponse resposta, Model model) {
        resposta.setStatus(HttpServletResponse.SC_CONFLICT);
        model.addAttribute("titulo", "Não foi possível concluir");
        model.addAttribute("mensagem", e.getMessage());
        return "erro";
    }
}
