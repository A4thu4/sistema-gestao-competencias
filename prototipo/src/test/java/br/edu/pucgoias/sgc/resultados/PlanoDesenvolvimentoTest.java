package br.edu.pucgoias.sgc.resultados;

import br.edu.pucgoias.sgc.resultados.dominio.MetaAtualizacao;
import br.edu.pucgoias.sgc.resultados.dominio.MetaDesenvolvimento;
import br.edu.pucgoias.sgc.resultados.dominio.SituacaoMeta;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaAtualizacaoRepository;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaDesenvolvimentoRepository;
import br.edu.pucgoias.sgc.resultados.servico.FontePlano;
import br.edu.pucgoias.sgc.resultados.servico.PlanoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** UC-06 Gerenciar plano de desenvolvimento (RF-27, RF-28) e a visibilidade dos planos (RNF-03). */
class PlanoDesenvolvimentoTest extends CenarioResultados {

    @Autowired
    private MetaDesenvolvimentoRepository metas;
    @Autowired
    private MetaAtualizacaoRepository atualizacoes;
    @Autowired
    private PlanoService planos;
    @Autowired
    private FontePlano fontePlano;

    @Test
    void gestorVeSugestoesECriaMetaParaAlguemDaEquipe() throws Exception {
        mvc.perform(get("/planos").with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(not(containsString("Igor Matos"))));
        mvc.perform(get("/planos/" + carla.id()).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Competências sugeridas")))
                .andExpect(content().string(containsString("Trabalho em equipe")))
                .andExpect(content().string(containsString("Nova meta")));

        long avisos = emailsPara("colaborador@exemplo.com", "Nova meta");
        mvc.perform(post("/planos/" + carla.id() + "/metas").with(csrf()).with(como("gestor@exemplo.com"))
                        .param("competenciaId", trabalho.toString())
                        .param("descricao", "Combinar entregas com a equipe toda semana")
                        .param("acoes", "Curso de comunicação em equipe (8 h); reunião semanal de alinhamento")
                        .param("prazo", LocalDate.now().plusMonths(3).toString())
                        .param("situacao", "NAO_INICIADA"))
                .andExpect(redirectedUrl("/planos/" + carla.id()))
                .andExpect(flash().attributeExists("sucesso"));

        MetaDesenvolvimento meta = metaDaCarla("Combinar entregas");
        assertThat(meta.getGestorId()).isEqualTo(bruno.id());
        assertThat(meta.getCicloId()).isEqualTo(ciclo);
        assertThat(meta.getSituacao()).isEqualTo(SituacaoMeta.NAO_INICIADA);
        assertThat(emailsPara("colaborador@exemplo.com", "Nova meta")).isEqualTo(avisos + 1);
        assertThat(acoesAuditadas()).anyMatch(a -> a.startsWith("META_CRIADA") && a.contains("Carla Dias"));
    }

    @Test
    void planoDeExemploDaCarlaTemMetasDaCompetencia() throws Exception {
        mvc.perform(get("/planos/" + carla.id()).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dominar o módulo fiscal do produto")))
                .andExpect(content().string(containsString("Conhecimento do produto")))
                .andExpect(content().string(not(containsString("Participar mais das decisões"))));
    }

    @Test
    void metaInvalidaVoltaAoFormularioComAMensagem() throws Exception {
        mvc.perform(post("/planos/" + carla.id() + "/metas").with(csrf()).with(como("gestor@exemplo.com"))
                        .param("competenciaId", trabalho.toString())
                        .param("descricao", "Ler um livro por mes")
                        .param("acoes", "")
                        .param("prazo", LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("erro", containsString("ações previstas")))
                // o formulário volta preenchido
                .andExpect(content().string(containsString("Ler um livro por mes")));
    }

    @Test
    void semResultadoLiberadoNaoHaPlano() throws Exception {
        liberado(false);
        mvc.perform(get("/planos/" + carla.id()).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("depois que o resultado do ciclo é liberado")));
        mvc.perform(post("/planos/" + carla.id() + "/metas").with(csrf()).with(como("gestor@exemplo.com"))
                        .param("competenciaId", trabalho.toString())
                        .param("descricao", "Meta")
                        .param("acoes", "Curso")
                        .param("prazo", LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("erro", containsString("liberado pelo RH")));
    }

    @Test
    void colaboradorAtualizaAMetaEOHistoricoFicaGravado() throws Exception {
        MetaDesenvolvimento meta = novaMeta(OffsetDateTime.now());

        mvc.perform(get("/planos").with(como("colaborador@exemplo.com")))
                .andExpect(redirectedUrl("/planos/" + carla.id()));
        mvc.perform(post("/planos/metas/" + meta.getId() + "/atualizacoes").with(csrf())
                        .with(como("colaborador@exemplo.com"))
                        .param("situacao", "EM_ANDAMENTO")
                        .param("progresso", "Terminei o primeiro módulo do curso"))
                .andExpect(redirectedUrlPattern("/planos/" + carla.id() + "#meta-*"));
        mvc.perform(post("/planos/metas/" + meta.getId() + "/atualizacoes").with(csrf())
                        .with(como("gestor@exemplo.com"))
                        .param("situacao", "EM_ANDAMENTO")
                        .param("progresso", "Conversamos na reunião de acompanhamento"))
                .andExpect(redirectedUrlPattern("/planos/" + carla.id() + "#meta-*"));

        List<MetaAtualizacao> historico = atualizacoes.findByMetaIdInOrderByCriadaEmDescIdDesc(List.of(meta.getId()));
        assertThat(historico).hasSize(2);
        assertThat(historico).extracting(MetaAtualizacao::getAutorId).containsExactly(bruno.id(), carla.id());
        assertThat(historico).allMatch(a -> a.getCriadaEm() != null && a.getSituacao() == SituacaoMeta.EM_ANDAMENTO);
        assertThat(metas.findById(meta.getId()).orElseThrow().getSituacao()).isEqualTo(SituacaoMeta.EM_ANDAMENTO);

        mvc.perform(get("/planos/" + carla.id()).with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Meu plano de desenvolvimento")))
                .andExpect(content().string(containsString("Terminei o primeiro módulo do curso")))
                .andExpect(content().string(containsString("Bruno Lima")))
                .andExpect(content().string(not(containsString("Registrar meta"))));

        // Progresso vazio não é gravado.
        mvc.perform(post("/planos/metas/" + meta.getId() + "/atualizacoes").with(csrf())
                        .with(como("colaborador@exemplo.com")).param("situacao", "CONCLUIDA").param("progresso", " "))
                .andExpect(flash().attribute("erro", containsString("progresso")));
        assertThat(fontePlano.itens(carla.id())).anyMatch(i -> i.contains("Terminei o primeiro módulo do curso"));
    }

    @Test
    void outrasPessoasNaoVeemNemAlteramOPlano() throws Exception {
        MetaDesenvolvimento meta = novaMeta(OffsetDateTime.now());
        mvc.perform(get("/planos/" + carla.id()).with(como("igor@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Combinar entregas"))));
        mvc.perform(get("/planos/" + carla.id()).with(como("diego@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/planos/" + carla.id()).with(como("gustavo@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/planos/metas/" + meta.getId() + "/atualizacoes").with(csrf()).with(como("igor@exemplo.com"))
                        .param("situacao", "CONCLUIDA").param("progresso", "Tentativa"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/planos/" + carla.id() + "/metas").with(csrf()).with(como("colaborador@exemplo.com"))
                        .param("competenciaId", trabalho.toString()).param("descricao", "x").param("acoes", "y")
                        .param("prazo", LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().isForbidden());
        mvc.perform(post("/planos/" + carla.id() + "/metas").with(csrf()).with(como("gustavo@exemplo.com"))
                        .param("competenciaId", trabalho.toString()).param("descricao", "x").param("acoes", "y")
                        .param("prazo", LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().isForbidden());

        // RH consulta, mas não altera.
        mvc.perform(get("/planos/" + carla.id()).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Consulta do RH")))
                .andExpect(content().string(containsString("Combinar entregas")))
                .andExpect(content().string(not(containsString("Salvar atualização"))));
        mvc.perform(post("/planos/metas/" + meta.getId() + "/atualizacoes").with(csrf()).with(como("rh@exemplo.com"))
                        .param("situacao", "CONCLUIDA").param("progresso", "Tentativa"))
                .andExpect(status().isForbidden());
        assertThat(atualizacoes.findByMetaIdInOrderByCriadaEmDescIdDesc(List.of(meta.getId()))).isEmpty();
    }

    @Test
    void lembreteTrimestralParaMetaParadaHaMaisDe90DiasSemRepetir() {
        OffsetDateTime agora = OffsetDateTime.now();
        MetaDesenvolvimento parada = novaMeta(agora.minusDays(100));
        MetaDesenvolvimento comAtualizacaoRecente = novaMeta(agora.minusDays(120));
        atualizacoes.save(new MetaAtualizacao(comAtualizacaoRecente.getId(), carla.id(), SituacaoMeta.EM_ANDAMENTO,
                "Andando", agora.minusDays(10)));
        MetaDesenvolvimento concluida = novaMeta(agora.minusDays(200));
        concluida.mudarSituacao(SituacaoMeta.CONCLUIDA);
        metas.flush();
        long paraCarla = emailsPara("colaborador@exemplo.com", "Lembrete");
        long paraBruno = emailsPara("gestor@exemplo.com", "Lembrete");

        int lembradas = planos.enviarLembretesTrimestrais(agora);

        assertThat(lembradas).isGreaterThanOrEqualTo(1);
        assertThat(metas.findById(parada.getId()).orElseThrow().getUltimoLembreteEm()).isNotNull();
        assertThat(metas.findById(comAtualizacaoRecente.getId()).orElseThrow().getUltimoLembreteEm()).isNull();
        assertThat(metas.findById(concluida.getId()).orElseThrow().getUltimoLembreteEm()).isNull();
        assertThat(emailsPara("colaborador@exemplo.com", "Lembrete")).isEqualTo(paraCarla + 1);
        assertThat(emailsPara("gestor@exemplo.com", "Lembrete")).isEqualTo(paraBruno + 1);

        // No dia seguinte não repete (lembrete recente).
        assertThat(planos.enviarLembretesTrimestrais(agora.plusDays(1))).isZero();
        assertThat(emailsPara("colaborador@exemplo.com", "Lembrete")).isEqualTo(paraCarla + 1);
        // Depois de mais 90 dias sem atualização, lembra de novo.
        assertThat(planos.enviarLembretesTrimestrais(agora.plusDays(91))).isGreaterThanOrEqualTo(1);
    }

    @Test
    void gestorEnviaOLembreteDeRevisaoNaHora() throws Exception {
        novaMeta(OffsetDateTime.now());
        long paraCarla = emailsPara("colaborador@exemplo.com", "Lembrete");
        mvc.perform(post("/planos/" + carla.id() + "/lembrete").with(csrf()).with(como("gestor@exemplo.com")))
                .andExpect(redirectedUrl("/planos/" + carla.id()))
                .andExpect(flash().attribute("sucesso", containsString("Lembrete de revisão enviado")));
        assertThat(emailsPara("colaborador@exemplo.com", "Lembrete")).isEqualTo(paraCarla + 1);
        mvc.perform(post("/planos/" + carla.id() + "/lembrete").with(csrf()).with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void paginaInicialDoGestorMostraMetasDaEquipe() throws Exception {
        MetaDesenvolvimento meta = novaMeta(OffsetDateTime.now());
        meta.mudarSituacao(SituacaoMeta.EM_ANDAMENTO);
        metas.flush();
        mvc.perform(get("/").with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Metas da equipe")));
        mvc.perform(get("/").with(como("colaborador@exemplo.com")))
                .andExpect(content().string(containsString("Meu plano de desenvolvimento")));
    }

    private MetaDesenvolvimento novaMeta(OffsetDateTime criadaEm) {
        return metas.saveAndFlush(new MetaDesenvolvimento(carla.id(), bruno.id(), ciclo, trabalho,
                "Combinar entregas com a equipe", "Curso de trabalho em equipe", LocalDate.now().plusMonths(3),
                SituacaoMeta.NAO_INICIADA, criadaEm));
    }

    private MetaDesenvolvimento metaDaCarla(String trecho) {
        return metas.findByColaboradorIdOrderByPrazoAscIdAsc(carla.id()).stream()
                .filter(m -> m.getDescricao().contains(trecho)).findFirst().orElseThrow();
    }
}
