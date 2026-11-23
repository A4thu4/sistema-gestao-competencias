package br.edu.pucgoias.sgc.competencias;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.competencias.servico.ConsultaUsoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-01, extensão 2b: questionário com respostas ou em uso em ciclo não é alterado; o RH duplica. Usa uma
 * implementação de teste de {@link ConsultaUsoQuestionario}, no lugar das que os pacotes ciclos e avaliacoes vão dar.
 */
class QuestionarioEmUsoTest extends TesteIntegracao {

    private static final String RH = "rh@exemplo.com";

    @TestConfiguration
    static class Configuracao {
        @Bean
        UsoDeTeste usoDeTeste() {
            return new UsoDeTeste();
        }
    }

    static class UsoDeTeste implements ConsultaUsoQuestionario {
        final Set<Long> comRespostas = new HashSet<>();
        final Map<Long, List<String>> ciclos = new HashMap<>();

        @Override
        public boolean possuiRespostas(Long questionarioId) {
            return comRespostas.contains(questionarioId);
        }

        @Override
        public List<String> ciclosQueUsam(Long questionarioId) {
            return ciclos.getOrDefault(questionarioId, List.of());
        }
    }

    @Autowired
    private UsoDeTeste uso;
    @Autowired
    private QuestionarioService questionarios;

    @AfterEach
    void limpar() {
        uso.comRespostas.clear();
        uso.ciclos.clear();
    }

    @Test
    void questionarioEmCicloNaoPodeSerEditadoMasPodeSerDuplicado() throws Exception {
        Long id = questionarios.publicados().get(0).id();
        uso.ciclos.put(id, List.of("Ciclo 2026"));

        mvc.perform(get("/competencias/questionarios/" + id).with(como(RH)))
                .andExpect(redirectedUrl("/competencias/questionarios/" + id + "/previa"));
        mvc.perform(get("/competencias/questionarios/" + id + "/previa").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ciclo 2026")))
                .andExpect(content().string(containsString("Duplicar como novo questionário")));
        mvc.perform(get("/competencias").with(como(RH)))
                .andExpect(content().string(containsString("Ciclo 2026")));

        mvc.perform(post("/competencias/questionarios/" + id).with(como(RH)).with(csrf())
                        .param("nome", "Tentativa").param("escalaMax", "5"))
                .andExpect(status().isConflict());
        assertThat(questionarios.resumo(id).nome()).isNotEqualTo("Tentativa");
        assertThat(questionarios.resumo(id).publicado()).isTrue();

        MvcResult r = mvc.perform(post("/competencias/questionarios/" + id + "/duplicar").with(como(RH)).with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();
        Long copia = QuestionariosTest.idNoFim(r.getResponse().getRedirectedUrl());
        mvc.perform(get("/competencias/questionarios/" + copia).with(como(RH))).andExpect(status().isOk());
    }

    @Test
    void questionarioComRespostasFicaBloqueado() {
        Long id = questionarios.publicados().get(0).id();
        uso.comRespostas.add(id);
        assertThat(questionarios.uso(id).bloqueado()).isTrue();
        assertThat(questionarios.uso(id).motivo()).contains("já tem respostas");
        assertThat(questionarios.listar()).filteredOn(q -> q.id().equals(id)).allMatch(q -> q.bloqueado());
    }
}
