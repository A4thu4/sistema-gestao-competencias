package br.edu.pucgoias.sgc.identidade;

import br.edu.pucgoias.sgc.TesteIntegracao;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/** UC-10: login, perfis e controle de acesso no servidor. */
class AutenticacaoTest extends TesteIntegracao {

    @Test
    void semLoginVaiParaATelaDeLogin() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginComSenhaCertaEntra() throws Exception {
        mvc.perform(formLogin("/login").userParameter("email").passwordParam("senha")
                        .user("rh@exemplo.com").password("senha123"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginComSenhaErradaVoltaComErro() throws Exception {
        mvc.perform(formLogin("/login").userParameter("email").passwordParam("senha")
                        .user("rh@exemplo.com").password("errada"))
                .andExpect(redirectedUrl("/login?erro"));
    }

    @Test
    void menuDoRhMostraGestaoEDoColaboradorNao() throws Exception {
        mvc.perform(get("/pessoas").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ciclos de avaliação")))
                .andExpect(content().string(containsString("Carla Dias")));
        mvc.perform(get("/emails").with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Ciclos de avaliação"))));
    }
}
