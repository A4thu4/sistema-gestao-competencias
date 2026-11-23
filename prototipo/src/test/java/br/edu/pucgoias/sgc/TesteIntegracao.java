package br.edu.pucgoias.sgc;

import br.edu.pucgoias.sgc.identidade.servico.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Base dos testes de integração: sobe a aplicação inteira contra o PostgreSQL (migrações Flyway de verdade),
 * com a empresa de exemplo carregada. Cada teste roda numa transação desfeita no final.
 *
 * <p>Usuários de exemplo: rh@, gestor@ (Bruno), colaborador@ (Carla), diego@, elisa@, fabio@, helena@,
 * gustavo@, igor@, julia@, katia@ — todos {@code @exemplo.com}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class TesteIntegracao {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private AutenticacaoService autenticacao;

    /** Faz a requisição como o usuário de exemplo com este e-mail. */
    protected RequestPostProcessor como(String email) {
        UserDetails u = autenticacao.loadUserByUsername(email);
        return user(u);
    }
}
