package br.edu.pucgoias.sgc.competencias.servico;

import br.edu.pucgoias.sgc.compartilhado.servico.RegistrosDaCarga;
import br.edu.pucgoias.sgc.identidade.servico.CargoResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.AUTO;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.CHEFE;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.PAR;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.SUBORDINADO;

/**
 * Catálogo de competências, padrão de cada cargo, os três modelos prontos, um questionário publicado para cada um
 * dos seis cargos da empresa de exemplo e um rascunho com pendências para a demonstração. Roda depois da carga do
 * identidade e só com o catálogo vazio.
 */
@Component
@Order(20)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploCompetencias implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosExemploCompetencias.class);

    /** Escala de frequência de 1 a 5, com a descrição do comportamento em cada nível (RF-04). */
    static final List<String> ESCALA_1_A_5 = List.of(
            "Nunca: não demonstra o comportamento, mesmo quando a situação pede.",
            "Raramente: demonstra o comportamento em poucas situações e costuma precisar de lembretes.",
            "Às vezes: demonstra o comportamento em parte das situações, ainda sem constância.",
            "Frequentemente: demonstra o comportamento na maior parte das situações.",
            "Sempre: demonstra o comportamento em todas as situações e serve de exemplo para os colegas.");

    private static final Set<TipoAvaliador> TODOS = EnumSet.allOf(TipoAvaliador.class);
    private static final Set<TipoAvaliador> SO_CHEFE = EnumSet.of(CHEFE);
    private static final Set<TipoAvaliador> LIDERADOS_E_CHEFE = EnumSet.of(SUBORDINADO, CHEFE);

    private record P(String texto, Set<TipoAvaliador> para) {
    }

    /** Perguntas de escala de cada competência. */
    private static final Map<String, List<P>> PERGUNTAS = new LinkedHashMap<>();

    static {
        PERGUNTAS.put("Comunicação", List.of(
                new P("Expressa as ideias com clareza, por escrito e oralmente.", TODOS),
                new P("Ouve com atenção antes de responder e confere se foi bem entendido.", TODOS)));
        PERGUNTAS.put("Trabalho em equipe", List.of(
                new P("Colabora com os colegas e compartilha o que sabe.", TODOS),
                new P("Lida com divergências de forma respeitosa e ajuda a chegar a um acordo.", TODOS)));
        PERGUNTAS.put("Orientação a resultados", List.of(
                new P("Cumpre os prazos e entrega o que foi combinado com qualidade.", TODOS),
                new P("Atinge as metas combinadas com a gestão no período.", SO_CHEFE)));
        PERGUNTAS.put("Adaptabilidade", List.of(
                new P("Reage bem a mudanças de prioridade, de processo ou de ferramenta.", TODOS)));
        PERGUNTAS.put("Pensamento analítico", List.of(
                new P("Usa dados e fatos para entender os problemas e embasar decisões.", TODOS),
                new P("Divide problemas complexos em partes e analisa as alternativas antes de agir.", TODOS)));
        PERGUNTAS.put("Liderança", List.of(
                new P("Dá retorno frequente e claro sobre o trabalho de cada pessoa da equipe.", LIDERADOS_E_CHEFE),
                new P("Apoia o desenvolvimento da equipe e cria um ambiente de confiança.", LIDERADOS_E_CHEFE)));
        PERGUNTAS.put("Atendimento ao cliente", List.of(
                new P("Atende os clientes com cordialidade e paciência.", TODOS),
                new P("Resolve a solicitação do cliente no primeiro contato sempre que possível.", TODOS)));
        PERGUNTAS.put("Conhecimento do produto", List.of(
                new P("Conhece as funcionalidades do produto e orienta o cliente corretamente.", TODOS),
                new P("Mantém-se atualizado sobre as novidades e mudanças do produto.", SO_CHEFE)));
        PERGUNTAS.put("Qualidade do código", List.of(
                new P("Entrega código legível, organizado e com testes.", TODOS),
                new P("Revisa o código dos colegas com cuidado e dá sugestões construtivas.", TODOS)));
        PERGUNTAS.put("Resolução de problemas técnicos", List.of(
                new P("Investiga a causa dos problemas, e não só o sintoma.", TODOS),
                new P("Documenta as soluções para que a equipe aprenda com elas.", SO_CHEFE)));
    }

    private final CompetenciaService catalogo;
    private final QuestionarioService questionarios;
    private final UsuarioService usuarios;
    private final RegistrosDaCarga registros;

    public DadosExemploCompetencias(CompetenciaService catalogo, QuestionarioService questionarios,
                                    UsuarioService usuarios, RegistrosDaCarga registros) {
        this.catalogo = catalogo;
        this.questionarios = questionarios;
        this.usuarios = usuarios;
        this.registros = registros;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (catalogo.totalNoCatalogo() > 0) {
            return;
        }
        Map<String, Long> cargos = usuarios.cargos().stream()
                .collect(Collectors.toMap(CargoResumo::nome, CargoResumo::id));
        if (cargos.isEmpty()) {
            log.warn("Dados de exemplo de competências não carregados: não há cargos.");
            return;
        }

        Map<String, Long> c = new LinkedHashMap<>();
        cadastrar(c, "Comunicação", "Transmite ideias com clareza, ouve com atenção e adapta a mensagem a quem recebe.", "COMPORTAMENTAL");
        cadastrar(c, "Trabalho em equipe", "Colabora com colegas e outras áreas, compartilha informações e ajuda a atingir objetivos comuns.", "COMPORTAMENTAL");
        cadastrar(c, "Orientação a resultados", "Prioriza as entregas, cumpre prazos e acompanha metas com qualidade.", "COMPORTAMENTAL");
        cadastrar(c, "Adaptabilidade", "Lida bem com mudanças, aprende rápido e ajusta a forma de trabalhar a novos desafios.", "COMPORTAMENTAL");
        cadastrar(c, "Pensamento analítico", "Analisa dados e situações, identifica causas e embasa decisões em fatos.", "COMPORTAMENTAL");
        cadastrar(c, "Liderança", "Orienta e desenvolve a equipe, dá retorno frequente e cria um ambiente de confiança.", "COMPORTAMENTAL");
        cadastrar(c, "Atendimento ao cliente", "Atende com cordialidade, entende a necessidade do cliente e resolve no primeiro contato sempre que possível.", "TECNICA");
        cadastrar(c, "Conhecimento do produto", "Conhece as funcionalidades, os limites e o uso correto dos produtos da empresa.", "TECNICA");
        cadastrar(c, "Qualidade do código", "Escreve código legível, testado e alinhado aos padrões do time.", "TECNICA");
        cadastrar(c, "Resolução de problemas técnicos", "Investiga falhas, encontra a causa e aplica soluções que não voltam a ocorrer.", "TECNICA");

        Map<String, List<String>> porCargo = new LinkedHashMap<>();
        porCargo.put("Gerente de Operações", List.of("Liderança", "Orientação a resultados", "Pensamento analítico", "Comunicação", "Adaptabilidade"));
        porCargo.put("Analista de RH", List.of("Comunicação", "Trabalho em equipe", "Orientação a resultados", "Pensamento analítico"));
        porCargo.put("Coordenador de Suporte", List.of("Liderança", "Atendimento ao cliente", "Orientação a resultados", "Comunicação"));
        porCargo.put("Analista de Suporte", List.of("Atendimento ao cliente", "Conhecimento do produto", "Resolução de problemas técnicos", "Trabalho em equipe", "Comunicação"));
        porCargo.put("Coordenador de Desenvolvimento", List.of("Liderança", "Qualidade do código", "Orientação a resultados", "Comunicação"));
        porCargo.put("Desenvolvedor", List.of("Qualidade do código", "Resolução de problemas técnicos", "Trabalho em equipe", "Adaptabilidade", "Comunicação"));

        Long questionarioSuporte = null;
        int publicados = 0;
        RegistrosDaCarga.Marca antesDaPublicacao = registros.marcar();
        for (var e : porCargo.entrySet()) {
            Long cargoId = cargos.get(e.getKey());
            if (cargoId == null) {
                continue;
            }
            List<Long> ids = e.getValue().stream().map(c::get).toList();
            catalogo.definirPadraoDoCargo(cargoId, ids);
            String nome = "Questionário 2026 — " + e.getKey();
            Long id = questionarios.criarEmBranco(nome, cargoId, 5);
            questionarios.salvar(id, dados(nome, cargoId, e.getValue(), c, P::para, true));
            List<String> faltas = questionarios.publicar(id);
            if (!faltas.isEmpty()) {
                throw new IllegalStateException("Questionário de exemplo incompleto: " + faltas);
            }
            publicados++;
            if (e.getKey().equals("Analista de Suporte")) {
                questionarioSuporte = id;
            }
        }

        registros.datar(antesDaPublicacao, LocalDate.of(2026, 4, 22).atTime(15, 40)
                .atZone(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime());

        questionarios.criarModelo("Modelo chefe–colaborador", dados("Modelo chefe–colaborador", null,
                List.of("Comunicação", "Trabalho em equipe", "Orientação a resultados", "Adaptabilidade"), c,
                p -> EnumSet.of(CHEFE, AUTO), true));
        questionarios.criarModelo("Modelo de avaliação por pares", dados("Modelo de avaliação por pares", null,
                List.of("Trabalho em equipe", "Comunicação", "Adaptabilidade"), c, p -> EnumSet.of(PAR), false));
        questionarios.criarModelo("Modelo 360º", dados("Modelo 360º", null,
                List.of("Comunicação", "Trabalho em equipe", "Orientação a resultados", "Liderança"), c,
                p -> p.para().equals(SO_CHEFE) ? TODOS : p.para(), true));

        // Rascunho para a demonstração: cópia do questionário do Suporte para 2027, com uma competência nova ainda
        // sem perguntas (a validação aponta a pendência antes de publicar).
        if (questionarioSuporte != null) {
            Long rascunho = questionarios.duplicar(questionarioSuporte);
            String nome = "Questionário 2027 — Analista de Suporte";
            List<String> comps = new ArrayList<>(porCargo.get("Analista de Suporte"));
            DadosQuestionario base = dados(nome, cargos.get("Analista de Suporte"), comps, c, P::para, true);
            List<Long> ids = new ArrayList<>(base.competenciaIds());
            ids.add(c.get("Adaptabilidade"));
            questionarios.salvar(rascunho, new DadosQuestionario(nome, base.cargoId(), 5, ids, base.perguntas(),
                    base.niveis(), false));
        }
        log.info("Dados de exemplo: {} competências, {} questionários publicados, 3 modelos e 1 rascunho",
                c.size(), publicados);
    }

    private void cadastrar(Map<String, Long> ids, String nome, String descricao, String tipo) {
        ids.put(nome, catalogo.cadastrar(new DadosCompetencia(nome, descricao, tipo)));
    }

    /**
     * Monta o questionário com as perguntas do banco acima. {@code para} decide a quem cada pergunta se aplica;
     * {@code comAberta} inclui a pergunta aberta opcional "Comentários e sugestões" em Comunicação.
     */
    private static DadosQuestionario dados(String nome, Long cargoId, List<String> competencias, Map<String, Long> ids,
                                           Function<P, Set<TipoAvaliador>> para, boolean comAberta) {
        List<DadosPergunta> perguntas = new ArrayList<>();
        for (String comp : competencias) {
            Long cid = ids.get(comp);
            for (P p : PERGUNTAS.get(comp)) {
                perguntas.add(new DadosPergunta(null, cid, p.texto(), "ESCALA", true, para.apply(p)));
            }
            if (comAberta && comp.equals("Comunicação")) {
                perguntas.add(new DadosPergunta(null, cid, "Comentários e sugestões", "ABERTA", false,
                        para.apply(new P("", TODOS))));
            }
        }
        return new DadosQuestionario(nome, cargoId, 5, competencias.stream().map(ids::get).toList(), perguntas,
                ESCALA_1_A_5, false);
    }
}
