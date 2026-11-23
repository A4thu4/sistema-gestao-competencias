package br.edu.pucgoias.sgc.privacidade;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.identidade.servico.AutenticacaoService;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.privacidade.dominio.SituacaoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.servico.ConsentimentoService;
import br.edu.pucgoias.sgc.privacidade.servico.SolicitacaoResumo;
import br.edu.pucgoias.sgc.privacidade.servico.SolicitacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-05 Gerenciar consentimento e dados pessoais. A Kátia é a única pessoa de exemplo sem aceite; os testes que a
 * usam rodam numa transação desfeita no final, então ela continua sem aceite para a demonstração.
 */
class PrivacidadeTest extends TesteIntegracao {

    @Autowired
    private ConsentimentoService consentimento;

    @Autowired
    private SolicitacaoService solicitacoes;

    @Autowired
    private UsuarioService usuarios;

    @Autowired
    private EmailService emails;

    private String protocoloDaUltima(String email) {
        PessoaResumo p = usuarios.buscarPorEmail(email);
        return solicitacoesDe(p.id()).get(0).protocolo();
    }

    private List<SolicitacaoResumo> solicitacoesDe(Long usuarioId) {
        return solicitacoes.listar(rh(), null).stream().filter(s -> s.usuarioId().equals(usuarioId)).toList();
    }

    @Autowired
    private AutenticacaoService autenticacao;

    private UsuarioAutenticado rh() {
        return (UsuarioAutenticado) autenticacao.loadUserByUsername("rh@exemplo.com");
    }

    @Test
    void primeiroAcessoMostraOTermoEDepoisDoAceiteEntra() throws Exception {
        mvc.perform(get("/").with(como("katia@exemplo.com")))
                .andExpect(redirectedUrl("/privacidade/termo"));
        mvc.perform(get("/privacidade/meus-dados").with(como("katia@exemplo.com")))
                .andExpect(redirectedUrl("/privacidade/termo"));
        mvc.perform(get("/privacidade/termo").with(como("katia@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Termo de Consentimento e Privacidade")))
                .andExpect(content().string(containsString("encarregado@exemplo.com")));

        Long termoId = consentimento.idTermoVigente().orElseThrow();
        mvc.perform(post("/privacidade/termo").with(como("katia@exemplo.com")).with(csrf())
                        .param("decisao", "aceitar").param("termoId", termoId.toString()).param("concordo", "true"))
                .andExpect(redirectedUrl("/"));

        mvc.perform(get("/").with(como("katia@exemplo.com"))).andExpect(status().isOk());
        PessoaResumo katia = usuarios.buscarPorEmail("katia@exemplo.com");
        assertThat(consentimento.ultimoAceite(katia.id())).get()
                .satisfies(a -> assertThat(a.versao()).isEqualTo("1.0"))
                .satisfies(a -> assertThat(a.aceitoEm()).isNotNull());
    }

    @Test
    void recusarMantemOBloqueio() throws Exception {
        mvc.perform(post("/privacidade/termo").with(como("katia@exemplo.com")).with(csrf()).param("decisao", "recusar"))
                .andExpect(redirectedUrl("/privacidade/termo"));
        mvc.perform(get("/").with(como("katia@exemplo.com"))).andExpect(redirectedUrl("/privacidade/termo"));
    }

    @Test
    void quemJaAceitouEntraDireto() throws Exception {
        mvc.perform(get("/").with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Meus dados")));
    }

    @Test
    void novaVersaoObrigaNovoAceiteMostrandoOQueMudou() throws Exception {
        mvc.perform(post("/privacidade/termos").with(como("rh@exemplo.com")).with(csrf())
                        .param("versao", "2.0")
                        .param("texto", "## Dados\n- Nome e cargo.\n\nTexto novo do termo.")
                        .param("resumoMudancas", "Os dados passam a ser guardados por cinco anos."))
                .andExpect(redirectedUrl("/privacidade/termos"));

        mvc.perform(get("/").with(como("diego@exemplo.com"))).andExpect(redirectedUrl("/privacidade/termo"));
        mvc.perform(get("/privacidade/termo").with(como("diego@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("O termo mudou desde o seu último aceite")))
                .andExpect(content().string(containsString("Os dados passam a ser guardados por cinco anos.")));

        Long novo = consentimento.idTermoVigente().orElseThrow();
        mvc.perform(post("/privacidade/termo").with(como("diego@exemplo.com")).with(csrf())
                        .param("decisao", "aceitar").param("termoId", novo.toString()).param("concordo", "true"))
                .andExpect(redirectedUrl("/"));
        mvc.perform(get("/").with(como("diego@exemplo.com"))).andExpect(status().isOk());
    }

    @Test
    void novaVersaoSemResumoDasMudancasVoltaComErro() throws Exception {
        mvc.perform(post("/privacidade/termos").with(como("rh@exemplo.com")).with(csrf())
                        .param("versao", "2.0").param("texto", "Texto"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Conte o que mudou")));
        mvc.perform(get("/").with(como("diego@exemplo.com"))).andExpect(status().isOk());
    }

    @Test
    void solicitacaoDeCorrecaoGeraProtocoloEEmail() throws Exception {
        mvc.perform(post("/privacidade/meus-dados/solicitacoes").with(como("diego@exemplo.com")).with(csrf())
                        .param("tipo", "CORRECAO").param("dadoIncorreto", "Área").param("valorCorreto", "Infraestrutura")
                        .param("descricao", "Mudei de área no mês passado."))
                .andExpect(redirectedUrl("/privacidade/meus-dados"));

        String protocolo = protocoloDaUltima("diego@exemplo.com");
        assertThat(protocolo).matches("LGPD-\\d{4}-\\d{6}");
        PessoaResumo diego = usuarios.buscarPorEmail("diego@exemplo.com");
        assertThat(solicitacoesDe(diego.id()).get(0).situacao()).isEqualTo(SituacaoSolicitacao.EM_ANALISE);
        assertThat(emails.enviadosPara("diego@exemplo.com"))
                .anySatisfy(e -> assertThat(e.getAssunto()).contains(protocolo));

        mvc.perform(get("/privacidade/meus-dados").with(como("diego@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(protocolo)))
                .andExpect(content().string(containsString("Em análise")));
    }

    @Test
    void correcaoSemValorCorretoVoltaComErro() throws Exception {
        mvc.perform(post("/privacidade/meus-dados/solicitacoes").with(como("diego@exemplo.com")).with(csrf())
                        .param("tipo", "CORRECAO").param("dadoIncorreto", "Nome"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Escreva o valor correto.")));
    }

    @Test
    void pedidoDeAcessoGeraCopiaEFicaAtendido() throws Exception {
        mvc.perform(post("/privacidade/meus-dados/solicitacoes").with(como("elisa@exemplo.com")).with(csrf())
                        .param("tipo", "ACESSO"))
                .andExpect(redirectedUrl("/privacidade/meus-dados/copia"));

        PessoaResumo elisa = usuarios.buscarPorEmail("elisa@exemplo.com");
        SolicitacaoResumo s = solicitacoesDe(elisa.id()).get(0);
        assertThat(s.tipo()).isEqualTo(TipoSolicitacao.ACESSO);
        assertThat(s.situacao()).isEqualTo(SituacaoSolicitacao.ATENDIDA);
        assertThat(s.concluidaEm()).isNotNull();

        mvc.perform(get("/privacidade/meus-dados/copia").with(como("elisa@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Elisa Martins")))
                .andExpect(content().string(containsString("Bruno Lima")))
                .andExpect(content().string(containsString("sem dizer quem respondeu")));
        mvc.perform(get("/privacidade/meus-dados/copia.pdf").with(como("elisa@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(content().string(containsString("%PDF")));
    }

    @Test
    void rhConcluiExclusaoEAPessoaFicaAnonimizadaEInativa() throws Exception {
        mvc.perform(post("/privacidade/meus-dados/solicitacoes").with(como("fabio@exemplo.com")).with(csrf())
                        .param("tipo", "EXCLUSAO").param("descricao", "Saí da empresa."))
                .andExpect(redirectedUrl("/privacidade/meus-dados"));
        PessoaResumo fabio = usuarios.buscarPorEmail("fabio@exemplo.com");
        SolicitacaoResumo s = solicitacoesDe(fabio.id()).get(0);

        mvc.perform(get("/privacidade/solicitacoes/" + s.id()).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mantivemos")));
        mvc.perform(post("/privacidade/solicitacoes/" + s.id() + "/concluir").with(como("rh@exemplo.com")).with(csrf())
                        .param("decisao", "ATENDIDA_PARCIAL")
                        .param("justificativa", SolicitacaoService.justificativaPadraoExclusao()))
                .andExpect(redirectedUrl("/privacidade/solicitacoes"));

        PessoaResumo depois = usuarios.buscar(fabio.id());
        assertThat(depois.ativo()).isFalse();
        assertThat(depois.nome()).startsWith("Titular excluído");
        assertThat(depois.email()).doesNotContain("fabio");
        assertThat(emails.enviadosPara("fabio@exemplo.com"))
                .anySatisfy(e -> assertThat(e.getAssunto()).contains("Resposta à sua solicitação"));
    }

    @Test
    void rhAtendeCorrecaoDeNomeDaCarla() throws Exception {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        SolicitacaoResumo s = solicitacoesDe(carla.id()).stream()
                .filter(x -> x.situacao() == SituacaoSolicitacao.EM_ANALISE).findFirst().orElseThrow();
        mvc.perform(post("/privacidade/solicitacoes/" + s.id() + "/concluir").with(como("rh@exemplo.com")).with(csrf())
                        .param("decisao", "ATENDIDA").param("justificativa", "Nome corrigido."))
                .andExpect(redirectedUrl("/privacidade/solicitacoes"));
        assertThat(usuarios.buscar(carla.id()).nome()).isEqualTo("Carla Dias Moreira");

        mvc.perform(post("/privacidade/solicitacoes/" + s.id() + "/concluir").with(como("rh@exemplo.com")).with(csrf())
                        .param("decisao", "NEGADA").param("justificativa", "De novo."))
                .andExpect(redirectedUrl("/privacidade/solicitacoes/" + s.id()));
    }

    @Test
    void conclusaoSemJustificativaNaoMudaNada() throws Exception {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        SolicitacaoResumo s = solicitacoesDe(carla.id()).get(0);
        mvc.perform(post("/privacidade/solicitacoes/" + s.id() + "/concluir").with(como("rh@exemplo.com")).with(csrf())
                        .param("decisao", "ATENDIDA").param("justificativa", " "))
                .andExpect(redirectedUrl("/privacidade/solicitacoes/" + s.id()));
        assertThat(usuarios.buscar(carla.id()).nome()).isEqualTo("Carla Dias");
    }

    @Test
    void rhVeAListaFiltradaEAsVersoesDoTermo() throws Exception {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        String protocolo = solicitacoesDe(carla.id()).get(0).protocolo();
        mvc.perform(get("/privacidade/solicitacoes").param("situacao", "EM_ANALISE").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(protocolo)))
                .andExpect(content().string(containsString("Carla Dias")));
        mvc.perform(get("/privacidade/solicitacoes").param("situacao", "NEGADA").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(protocolo))));
        mvc.perform(get("/privacidade/termos").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Publicar nova versão")))
                .andExpect(content().string(containsString("pessoas ativas já aceitaram")));
    }

    @Test
    void colaboradorNaoAcessaAsSolicitacoesDoRh() throws Exception {
        mvc.perform(get("/privacidade/solicitacoes").with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/privacidade/termos").with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Publicar nova versão"))));
    }

    @Test
    void ninguemVeASolicitacaoDeOutraPessoa() throws Exception {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        SolicitacaoResumo daCarla = solicitacoesDe(carla.id()).get(0);
        mvc.perform(get("/privacidade/meus-dados/solicitacoes/" + daCarla.id()).with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(daCarla.protocolo())));
        mvc.perform(get("/privacidade/meus-dados/solicitacoes/" + daCarla.id()).with(como("diego@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString(daCarla.protocolo()))));
        mvc.perform(get("/privacidade/meus-dados").with(como("diego@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(daCarla.protocolo()))));
    }

    @Test
    void paginaInicialMostraOsCartoesDePrivacidade() throws Exception {
        mvc.perform(get("/").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Solicitações LGPD em análise")))
                .andExpect(content().string(containsString("Termo v1.0")));
    }
}
