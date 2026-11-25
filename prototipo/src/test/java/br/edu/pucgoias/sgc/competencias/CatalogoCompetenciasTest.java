package br.edu.pucgoias.sgc.competencias;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.competencias.servico.DadosCompetencia;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** UC-01, extensão 2c: catálogo de competências (RF-01) — cadastrar, editar, desativar sem apagar e reativar. */
class CatalogoCompetenciasTest extends TesteIntegracao {

    private static final String RH = "rh@exemplo.com";

    @Autowired
    private CompetenciaService catalogo;
    @Autowired
    private QuestionarioService questionarios;
    @Autowired
    private UsuarioService usuarios;

    @Test
    void catalogoDeExemploTemTecnicasEComportamentais() {
        List<CompetenciaResumo> ativas = catalogo.competenciasAtivas();
        assertThat(ativas).hasSizeGreaterThanOrEqualTo(8);
        assertThat(ativas).anyMatch(CompetenciaResumo::tecnica).anyMatch(c -> !c.tecnica());
    }

    @Test
    void rhCadastraEEditaCompetencia() throws Exception {
        mvc.perform(post("/competencias/catalogo/nova").with(como(RH)).with(csrf())
                        .param("nome", "  Negociação ").param("descricao", "Busca acordos bons para todos.").param("tipo", "COMPORTAMENTAL"))
                .andExpect(redirectedUrl("/competencias/catalogo"));
        CompetenciaResumo nova = porNome("Negociação");
        assertThat(nova.ativa()).isTrue();
        assertThat(nova.tecnica()).isFalse();

        mvc.perform(post("/competencias/catalogo/" + nova.id() + "/editar").with(como(RH)).with(csrf())
                        .param("nome", "Negociação").param("descricao", "Nova descrição.").param("tipo", "TECNICA"))
                .andExpect(redirectedUrl("/competencias/catalogo"));
        assertThat(catalogo.buscar(nova.id()).descricao()).isEqualTo("Nova descrição.");
        assertThat(catalogo.buscar(nova.id()).tecnica()).isTrue();

        mvc.perform(post("/competencias/catalogo/nova").with(como(RH)).with(csrf())
                        .param("nome", "Sem descrição").param("descricao", " ").param("tipo", "TECNICA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Informe o nome e a descrição da competência.")));
    }

    @Test
    void nomeDuplicadoAvisaESugereAExistente() throws Exception {
        mvc.perform(post("/competencias/catalogo/nova").with(como(RH)).with(csrf())
                        .param("nome", "comunicação").param("descricao", "Outra.").param("tipo", "COMPORTAMENTAL"))
                .andExpect(status().isOk())
                .andExpect(view().name("competencias/competencia-form"))
                .andExpect(model().attributeExists("duplicada"))
                .andExpect(content().string(containsString("Já existe a competência “Comunicação”")));
        assertThat(catalogo.catalogo("comunica")).hasSize(1);
    }

    @Test
    void desativarCompetenciaEmUsoPedeConfirmacaoENaoApaga() throws Exception {
        CompetenciaResumo lideranca = porNome("Liderança");
        Long questionarioCoordenador = questionarios.publicadosDoCargo(cargo("Coordenador de Suporte")).get(0).id();

        // Sem confirmação: mostra os questionários publicados afetados.
        mvc.perform(post("/competencias/catalogo/" + lideranca.id() + "/desativar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/competencias/catalogo/" + lideranca.id() + "/desativar"));
        assertThat(catalogo.buscar(lideranca.id()).ativa()).isTrue();
        mvc.perform(get("/competencias/catalogo/" + lideranca.id() + "/desativar").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Questionário — Coordenador de Suporte")));

        mvc.perform(post("/competencias/catalogo/" + lideranca.id() + "/desativar").param("confirmado", "true")
                        .with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/competencias/catalogo"));

        assertThat(catalogo.buscar(lideranca.id()).ativa()).isFalse();
        assertThat(catalogo.competenciasAtivas()).noneMatch(c -> c.id().equals(lideranca.id()));
        assertThat(catalogo.competenciasPorIds(List.of(lideranca.id()))).containsKey(lideranca.id());
        assertThat(questionarios.detalhar(questionarioCoordenador).competencias())
                .extracting(CompetenciaDoQuestionario::nome).contains("Liderança");
        mvc.perform(get("/competencias/catalogo").with(como(RH)))
                .andExpect(content().string(containsString("Liderança")))
                .andExpect(content().string(containsString("Desativada")));

        mvc.perform(post("/competencias/catalogo/" + lideranca.id() + "/reativar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/competencias/catalogo"));
        assertThat(catalogo.buscar(lideranca.id()).ativa()).isTrue();
    }

    @Test
    void desativarCompetenciaSemQuestionarioPublicadoNaoPrecisaDeConfirmacao() throws Exception {
        Long id = catalogo.cadastrar(new DadosCompetencia(
                "Criatividade", "Propõe ideias novas.", "COMPORTAMENTAL"));
        mvc.perform(post("/competencias/catalogo/" + id + "/desativar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/competencias/catalogo"));
        assertThat(catalogo.buscar(id).ativa()).isFalse();
    }

    @Test
    void colaboradorNaoAcessaOCatalogo() throws Exception {
        Long id = porNome("Comunicação").id();
        mvc.perform(post("/competencias/catalogo/" + id + "/desativar").param("confirmado", "true")
                        .with(como("colaborador@exemplo.com")).with(csrf()))
                .andExpect(status().isForbidden());
        assertThat(catalogo.buscar(id).ativa()).isTrue();
    }

    private CompetenciaResumo porNome(String nome) {
        return catalogo.catalogo(null).stream().filter(c -> c.nome().equals(nome)).findFirst().orElseThrow();
    }

    private Long cargo(String nome) {
        return usuarios.cargos().stream().filter(c -> c.nome().equals(nome)).findFirst().orElseThrow().id();
    }
}
