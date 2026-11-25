package br.edu.pucgoias.sgc.ciclos;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.ciclos.servico.AvaliadoNoCiclo;
import br.edu.pucgoias.sgc.ciclos.servico.AvaliadorNoCiclo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloEncerradoEvento;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.Datas;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.ciclos.servico.MontagemCiclo;
import br.edu.pucgoias.sgc.ciclos.servico.MontagemCicloService;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.competencias.servico.DadosPergunta;
import br.edu.pucgoias.sgc.competencias.servico.DadosQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** UC-02 Criar ciclo de avaliação: cenário principal, extensões 6a, 8a, 8b, 9a, 9b, 10a, 10b, 13a, 2b e 2c. */
@RecordApplicationEvents
class CriarCicloTest extends TesteIntegracao {

    private static final String RH = "rh@exemplo.com";

    @Autowired
    private CicloService ciclos;
    @Autowired
    private MontagemCicloService montagem;
    @Autowired
    private UsuarioService usuarios;
    @Autowired
    private EmailService emails;
    @Autowired
    private QuestionarioService questionarios;
    @Autowired
    private CompetenciaService catalogo;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private ApplicationEvents eventos;

    @Test
    void rhCriaEAgendaUmCicloValido() throws Exception {
        LocalDate abertura = Datas.hoje().plusDays(5);
        Long id = criarPelaTela("Ciclo de teste", abertura, abertura.plusDays(20), "CHEFE", "AUTO");

        mvc.perform(post("/ciclos/" + id + "/avaliados").with(como(RH)).with(csrf())
                        .param("pessoas", ids("colaborador", "diego", "elisa", "fabio")))
                .andExpect(redirectedUrl("/ciclos/" + id + "/avaliadores"));

        // Passo 8: chefe = gestor direto (Bruno) e a autoavaliação.
        MontagemCiclo m = montagem.montagem(id);
        assertThat(m.avaliados()).hasSize(4);
        PessoaResumo bruno = pessoa("gestor");
        for (AvaliadoNoCiclo a : m.avaliados()) {
            assertThat(a.avaliadores(TipoAvaliador.CHEFE)).extracting(AvaliadorNoCiclo::pessoaId).containsExactly(bruno.id());
            assertThat(a.avaliadores(TipoAvaliador.AUTO)).extracting(AvaliadorNoCiclo::pessoaId).containsExactly(a.id());
            assertThat(a.questionarioNome()).isEqualTo("Questionário 2026 — Analista de Suporte");
        }
        assertThat(m.verificacao().podeAgendar()).isTrue();

        mvc.perform(get("/ciclos/" + id).with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Agendar ciclo")))
                .andExpect(content().string(containsString("Pronto para agendar")));
        int antes = emails.enviadosPara("elisa@exemplo.com").size();
        mvc.perform(post("/ciclos/" + id + "/agendar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/ciclos/" + id))
                .andExpect(flash().attribute("sucesso", containsString("abre sozinho em " + Datas.data(abertura))));

        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("AGENDADO");
        // Nenhum convite antes da data de abertura.
        assertThat(emails.enviadosPara("elisa@exemplo.com")).hasSize(antes);
        mvc.perform(get("/ciclos").with(como(RH)))
                .andExpect(content().string(containsString("Ciclo de teste")));
        // O questionário passa a constar como usado pelo ciclo (UC-01, 2b).
        Long q = ciclos.questionarioDoAvaliado(id, pessoa("elisa").id());
        assertThat(questionarios.uso(q).ciclos()).contains("Ciclo de teste");
    }

    @Test
    void dadosInvalidosSaoDestacadosENadaEGravado() throws Exception {
        int antes = montagem.listar().size();
        LocalDate ontem = Datas.hoje().minusDays(1);
        mvc.perform(post("/ciclos/novo").with(como(RH)).with(csrf())
                        .param("nome", "  ").param("abertura", ontem.toString()).param("encerramento", ontem.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dê um nome ao ciclo.")))
                .andExpect(content().string(containsString("Marque ao menos um tipo de avaliação.")))
                .andExpect(content().string(containsString("A abertura não pode ser anterior a hoje.")))
                .andExpect(content().string(containsString("O encerramento deve ser depois da abertura.")));
        assertThat(montagem.listar()).hasSize(antes);
    }

    @Test
    void cargoSemQuestionarioETipoSemPerguntasImpedemOAgendamento() throws Exception {
        Long cargo = jdbc.queryForObject("insert into cargo (nome) values ('Estagiário de Suporte') returning id", Long.class);
        Long estagiario = jdbc.queryForObject("""
                insert into usuario (nome, email, senha_hash, perfil, cargo_id, gestor_id, area)
                values ('Lia Estagiária', 'lia@exemplo.com', 'x', 'COLABORADOR', ?, ?, 'Suporte') returning id""",
                Long.class, cargo, pessoa("gestor").id());
        LocalDate abertura = Datas.hoje().plusDays(3);
        Long id = criarPelaTela("Ciclo com estagiária", abertura, abertura.plusDays(10), "CHEFE", "PAR");

        // 8a: o cargo não tem questionário publicado.
        mvc.perform(post("/ciclos/" + id + "/avaliados").with(como(RH)).with(csrf())
                        .param("pessoas", estagiario.toString(), pessoa("diego").id().toString()))
                .andExpect(redirectedUrl("/ciclos/" + id + "/avaliados"))
                .andExpect(flash().attributeExists("erro"));
        MontagemCiclo m = montagem.montagem(id);
        assertThat(m.verificacao().semQuestionario()).extracting(s -> s.cargoNome()).containsExactly("Estagiário de Suporte");
        mvc.perform(get("/ciclos/" + id + "/avaliados").with(como(RH)))
                .andExpect(content().string(containsString("Tirar essas pessoas do ciclo")));
        mvc.perform(post("/ciclos/" + id + "/agendar").with(como(RH)).with(csrf()))
                .andExpect(flash().attributeExists("erro"));
        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("RASCUNHO");

        // Publica um questionário só com perguntas para o chefe: vira 8b (sem perguntas para pares).
        Long comunicacao = catalogo.competenciasAtivas().stream().filter(c -> c.nome().equals("Comunicação"))
                .findFirst().orElseThrow().id();
        Long q = questionarios.criarEmBranco("Questionário do estágio", cargo, 5);
        questionarios.salvar(q, new DadosQuestionario("Questionário do estágio", cargo, 5, List.of(comunicacao),
                List.of(new DadosPergunta(null, comunicacao, "Comunica-se com clareza?", "ESCALA", true,
                        Set.of(TipoAvaliador.CHEFE))),
                List.of("Nunca", "Raramente", "Às vezes", "Frequentemente", "Sempre"), false));
        assertThat(questionarios.publicar(q)).isEmpty();
        montagem.salvarAvaliados(id, List.of(estagiario, pessoa("diego").id()), null, null, java.util.Map.of());
        m = montagem.montagem(id);
        assertThat(m.verificacao().semQuestionario()).isEmpty();
        assertThat(m.verificacao().semPerguntas()).anySatisfy(s -> {
            assertThat(s.tipo()).isEqualTo(TipoAvaliador.PAR);
            assertThat(s.cargoNome()).isEqualTo("Estagiário de Suporte");
        });
        assertThat(m.verificacao().podeAgendar()).isFalse();

        // Oferta da extensão 8b: retirar o tipo do ciclo.
        mvc.perform(post("/ciclos/" + id + "/tipos/retirar").with(como(RH)).with(csrf()).param("tipo", "PAR"))
                .andExpect(redirectedUrl("/ciclos/" + id + "/avaliados"));
        assertThat(ciclos.ciclo(id).tiposAtivos()).containsExactly(TipoAvaliador.CHEFE);
        assertThat(montagem.montagem(id).verificacao().podeAgendar()).isTrue();

        // Oferta da extensão 8a: tirar as pessoas do cargo.
        montagem.removerCargo(id, cargo);
        assertThat(ciclos.avaliadosDoCiclo(id)).containsExactly(pessoa("diego").id());
    }

    @Test
    void avaliadoSemAvaliadorImpedeOAgendamentoAteORhIndicar() throws Exception {
        LocalDate abertura = Datas.hoje().plusDays(2);
        Long id = criarPelaTela("Ciclo da gerência", abertura, abertura.plusDays(10), "CHEFE", "AUTO");
        PessoaResumo helena = pessoa("helena");
        montagem.salvarAvaliados(id, List.of(helena.id()), null, null, java.util.Map.of());

        // 9a: Helena não tem chefe; fica pendente e 10b impede o agendamento.
        AvaliadoNoCiclo h = montagem.montagem(id).avaliados().get(0);
        assertThat(h.pendente()).isTrue();
        assertThat(h.pendencias()).anyMatch(p -> p.contains("sem chefe"));
        mvc.perform(get("/ciclos/" + id + "/avaliadores").with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pendente de avaliadores")));
        mvc.perform(post("/ciclos/" + id + "/agendar").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/ciclos/" + id))
                .andExpect(flash().attribute("erro", containsString("pendência")));
        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("RASCUNHO");

        // Ninguém avalia a si mesmo fora da autoavaliação.
        mvc.perform(post("/ciclos/" + id + "/avaliadores/incluir").with(como(RH)).with(csrf())
                        .param("avaliadoId", helena.id().toString()).param("tipo", "CHEFE")
                        .param("avaliadorId", helena.id().toString()))
                .andExpect(flash().attribute("erro", containsString("a si mesmo")));

        // O RH indica o avaliador manualmente.
        mvc.perform(post("/ciclos/" + id + "/avaliadores/incluir").with(como(RH)).with(csrf())
                        .param("avaliadoId", helena.id().toString()).param("tipo", "CHEFE")
                        .param("avaliadorId", pessoa("rh").id().toString()))
                .andExpect(redirectedUrl("/ciclos/" + id + "/avaliadores#avaliado-" + helena.id()))
                .andExpect(flash().attributeExists("sucesso"));
        mvc.perform(post("/ciclos/" + id + "/agendar").with(como(RH)).with(csrf()))
                .andExpect(flash().attributeExists("sucesso"));
        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("AGENDADO");

        // 2a: alterar o ciclo agendado o devolve a rascunho.
        mvc.perform(post("/ciclos/" + id + "/dados").with(como(RH)).with(csrf())
                        .param("nome", "Ciclo da gerência 2").param("tipos", "CHEFE", "AUTO")
                        .param("abertura", abertura.toString()).param("encerramento", abertura.plusDays(15).toString()))
                .andExpect(redirectedUrl("/ciclos/" + id + "/avaliados"))
                .andExpect(flash().attributeExists("aviso"));
        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("RASCUNHO");
        assertThat(ciclos.ciclo(id).nome()).isEqualTo("Ciclo da gerência 2");
    }

    @Test
    void sugereChefeParesESubordinadosPelaEstruturaEAlertaMenosDeTres() {
        Long id = montagem.criar(new br.edu.pucgoias.sgc.ciclos.servico.DadosCiclo("Ciclo 360",
                Set.of(TipoAvaliador.CHEFE, TipoAvaliador.AUTO, TipoAvaliador.PAR, TipoAvaliador.SUBORDINADO),
                Datas.hoje().plusDays(1), Datas.hoje().plusDays(20)));
        PessoaResumo bruno = pessoa("gestor");
        PessoaResumo carla = pessoa("colaborador");
        montagem.salvarAvaliados(id, List.of(bruno.id(), carla.id()), null, null, java.util.Map.of());
        MontagemCiclo m = montagem.montagem(id);

        AvaliadoNoCiclo b = m.avaliados().stream().filter(a -> a.id().equals(bruno.id())).findFirst().orElseThrow();
        assertThat(nomes(b, TipoAvaliador.CHEFE)).containsExactly("Helena Prado");
        assertThat(nomes(b, TipoAvaliador.AUTO)).containsExactly("Bruno Lima");
        assertThat(nomes(b, TipoAvaliador.PAR)).containsExactlyInAnyOrder("Ana Souza", "Gustavo Reis");
        assertThat(nomes(b, TipoAvaliador.SUBORDINADO))
                .containsExactlyInAnyOrder("Carla Dias", "Diego Rocha", "Elisa Martins", "Fábio Nunes");
        // 10a: 2 pares só gera alerta (RNF-02), sem impedir.
        assertThat(b.alertas()).anyMatch(a -> a.contains("2 par(es)"));

        AvaliadoNoCiclo c = m.avaliados().stream().filter(a -> a.id().equals(carla.id())).findFirst().orElseThrow();
        assertThat(nomes(c, TipoAvaliador.CHEFE)).containsExactly("Bruno Lima");
        assertThat(nomes(c, TipoAvaliador.PAR)).containsExactlyInAnyOrder("Diego Rocha", "Elisa Martins", "Fábio Nunes");
        // Subordinados não se aplica a quem não tem equipe.
        assertThat(c.temEquipe()).isFalse();
        assertThat(c.pendencias()).isEmpty();
        assertThat(m.verificacao().podeAgendar()).isTrue();
    }

    @Test
    void avaliadorExternoRecebeTokenSeguroEEmailInvalidoERecusado() throws Exception {
        Long id = montagem.criar(new br.edu.pucgoias.sgc.ciclos.servico.DadosCiclo("Ciclo com cliente",
                Set.of(TipoAvaliador.CHEFE), Datas.hoje().plusDays(1), Datas.hoje().plusDays(20)));
        PessoaResumo elisa = pessoa("elisa");
        montagem.salvarAvaliados(id, List.of(elisa.id()), null, null, java.util.Map.of());

        mvc.perform(post("/ciclos/" + id + "/avaliadores/externo").with(como(RH)).with(csrf())
                        .param("avaliadoId", elisa.id().toString()).param("nome", "Cliente X").param("email", "sem-arroba"))
                .andExpect(flash().attribute("erro", containsString("e-mail válido")));
        assertThat(ciclos.designacoesDoCiclo(id)).noneMatch(DesignacaoResumo::externa);

        mvc.perform(post("/ciclos/" + id + "/avaliadores/externo").with(como(RH)).with(csrf())
                        .param("avaliadoId", elisa.id().toString()).param("nome", "Paula Cliente")
                        .param("email", "paula@cliente.com.br"))
                .andExpect(flash().attributeExists("sucesso"));
        DesignacaoResumo externa = ciclos.designacoesDoCiclo(id).stream().filter(DesignacaoResumo::externa)
                .findFirst().orElseThrow();
        assertThat(externa.relacao()).isEqualTo(TipoAvaliador.EXTERNO);
        assertThat(externa.avaliadorId()).isNull();
        assertThat(externa.externoEmail()).isEqualTo("paula@cliente.com.br");

        String token = jdbc.queryForObject("select token from designacao where id = ?", String.class, externa.id());
        assertThat(token).matches("[A-Za-z0-9_-]{43,}");
        assertThat(ciclos.designacaoPorToken(token)).map(DesignacaoResumo::id).contains(externa.id());
        assertThat(ciclos.designacaoPorToken("token-que-nao-existe")).isEmpty();
        assertThat(ciclos.ciclo(id).tiposAtivos()).contains(TipoAvaliador.EXTERNO);
    }

    @Test
    void aberturaHojeAbreNaHoraEEnviaOsConvites() throws Exception {
        LocalDate hoje = Datas.hoje();
        Long id = criarPelaTela("Ciclo relâmpago", hoje, hoje.plusDays(7), "CHEFE", "AUTO");
        PessoaResumo fabio = pessoa("fabio");
        montagem.salvarAvaliados(id, List.of(fabio.id()), null, null, java.util.Map.of());
        montagem.incluirExterno(id, fabio.id(), "Rui Fornecedor", "rui@fornecedor.com");
        int convitesBruno = emails.enviadosPara("gestor@exemplo.com").size();

        mvc.perform(post("/ciclos/" + id + "/agendar").with(como(RH)).with(csrf()))
                .andExpect(flash().attribute("sucesso", containsString("aberto agora")));

        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("ABERTO");
        assertThat(ciclos.aceitaRespostas(id)).isTrue();
        assertThat(emails.enviadosPara("fabio@exemplo.com"))
                .anySatisfy(e -> assertThat(e.getAssunto()).isEqualTo("Convite para avaliação: Ciclo relâmpago"));
        assertThat(emails.enviadosPara("gestor@exemplo.com")).hasSize(convitesBruno + 1);
        assertThat(emails.enviadosPara("rui@fornecedor.com"))
                .anySatisfy(e -> assertThat(e.getCorpo()).contains("/externo/"));
        assertThat(montagem.montagem(id).avaliados().get(0).porTipo().values().stream().flatMap(List::stream))
                .allMatch(AvaliadorNoCiclo::conviteEnviado);
        assertThat(montagem.montagem(id).convitesPendentes()).isZero();
        mvc.perform(get("/auditoria").with(como(RH)))
                .andExpect(content().string(containsString("Abriu ciclo")));

        // 2b: com o ciclo aberto, só se inclui avaliador (convite na hora) e se muda o encerramento.
        PessoaResumo elisa = pessoa("elisa");
        mvc.perform(post("/ciclos/" + id + "/avaliadores/" + ciclos.designacoesDoCiclo(id).get(0).id() + "/remover")
                        .with(como(RH)).with(csrf()).param("avaliadoId", fabio.id().toString()))
                .andExpect(flash().attribute("erro", containsString("já está aberto")));
        mvc.perform(post("/ciclos/" + id + "/avaliadores/incluir").with(como(RH)).with(csrf())
                        .param("avaliadoId", fabio.id().toString()).param("tipo", "CHEFE")
                        .param("avaliadorId", elisa.id().toString()))
                .andExpect(flash().attribute("sucesso", containsString("convite foi enviado")));
        assertThat(emails.enviadosPara("elisa@exemplo.com"))
                .anySatisfy(e -> assertThat(e.getAssunto()).contains("Ciclo relâmpago"));
        mvc.perform(post("/ciclos/" + id + "/dados").with(como(RH)).with(csrf())
                        .param("encerramento", hoje.plusDays(15).toString()))
                .andExpect(redirectedUrl("/ciclos/" + id));
        assertThat(ciclos.ciclo(id).dataEncerramento()).isEqualTo(hoje.plusDays(15));
        assertThat(ciclos.ciclo(id).nome()).isEqualTo("Ciclo relâmpago");
    }

    @Test
    void encerrarAgoraPublicaOEventoEDeixaSoParaConsulta() throws Exception {
        CicloResumo aberto = cicloDeExemplo("Ciclo 2026.2");
        mvc.perform(post("/ciclos/" + aberto.id() + "/encerrar-agora").with(como(RH)).with(csrf()))
                .andExpect(redirectedUrl("/ciclos/" + aberto.id()))
                .andExpect(flash().attributeExists("sucesso"));

        assertThat(eventos.stream(CicloEncerradoEvento.class)).containsExactly(new CicloEncerradoEvento(aberto.id()));
        CicloResumo depois = ciclos.ciclo(aberto.id());
        assertThat(depois.situacao()).isEqualTo("ENCERRADO");
        assertThat(depois.dataEncerramento()).isEqualTo(aberto.dataEncerramento());
        assertThat(ciclos.aceitaRespostas(aberto.id())).isFalse();

        // 2c: encerrado só consulta.
        mvc.perform(get("/ciclos/" + aberto.id()).with(como(RH)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Disponível só para consulta")));
        mvc.perform(post("/ciclos/" + aberto.id() + "/dados").with(como(RH)).with(csrf())
                        .param("encerramento", Datas.hoje().plusDays(30).toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void abrirAgoraAbreUmCicloAgendadoSemMudarAsDatas() throws Exception {
        LocalDate abertura = Datas.hoje().plusDays(10);
        Long id = montagem.criar(new br.edu.pucgoias.sgc.ciclos.servico.DadosCiclo("Ciclo adiantado",
                Set.of(TipoAvaliador.AUTO), abertura, abertura.plusDays(10)));
        montagem.salvarAvaliados(id, List.of(pessoa("julia").id()), null, null, java.util.Map.of());
        montagem.agendar(id);

        mvc.perform(post("/ciclos/" + id + "/abrir-agora").with(como(RH)).with(csrf()))
                .andExpect(flash().attribute("sucesso", containsString("Ciclo aberto")));
        assertThat(ciclos.ciclo(id).situacao()).isEqualTo("ABERTO");
        assertThat(ciclos.ciclo(id).dataAbertura()).isEqualTo(abertura);
        assertThat(emails.enviadosPara("julia@exemplo.com"))
                .anySatisfy(e -> assertThat(e.getAssunto()).contains("Ciclo adiantado"));
    }

    @Test
    void telasDoRhAbremEOutrosPerfisSaoBarrados() throws Exception {
        CicloResumo rascunho = cicloDeExemplo("Ciclo 2027.1");
        for (String url : List.of("/ciclos", "/ciclos/novo", "/ciclos/" + rascunho.id(),
                "/ciclos/" + rascunho.id() + "/dados", "/ciclos/" + rascunho.id() + "/avaliados",
                "/ciclos/" + rascunho.id() + "/avaliadores")) {
            mvc.perform(get(url).with(como(RH))).andExpect(status().isOk());
        }
        for (String usuario : List.of("colaborador@exemplo.com", "gestor@exemplo.com")) {
            for (String url : List.of("/ciclos", "/ciclos/novo", "/ciclos/" + rascunho.id())) {
                mvc.perform(get(url).with(como(usuario))).andExpect(status().isForbidden());
            }
            mvc.perform(post("/ciclos/" + rascunho.id() + "/agendar").with(como(usuario)).with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    // ==== apoio ====

    private Long criarPelaTela(String nome, LocalDate abertura, LocalDate encerramento, String... tipos) throws Exception {
        MockHttpServletRequestBuilder req = post("/ciclos/novo").with(como(RH)).with(csrf())
                .param("nome", nome).param("tipos", tipos)
                .param("abertura", abertura.toString()).param("encerramento", encerramento.toString());
        MvcResult r = mvc.perform(req).andExpect(status().is3xxRedirection()).andReturn();
        String url = r.getResponse().getRedirectedUrl();
        assertThat(url).matches("/ciclos/\\d+/avaliados");
        return Long.valueOf(url.replaceAll("\\D", ""));
    }

    private PessoaResumo pessoa(String usuario) {
        return usuarios.buscarPorEmail(usuario + "@exemplo.com");
    }

    private String[] ids(String... usuarios) {
        return java.util.Arrays.stream(usuarios).map(u -> pessoa(u).id().toString()).toArray(String[]::new);
    }

    private CicloResumo cicloDeExemplo(String nome) {
        return montagem.listar().stream().map(l -> l.ciclo()).filter(c -> c.nome().equals(nome)).findFirst().orElseThrow();
    }

    private static List<String> nomes(AvaliadoNoCiclo a, TipoAvaliador t) {
        return a.avaliadores(t).stream().map(AvaliadorNoCiclo::nome).toList();
    }
}
