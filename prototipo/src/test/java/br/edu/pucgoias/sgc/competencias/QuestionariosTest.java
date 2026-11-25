package br.edu.pucgoias.sgc.competencias;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.competencias.servico.DadosQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.CargoResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** UC-01: questionários (cenário principal e extensões 2a, 5a, 5b, 9a, 9c, 12a, 14a) e a interface pública. */
class QuestionariosTest extends TesteIntegracao {

    private static final String RH = "rh@exemplo.com";

    @Autowired
    private QuestionarioService questionarios;
    @Autowired
    private CompetenciaService catalogo;
    @Autowired
    private UsuarioService usuarios;

    @Test
    void rhCriaEPublicaQuestionarioEmBranco() throws Exception {
        Long cargo = cargo("Desenvolvedor");
        MvcResult criado = mvc.perform(post("/competencias/questionarios/novo").with(como(RH)).with(csrf())
                        .param("nome", "Questionário de teste").param("cargoId", cargo.toString()).param("escalaMax", "5"))
                .andExpect(status().is3xxRedirection()).andReturn();
        Long id = idNoFim(criado.getResponse().getRedirectedUrl());

        // Passo 8: as competências padrão do cargo vêm marcadas (RF-02).
        assertThat(questionarios.detalhar(id).competenciaIds()).containsExactlyInAnyOrderElementsOf(catalogo.padraoDoCargo(cargo));
        mvc.perform(get("/competencias/questionarios/" + id).with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usar esta seleção como padrão do cargo")))
                .andExpect(content().string(containsString("name=\"perguntas[XINDICEX].texto\"")))
                .andExpect(content().string(containsString("window.padroesPorCargo = {\"")))
                .andExpect(content().string(containsString("/js/questionario-editor.js")));

        Long comunicacao = competencia("Comunicação");
        mvc.perform(post("/competencias/questionarios/" + id).with(como(RH)).with(csrf())
                        .param("nome", "Questionário de teste").param("cargoId", cargo.toString()).param("escalaMax", "5")
                        .param("competencias", comunicacao.toString())
                        .param("perguntas[0].competenciaId", comunicacao.toString())
                        .param("perguntas[0].texto", "Explica as ideias com clareza.")
                        .param("perguntas[0].tipo", "ESCALA").param("perguntas[0].obrigatoria", "true")
                        .param("perguntas[0].chefe", "true").param("perguntas[0].auto", "true")
                        .param("perguntas[3].competenciaId", comunicacao.toString())
                        .param("perguntas[3].texto", "Comentários").param("perguntas[3].tipo", "ABERTA")
                        .param("perguntas[3].par", "true")
                        .param("perguntas[4].competenciaId", comunicacao.toString()).param("perguntas[4].texto", "  ")
                        .param("niveis[0]", "Nunca").param("niveis[1]", "Raramente").param("niveis[2]", "Às vezes")
                        .param("niveis[3]", "Frequentemente").param("niveis[4]", "Sempre")
                        .param("acao", "previa"))
                .andExpect(redirectedUrl("/competencias/questionarios/" + id + "/previa"));

        // Passo 13: pré-visualização com "não tenho como avaliar" (RF-06).
        mvc.perform(get("/competencias/questionarios/" + id + "/previa").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Não tenho como avaliar")))
                .andExpect(content().string(containsString("Explica as ideias com clareza.")));

        mvc.perform(post("/competencias/questionarios/" + id + "/publicar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/competencias/questionarios"))
                .andExpect(flash().attributeExists("sucesso"));

        QuestionarioCompleto q = questionarios.detalhar(id);
        assertThat(q.publicado()).isTrue();
        assertThat(q.competenciaIds()).containsExactly(comunicacao);
        assertThat(q.perguntas()).extracting(PerguntaResumo::texto).containsExactly("Explica as ideias com clareza.", "Comentários");
        assertThat(q.perguntas().get(0).avaliadores()).containsExactlyInAnyOrder(TipoAvaliador.CHEFE, TipoAvaliador.AUTO);
        assertThat(q.perguntas().get(1).aberta()).isTrue();
        assertThat(q.perguntas().get(1).obrigatoria()).isFalse();
        assertThat(q.niveis()).hasSize(5);
        assertThat(questionarios.publicadosDoCargo(cargo)).anyMatch(r -> r.id().equals(id));
        mvc.perform(get("/competencias/questionarios").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Questionário de teste")));
    }

    @Test
    void publicacaoBloqueadaComPendenciasMantemORascunho() throws Exception {
        Long id = questionarios.criarEmBranco("Sem perguntas", cargo("Analista de RH"), 5);

        MvcResult r = mvc.perform(post("/competencias/questionarios/" + id + "/publicar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/competencias/questionarios/" + id))
                .andExpect(flash().attributeExists("erro"))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<String> pendencias = (List<String>) r.getFlashMap().get("pendencias");
        assertThat(pendencias).anyMatch(p -> p.contains("ainda não tem nenhuma pergunta"))
                .contains("Descreva o nível 1 da escala.");
        assertThat(questionarios.resumo(id).publicado()).isFalse();

        mvc.perform(get("/competencias/questionarios/" + id + "/previa").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ainda não dá para publicar")));

        Long semCargo = questionarios.criarEmBranco("Outro", cargo("Analista de RH"), 5);
        questionarios.salvar(semCargo, new DadosQuestionario(
                "", null, 5, List.of(), List.of(), List.of(), false));
        assertThat(questionarios.publicar(semCargo)).contains("Dê um nome ao questionário.",
                "Escolha o cargo a que o questionário se destina.", "Escolha ao menos uma competência.");
    }

    @Test
    void copiaDeQuestionarioAnteriorLevaAEstruturaCompleta() throws Exception {
        QuestionarioCompleto original = questionarios.detalhar(questionarios.publicadosDoCargo(cargo("Desenvolvedor")).get(0).id());

        MvcResult r = mvc.perform(post("/competencias/questionarios/copiar").with(como(RH)).with(csrf())
                        .param("origemId", original.id().toString()))
                .andExpect(status().is3xxRedirection()).andReturn();
        QuestionarioCompleto copia = questionarios.detalhar(idNoFim(r.getResponse().getRedirectedUrl()));

        assertThat(copia.id()).isNotEqualTo(original.id());
        assertThat(copia.origemId()).isEqualTo(original.id());
        assertThat(copia.publicado()).isFalse();
        assertThat(copia.nome()).contains("(cópia)");
        assertThat(copia.cargoId()).isEqualTo(original.cargoId());
        assertThat(copia.competenciaIds()).containsExactlyElementsOf(original.competenciaIds());
        assertThat(copia.perguntas()).extracting(PerguntaResumo::texto)
                .containsExactlyElementsOf(original.perguntas().stream().map(PerguntaResumo::texto).toList());
        assertThat(copia.perguntas()).extracting(PerguntaResumo::id)
                .doesNotContainAnyElementsOf(original.perguntas().stream().map(PerguntaResumo::id).toList());
        assertThat(copia.niveis()).isEqualTo(original.niveis());
        assertThat(questionarios.pendencias(copia.id())).isEmpty();
    }

    @Test
    void modeloProntoViraRascunhoParaPersonalizar() throws Exception {
        QuestionarioCompleto modelo = questionarios.modelos().stream()
                .filter(m -> m.nome().contains("360")).findFirst().orElseThrow();
        assertThat(questionarios.modelos()).hasSize(3);

        MvcResult r = mvc.perform(post("/competencias/questionarios/modelo/" + modelo.id()).with(como(RH)).with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();
        QuestionarioCompleto novo = questionarios.detalhar(idNoFim(r.getResponse().getRedirectedUrl()));

        assertThat(novo.modelo()).isFalse();
        assertThat(novo.cargoId()).isNull();
        assertThat(novo.publicado()).isFalse();
        assertThat(novo.totalPerguntas()).isEqualTo(modelo.totalPerguntas());
        assertThat(questionarios.pendencias(novo.id())).containsExactly("Escolha o cargo a que o questionário se destina.");
        // Modelos não aparecem como questionários disponíveis para ciclos.
        assertThat(questionarios.publicados()).noneMatch(q -> q.id().equals(modelo.id()));
    }

    @Test
    void detalharParaFiltraPorTipoDeAvaliador() {
        Long id = questionarios.publicadosDoCargo(cargo("Coordenador de Suporte")).get(0).id();

        assertThat(nomes(questionarios.detalhar(id))).contains("Liderança");
        assertThat(nomes(questionarios.detalharPara(id, TipoAvaliador.SUBORDINADO))).contains("Liderança");
        assertThat(nomes(questionarios.detalharPara(id, TipoAvaliador.AUTO))).doesNotContain("Liderança");
        assertThat(nomes(questionarios.detalharPara(id, TipoAvaliador.PAR))).doesNotContain("Liderança");

        String soChefe = "Atinge as metas combinadas com a gestão no período.";
        assertThat(questionarios.detalharPara(id, TipoAvaliador.CHEFE).perguntas()).extracting(PerguntaResumo::texto).contains(soChefe);
        assertThat(questionarios.detalharPara(id, TipoAvaliador.PAR).perguntas()).extracting(PerguntaResumo::texto).doesNotContain(soChefe);
        assertThat(questionarios.detalharPara(id, TipoAvaliador.PAR).perguntas()).allMatch(p -> p.seAplicaA(TipoAvaliador.PAR));
        assertThat(questionarios.temPerguntasPara(id, TipoAvaliador.EXTERNO)).isTrue();

        Long pares = questionarios.modelos().stream().filter(m -> m.nome().contains("pares")).findFirst().orElseThrow().id();
        assertThat(questionarios.temPerguntasPara(pares, TipoAvaliador.CHEFE)).isFalse();
        assertThat(questionarios.detalharPara(pares, TipoAvaliador.CHEFE).competencias()).isEmpty();
    }

    @Test
    void dadosDeExemploTemUmQuestionarioPublicadoCompletoPorCargo() {
        for (CargoResumo c : usuarios.cargos()) {
            var lista = questionarios.publicadosDoCargo(c.id());
            assertThat(lista).as(c.nome()).isNotEmpty();
            assertThat(questionarios.pendencias(lista.get(0).id())).isEmpty();
            assertThat(lista.get(0).escalaMax()).isEqualTo(5);
        }
        assertThat(questionarios.contarRascunhos()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void competenciaNovaSemSairDoFluxoEPadraoDoCargo() throws Exception {
        Long cargo = cargo("Analista de Suporte");
        Long id = questionarios.criarEmBranco("Com competência nova", cargo, 5);
        Long atendimento = competencia("Atendimento ao cliente");

        mvc.perform(post("/competencias/questionarios/" + id).with(como(RH)).with(csrf())
                        .param("nome", "Com competência nova").param("cargoId", cargo.toString()).param("escalaMax", "5")
                        .param("competencias", atendimento.toString())
                        .param("padraoDoCargo", "true")
                        .param("novaNome", "Negociação").param("novaDescricao", "Busca acordos bons para todos.")
                        .param("novaTipo", "COMPORTAMENTAL")
                        .param("acao", "nova-competencia"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("sucesso"));

        Long negociacao = catalogo.competenciasAtivas().stream().filter(c -> c.nome().equals("Negociação"))
                .findFirst().orElseThrow().id();
        assertThat(questionarios.detalhar(id).competenciaIds()).containsExactly(atendimento, negociacao);
        assertThat(catalogo.padraoDoCargo(cargo)).containsExactly(atendimento);

        // 9b no fluxo: nome repetido usa a competência existente.
        mvc.perform(post("/competencias/questionarios/" + id).with(como(RH)).with(csrf())
                        .param("nome", "Com competência nova").param("cargoId", cargo.toString())
                        .param("competencias", atendimento.toString(), negociacao.toString())
                        .param("novaNome", "comunicação").param("novaDescricao", "repetida").param("novaTipo", "COMPORTAMENTAL")
                        .param("acao", "nova-competencia"))
                .andExpect(flash().attributeExists("aviso"));
        assertThat(questionarios.detalhar(id).competenciaIds()).contains(competencia("Comunicação"));
    }

    @Test
    void paginasDoRhAbrem() throws Exception {
        mvc.perform(get("/competencias").with(como(RH))).andExpect(redirectedUrl("/competencias/catalogo"));
        for (String url : List.of("/competencias/questionarios", "/competencias/questionarios/novo", "/competencias/catalogo",
                "/competencias/catalogo/nova", "/competencias/catalogo?busca=lider")) {
            mvc.perform(get(url).with(como(RH))).andExpect(status().isOk());
        }
        Long modelo = questionarios.modelos().get(0).id();
        mvc.perform(get("/competencias/questionarios/" + modelo + "/previa").with(como(RH)))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Usar este modelo")));
        Long publicado = questionarios.publicados().get(0).id();
        for (TipoAvaliador t : TipoAvaliador.values()) {
            mvc.perform(get("/competencias/questionarios/" + publicado + "/previa").param("como", t.name()).with(como(RH)))
                    .andExpect(status().isOk());
        }
        mvc.perform(get("/").with(como(RH)))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Questionários publicados")));
    }

    @Test
    void quemNaoERhRecebe403() throws Exception {
        for (String email : List.of("colaborador@exemplo.com", "gestor@exemplo.com")) {
            for (String url : List.of("/competencias", "/competencias/catalogo", "/competencias/questionarios",
                    "/competencias/questionarios/novo")) {
                mvc.perform(get(url).with(como(email))).andExpect(status().isForbidden());
            }
        }
        Long id = questionarios.anteriores().get(0).id();
        mvc.perform(post("/competencias/questionarios/" + id + "/duplicar").with(como("colaborador@exemplo.com")).with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ---- apoio ----

    private Long cargo(String nome) {
        return usuarios.cargos().stream().filter(c -> c.nome().equals(nome)).findFirst().orElseThrow().id();
    }

    private Long competencia(String nome) {
        return catalogo.competenciasAtivas().stream().filter(c -> c.nome().equals(nome)).findFirst().orElseThrow().id();
    }

    private static List<String> nomes(QuestionarioCompleto q) {
        return q.competencias().stream().map(CompetenciaDoQuestionario::nome).toList();
    }

    static Long idNoFim(String url) {
        return Long.valueOf(url.substring(url.lastIndexOf('/') + 1));
    }
}
