package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.dominio.Observacao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.AvaliacaoEnviadaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.ObservacaoRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RascunhoRespostaRepository;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
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
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao.MELHORIA;
import static br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao.POSITIVO;

/**
 * Dados de exemplo do pacote avaliacoes. Roda depois de identidade, privacidade, competências e ciclos e só com
 * {@code sgc.demo.carregar=true}. Cada parte é idempotente: só carrega quando a sua tabela está vazia.
 *
 * <ul>
 *   <li>UC-08: observações dos últimos cinco meses, com datas relativas a hoje. Bruno tem observações sobre toda a
 *       equipe, mas a do Fábio é antiga (mais de 60 dias), para o aviso da página inicial; Gustavo tem algumas sobre
 *       Igor e Júlia e nenhuma sobre a Kátia;</li>
 *   <li>UC-03: respostas dos ciclos de exemplo do pacote ciclos. Nas designações que o ciclos gravou como
 *       ENVIADA (todas dos ciclos 2025.2 e 2026.1 e parte do 2026.2) grava a avaliação enviada pelo mesmo caminho
 *       do envio ({@code RespostaService.gravarEnviadaDoExemplo}: UUID, verificador HMAC, sem designação nem
 *       avaliador); nas EM_PREENCHIMENTO grava um rascunho parcial. As NAO_INICIADA ficam para a demonstração ao
 *       vivo. Notas com perfil por pessoa (Carla forte em atendimento e fraca em resolução de problemas técnicos,
 *       Diego o inverso, Bruno bem avaliado em comunicação e pior em liderança), um pouco menores no 2025.2, algum
 *       "não tenho como avaliar", comentários na competência do assunto, sem frase repetida no resultado de um ciclo,
 *       e um comentário grosseiro sobre o Bruno no 2026.1, para a demonstração da moderação. {@link Random} com semente fixa: o exemplo é sempre o mesmo.</li>
 * </ul>
 */
@Component
@Order(40)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploAvaliacoes implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosExemploAvaliacoes.class);

    /** Comentário ofensivo do exemplo, para o RH ocultar na demonstração da moderação (UC-09). */
    static final String COMENTARIO_GROSSEIRO = "Ele é arrogante e ninguém aguenta trabalhar com ele.";

    private final ObservacaoRepository observacoes;
    private final UsuarioService usuarios;
    private final AvaliacaoEnviadaRepository avaliacoes;
    private final RascunhoRespostaRepository rascunhos;
    private final CicloService ciclos;
    private final QuestionarioService questionarios;
    private final RespostaService respostas;

    public DadosExemploAvaliacoes(ObservacaoRepository observacoes, UsuarioService usuarios,
                                  AvaliacaoEnviadaRepository avaliacoes, RascunhoRespostaRepository rascunhos,
                                  CicloService ciclos, QuestionarioService questionarios, RespostaService respostas) {
        this.observacoes = observacoes;
        this.usuarios = usuarios;
        this.avaliacoes = avaliacoes;
        this.rascunhos = rascunhos;
        this.ciclos = ciclos;
        this.questionarios = questionarios;
        this.respostas = respostas;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        carregarObservacoes();
        carregarRespostas();
    }

    private void carregarObservacoes() {
        if (observacoes.count() > 0) {
            return;
        }
        String bruno = "gestor@exemplo.com";
        observar(bruno, "colaborador@exemplo.com", POSITIVO, 12, "Resolveu sozinha a queda do sistema de pedidos da "
                + "filial de Anápolis em menos de uma hora e deixou o passo a passo registrado na base de conhecimento.");
        observar(bruno, "colaborador@exemplo.com", MELHORIA, 48, "Encerrou três chamados sem confirmar com o cliente se "
                + "o problema tinha sido resolvido; dois foram reabertos no dia seguinte. Combinamos de sempre "
                + "confirmar antes de fechar.");
        observar(bruno, "colaborador@exemplo.com", POSITIVO, 103, "Conduziu o treinamento da nova versão do sistema de "
                + "ponto para 25 pessoas do financeiro, com boa avaliação dos participantes.");

        observar(bruno, "diego@exemplo.com", MELHORIA, 6, "Deixou um chamado urgente da diretoria sem resposta até o "
                + "dia seguinte porque não conferiu a fila antes de sair.");
        observar(bruno, "diego@exemplo.com", POSITIVO, 37, "Criou uma lista de conferência para a troca de "
                + "computadores que acabou com as visitas repetidas ao mesmo setor.");
        observar(bruno, "diego@exemplo.com", POSITIVO, 120, "Acompanhou o colega recém-chegado nas primeiras semanas e "
                + "explicou com paciência as rotinas de atendimento.");

        observar(bruno, "elisa@exemplo.com", POSITIVO, 21, "Recebeu elogio por escrito da coordenação do almoxarifado "
                + "pela clareza ao explicar a configuração das novas impressoras.");
        observar(bruno, "elisa@exemplo.com", MELHORIA, 66, "Na reunião de alinhamento, interrompeu os colegas várias "
                + "vezes. Conversamos depois e ela se comprometeu a esperar a vez de falar.");
        observar(bruno, "elisa@exemplo.com", POSITIVO, 94, "Assumiu o plantão de um colega doente no fim de semana "
                + "do fechamento contábil, sem nenhum chamado em atraso.");
        observar(bruno, "elisa@exemplo.com", MELHORIA, 140, "Ficou uma semana sem registrar no sistema os atendimentos "
                + "feitos por telefone, o que atrapalhou o relatório mensal da equipe.");

        observar(bruno, "fabio@exemplo.com", POSITIVO, 74, "Fez o inventário dos notebooks da área comercial antes do "
                + "prazo e encontrou quatro equipamentos sem número de patrimônio.");
        observar(bruno, "fabio@exemplo.com", MELHORIA, 131, "Chegou atrasado a dois plantões de sábado sem avisar a "
                + "equipe.");

        String gustavo = "gustavo@exemplo.com";
        observar(gustavo, "igor@exemplo.com", POSITIVO, 15, "Achou a causa da lentidão no relatório de vendas e "
                + "corrigiu a consulta: o tempo caiu de 40 para 3 segundos.");
        observar(gustavo, "igor@exemplo.com", MELHORIA, 55, "Publicou uma alteração sem a revisão de código de um "
                + "colega, o que causou um erro na tela de cadastro de clientes.");
        observar(gustavo, "julia@exemplo.com", POSITIVO, 28, "Escreveu a documentação da integração com o banco de "
                + "forma tão clara que o suporte passou a resolver as dúvidas sem chamar o desenvolvimento.");
        log.info("Dados de exemplo (avaliacoes): {} observações de gestores", observacoes.count());
    }

    // ---------------------------------------------------------------- UC-03: respostas dos ciclos

    /** Nota média de cada pessoa por competência (escala 1 a 5); o que não está aqui fica em torno de 3,6. */
    private static final Map<String, Map<String, Double>> PERFIS = Map.of(
            "colaborador@exemplo.com", Map.of("Atendimento ao cliente", 4.6, "Resolução de problemas técnicos", 2.5,
                    "Comunicação", 4.2, "Trabalho em equipe", 4.0),
            "diego@exemplo.com", Map.of("Atendimento ao cliente", 2.7, "Resolução de problemas técnicos", 4.6,
                    "Conhecimento do produto", 4.2, "Comunicação", 3.0),
            "elisa@exemplo.com", Map.of("Comunicação", 4.4, "Trabalho em equipe", 4.2, "Atendimento ao cliente", 3.9),
            "fabio@exemplo.com", Map.of("Conhecimento do produto", 4.4, "Resolução de problemas técnicos", 3.2,
                    "Trabalho em equipe", 3.8),
            "gestor@exemplo.com", Map.of("Comunicação", 4.5, "Liderança", 2.8, "Atendimento ao cliente", 4.1,
                    "Orientação a resultados", 3.7),
            "gustavo@exemplo.com", Map.of("Qualidade do código", 4.5, "Liderança", 3.5, "Comunicação", 3.0,
                    "Orientação a resultados", 3.9),
            "igor@exemplo.com", Map.of("Qualidade do código", 3.0, "Resolução de problemas técnicos", 4.6,
                    "Comunicação", 3.3),
            "julia@exemplo.com", Map.of("Comunicação", 4.6, "Qualidade do código", 4.1, "Trabalho em equipe", 4.3));

    private static final String ATENDIMENTO = "Atendimento ao cliente";
    private static final String PRODUTO = "Conhecimento do produto";
    private static final String PROBLEMAS = "Resolução de problemas técnicos";
    private static final String EQUIPE = "Trabalho em equipe";
    private static final String COMUNICACAO = "Comunicação";
    private static final String LIDERANCA = "Liderança";
    private static final String RESULTADOS = "Orientação a resultados";
    private static final String CODIGO = "Qualidade do código";
    private static final String ADAPTABILIDADE = "Adaptabilidade";

    /** Comentários sobre cada pessoa, na competência do assunto de cada um. */
    private static final Map<String, Map<String, List<String>>> COMENTARIOS = Map.of(
            "colaborador@exemplo.com", Map.of(
                    ATENDIMENTO, List.of(
                            "Os clientes elogiam muito a paciência dela no atendimento.",
                            "É a pessoa que a equipe procura quando o cliente está nervoso.",
                            "Mesmo com a fila cheia, responde todo mundo com calma e educação."),
                    PROBLEMAS, List.of(
                            "Às vezes resolve o chamado sem entender a causa e o problema volta. Vale olhar os dados antes.",
                            "Quando o problema é mais técnico, demora a pedir ajuda e o chamado fica parado.",
                            "Poderia olhar os registros do sistema antes de reiniciar o computador do cliente."),
                    PRODUTO, List.of(
                            "Conhece bem os módulos de pedidos e faturamento e explica o passo a passo para o cliente.",
                            "Ainda tem dúvidas nas funções novas do módulo financeiro."),
                    EQUIPE, List.of(
                            "Divide com a equipe o que aprende, sem ninguém precisar pedir.",
                            "Cobre os colegas nas férias sem deixar chamado para trás."),
                    COMUNICACAO, List.of(
                            "Escreve respostas claras para os clientes, sem termos técnicos.",
                            "Fala pouco nas reuniões; as ideias dela são boas e poderiam aparecer mais.")),
            "diego@exemplo.com", Map.of(
                    ATENDIMENTO, List.of(
                            "No telefone pode ter mais paciência com quem não entende de tecnologia.",
                            "Às vezes responde o cliente de forma seca, mesmo resolvendo rápido.",
                            "Com os clientes antigos o atendimento é ótimo; com os novos, nem tanto."),
                    PROBLEMAS, List.of(
                            "Muito bom em achar a causa dos problemas; os relatórios dele ajudam a equipe toda.",
                            "Quando um problema parece sem solução, é ele quem acha a saída.",
                            "Organizado e cuidadoso com a documentação dos chamados."),
                    PRODUTO, List.of(
                            "Sabe responder dúvidas de configuração que nem o manual explica.",
                            "Acompanha as notas de versão e avisa a equipe sobre as mudanças."),
                    EQUIPE, List.of(
                            "Ajudou muito o colega novo nas primeiras semanas.",
                            "Às vezes prefere resolver tudo sozinho em vez de dividir as tarefas."),
                    COMUNICACAO, List.of(
                            "Explica bem a solução, mas usa termos que o cliente não entende.",
                            "Nas reuniões, poderia ouvir mais antes de responder.")),
            "elisa@exemplo.com", Map.of(
                    COMUNICACAO, List.of(
                            "Explica as coisas com muita clareza, até para quem não é da área.",
                            "Às vezes interrompe os colegas nas reuniões, mas tem melhorado.",
                            "Os e-mails dela para os clientes são curtos e resolvem a dúvida de primeira."),
                    EQUIPE, List.of(
                            "Ajuda a equipe a chegar a um acordo quando há divergência.",
                            "Assume o plantão de um colega sem reclamar quando alguém precisa."),
                    ATENDIMENTO, List.of(
                            "Atende com educação e presta atenção no que o cliente pede.",
                            "Poderia registrar os atendimentos no sistema com mais regularidade."),
                    PRODUTO, List.of(
                            "Conhece bem a configuração das impressoras e dos equipamentos da rede.",
                            "Precisa se atualizar nas mudanças da última versão do sistema."),
                    PROBLEMAS, List.of(
                            "Resolve bem os chamados do dia a dia; nos mais complexos, ainda pede ajuda.",
                            "Testa a solução com o cliente antes de fechar o chamado.")),
            "fabio@exemplo.com", Map.of(
                    PRODUTO, List.of(
                            "Conhece o produto como ninguém e orienta bem os clientes.",
                            "Sabe de cor onde fica cada configuração do sistema.",
                            "É quem a equipe consulta quando sai uma versão nova."),
                    PROBLEMAS, List.of(
                            "Resolve os chamados, mas às vezes demora para começar os mais difíceis.",
                            "Poderia registrar melhor a solução que aplicou em cada chamado."),
                    EQUIPE, List.of(
                            "Tranquilo e prestativo com os colegas.",
                            "Precisa avisar a equipe quando vai se atrasar para o plantão."),
                    ATENDIMENTO, List.of(
                            "Educado com os clientes, mas às vezes demora a dar retorno.",
                            "Os clientes gostam das explicações dele."),
                    COMUNICACAO, List.of(
                            "Fala pouco nas reuniões; poderia compartilhar mais o que sabe.",
                            "Explica bem por telefone, mas os registros escritos ficam incompletos.")),
            "gestor@exemplo.com", Map.of(
                    COMUNICACAO, List.of(
                            "Comunica as mudanças com antecedência e explica o porquê.",
                            "As reuniões de segunda são objetivas e todo mundo sai sabendo o que fazer.",
                            "Escreve muito bem; os comunicados dele não deixam dúvida."),
                    LIDERANCA, List.of(
                            "Gostaria de receber retorno sobre o meu trabalho com mais frequência, não só na avaliação.",
                            "Às vezes decide sozinho coisas que afetam a equipe toda; seria bom ouvir mais antes.",
                            "Poderia acompanhar mais de perto quem acabou de chegar.",
                            "Quase não conversa com cada pessoa sobre o desenvolvimento dela."),
                    ATENDIMENTO, List.of(
                            "Muito bom com clientes difíceis; a equipe aprende vendo ele atender.",
                            "Quando um cliente importante reclama, resolve pessoalmente e rápido."),
                    RESULTADOS, List.of(
                            "Acompanha as metas da equipe toda semana.",
                            "Às vezes muda as prioridades no meio da semana e algumas entregas atrasam.")),
            "gustavo@exemplo.com", Map.of(
                    CODIGO, List.of(
                            "Revisa o código com cuidado e as sugestões dele ensinam muito.",
                            "Exige testes em tudo, e isso evitou vários erros em produção."),
                    LIDERANCA, List.of(
                            "Protege a equipe das pressões de prazo e mantém o foco no que importa.",
                            "Dá liberdade para a equipe decidir, mas às vezes falta retorno sobre o que ficou bom.",
                            "Conversa com cada um sobre o que quer aprender."),
                    COMUNICACAO, List.of(
                            "As reuniões poderiam ser mais curtas e ter pauta.",
                            "Explica bem as decisões técnicas, mas demora a responder as mensagens."),
                    RESULTADOS, List.of(
                            "Planeja bem as entregas e raramente perde um prazo.",
                            "Às vezes aceita mais demandas do que a equipe consegue entregar.")),
            "igor@exemplo.com", Map.of(
                    CODIGO, List.of(
                            "Precisa respeitar a revisão de código antes de publicar.",
                            "O código funciona, mas poderia ter mais testes."),
                    PROBLEMAS, List.of(
                            "Resolve problemas difíceis de desempenho muito rápido.",
                            "Achou a causa da lentidão no relatório de vendas que ninguém tinha encontrado."),
                    EQUIPE, List.of("Sempre disposto a ajudar quando alguém trava."),
                    ADAPTABILIDADE, List.of("Aprendeu rápido a nova ferramenta de publicação."),
                    COMUNICACAO, List.of("Poderia avisar a equipe antes de mudar algo que afeta os outros.")),
            "julia@exemplo.com", Map.of(
                    COMUNICACAO, List.of(
                            "A documentação que ela escreve é referência para o time.",
                            "Conversa muito bem com as outras áreas."),
                    CODIGO, List.of(
                            "O código dela é fácil de entender e sempre vem com testes.",
                            "Tem potencial para assumir as partes mais críticas do sistema."),
                    EQUIPE, List.of("Revisa o código dos colegas sem deixar ninguém esperando."),
                    ADAPTABILIDADE, List.of("Se adaptou rápido quando o banco de dados mudou no meio do projeto."),
                    PROBLEMAS, List.of("Investiga com calma e encontra a causa antes de mexer no código.")));

    /** Comentário da autoavaliação, um por competência. */
    private static final Map<String, String> AUTOAVALIACAO = Map.of(
            ATENDIMENTO, "Quero ter mais calma no atendimento quando a fila está cheia.",
            PRODUTO, "Acho que evoluí neste período, mas preciso estudar mais o produto.",
            PROBLEMAS, "Gostaria de participar de mais treinamentos técnicos.",
            EQUIPE, "Posso ajudar mais os colegas nos dias de muito chamado.",
            COMUNICACAO, "Preciso registrar por escrito o que combino com as pessoas.",
            RESULTADOS, "Quero organizar melhor as prioridades da semana.",
            CODIGO, "Quero escrever mais testes antes de pedir a revisão.",
            ADAPTABILIDADE, "Demorei a me acostumar com as ferramentas novas, mas já estou mais à vontade.");

    private void carregarRespostas() {
        if (avaliacoes.count() > 0 || rascunhos.count() > 0) {
            return;
        }
        List<CicloResumo> comRespostas = Stream.concat(ciclos.encerrados().stream(), ciclos.abertos().stream())
                .sorted(Comparator.comparing(CicloResumo::dataAbertura)).toList();
        if (comRespostas.isEmpty()) {
            return;
        }
        Map<Long, PessoaResumo> pessoas = usuarios.listarAtivos().stream()
                .collect(Collectors.toMap(PessoaResumo::id, p -> p));
        Random principal = new Random(20261);
        Random anterior = new Random(20252);
        Random textos = new Random(2026);
        SorteioComentarios sorteio = new SorteioComentarios();
        int enviadas = 0;
        int parciais = 0;
        boolean grosseiroGravado = false;
        for (CicloResumo ciclo : comRespostas) {
            Random aleatorio = ciclo.nome().equals("Ciclo 2025.2") ? anterior : principal;
            double evolucao = switch (ciclo.nome()) {
                case "Ciclo 2025.2" -> -0.3;
                case "Ciclo 2026.1" -> 0.0;
                default -> 0.2;
            };
            for (DesignacaoResumo d : ciclos.designacoesDoCiclo(ciclo.id())) {
                PessoaResumo avaliado = pessoas.get(d.avaliadoId());
                if (d.questionarioId() == null || avaliado == null || !(d.enviada() || d.emPreenchimento())) {
                    continue;
                }
                QuestionarioCompleto q = questionarios.detalharPara(d.questionarioId(), d.relacao());
                boolean grosseiro = !grosseiroGravado && ciclo.nome().equals("Ciclo 2026.1")
                        && d.relacao() == TipoAvaliador.SUBORDINADO
                        && avaliado.email().equals("gestor@exemplo.com");
                Map<Long, RespostaInformada> geradas = gerar(q, d, avaliado, evolucao, d.enviada(), grosseiro,
                        aleatorio, sorteio.doResultado(ciclo.id(), avaliado.email()), textos);
                if (d.enviada()) {
                    respostas.gravarEnviadaDoExemplo(d, geradas);
                    grosseiroGravado |= grosseiro;
                    enviadas++;
                } else {
                    respostas.gravarRascunhoDoExemplo(d, geradas,
                            Calendario.agora().minusDays(1 + parciais % 4).minusHours(parciais % 6));
                    parciais++;
                }
            }
        }
        log.info("Dados de exemplo (avaliacoes): {} avaliações enviadas e {} rascunhos em preenchimento",
                enviadas, parciais);
    }

    /** Respostas de uma designação: completas (enviada) ou cerca de metade (rascunho). */
    private static Map<Long, RespostaInformada> gerar(QuestionarioCompleto q, DesignacaoResumo d, PessoaResumo avaliado,
                                                      double evolucao, boolean completa, boolean grosseiro,
                                                      Random aleatorio, Sorteio comentarios, Random textos) {
        Map<String, Double> perfil = PERFIS.getOrDefault(avaliado.email(), Map.of());
        double vies = switch (d.relacao()) {
            case AUTO -> 0.35;
            case CHEFE -> -0.1;
            default -> 0.0;
        };
        boolean podeNaoAvaliar = d.relacao() == TipoAvaliador.PAR || d.relacao() == TipoAvaliador.SUBORDINADO
                || d.relacao() == TipoAvaliador.EXTERNO;
        boolean auto = d.relacao() == TipoAvaliador.AUTO;
        String daAutoavaliacao = auto && textos.nextDouble() < 0.85
                ? comentarios.escolher(q.competencias().stream()
                        .filter(c -> c.perguntas().stream().anyMatch(p -> !p.escala()))
                        .map(c -> AUTOAVALIACAO.get(c.nome())).filter(Objects::nonNull).toList(), textos)
                : null;
        Map<String, List<String>> sobreAPessoa = COMENTARIOS.getOrDefault(avaliado.email(), Map.of());
        Map<Long, RespostaInformada> geradas = new LinkedHashMap<>();
        for (CompetenciaDoQuestionario c : q.competencias()) {
            double base = perfil.getOrDefault(c.nome(), 3.6) + vies + evolucao;
            for (PerguntaResumo p : c.perguntas()) {
                if (!p.escala()) {
                    String texto = textoAberto(c.nome(), auto, daAutoavaliacao, grosseiro, sobreAPessoa,
                            comentarios, completa, textos);
                    if (texto != null) {
                        geradas.put(p.id(), new RespostaInformada(null, false, texto));
                    }
                    continue;
                }
                if (!completa && aleatorio.nextDouble() < 0.5) {
                    continue;
                }
                if (podeNaoAvaliar && aleatorio.nextDouble() < 0.07) {
                    geradas.put(p.id(), new RespostaInformada(null, true, null));
                } else {
                    long nota = Math.round(base + aleatorio.nextGaussian() * 0.55);
                    int limitada = (int) Math.max(1, Math.min(q.escalaMax(), nota));
                    geradas.put(p.id(), new RespostaInformada(limitada, false, null));
                }
            }
        }
        if (geradas.isEmpty() && !q.perguntas().isEmpty()) {
            geradas.put(q.perguntas().get(0).id(), new RespostaInformada(3, false, null));
        }
        return geradas;
    }

    /**
     * Texto da pergunta aberta da competência, ou {@code null} se o avaliador deixou em branco. A autoavaliação
     * comenta uma competência só; o comentário grosseiro vai em Liderança.
     */
    private static String textoAberto(String competencia, boolean auto, String daAutoavaliacao, boolean grosseiro,
                                      Map<String, List<String>> sobreAPessoa, Sorteio comentarios, boolean completa,
                                      Random textos) {
        if (auto) {
            return daAutoavaliacao != null && daAutoavaliacao.equals(AUTOAVALIACAO.get(competencia))
                    ? daAutoavaliacao : null;
        }
        if (grosseiro && competencia.equals(LIDERANCA)) {
            return COMENTARIO_GROSSEIRO;
        }
        if (textos.nextDouble() >= (completa ? 0.35 : 0.2)) {
            return null;
        }
        return comentarios.escolher(sobreAPessoa.getOrDefault(competencia, List.of()), textos);
    }

    /**
     * Sorteio dos comentários sem repetir frase no resultado de uma pessoa num ciclo. Entre as frases livres, prefere
     * as que a pessoa ainda não recebeu em outro ciclo.
     */
    private static final class SorteioComentarios {

        private final Map<String, Set<String>> recebidasPorPessoa = new HashMap<>();
        private final Map<String, Sorteio> porResultado = new HashMap<>();

        Sorteio doResultado(Long cicloId, String avaliado) {
            Set<String> recebidas = recebidasPorPessoa.computeIfAbsent(avaliado, k -> new HashSet<>());
            return porResultado.computeIfAbsent(cicloId + "/" + avaliado, k -> new Sorteio(recebidas));
        }
    }

    private static final class Sorteio {

        private final Set<String> noResultado = new HashSet<>();
        private final Set<String> recebidasAntes;

        Sorteio(Set<String> recebidasAntes) {
            this.recebidasAntes = recebidasAntes;
        }

        String escolher(List<String> opcoes, Random textos) {
            List<String> livres = opcoes.stream().filter(o -> !noResultado.contains(o)).toList();
            if (livres.isEmpty()) {
                return null;
            }
            List<String> novas = livres.stream().filter(o -> !recebidasAntes.contains(o)).toList();
            List<String> candidatas = novas.isEmpty() ? livres : novas;
            String texto = candidatas.get(textos.nextInt(candidatas.size()));
            noResultado.add(texto);
            recebidasAntes.add(texto);
            return texto;
        }
    }

    private void observar(String gestorEmail, String colaboradorEmail, TipoObservacao tipo, int diasAtras,
                          String descricao) {
        Long gestorId = usuarios.buscarPorEmail(gestorEmail).id();
        Long colaboradorId = usuarios.buscarPorEmail(colaboradorEmail).id();
        LocalDate dataFato = Calendario.hoje().minusDays(diasAtras);
        // Registrada no mesmo dia ou até dois dias depois do fato, em horário de expediente, nunca no futuro.
        OffsetDateTime criadaEm = dataFato.plusDays(diasAtras % 3)
                .atTime(LocalTime.of(9 + diasAtras % 8, (diasAtras * 7) % 60))
                .atZone(Calendario.ZONA).toOffsetDateTime();
        if (criadaEm.isAfter(Calendario.agora())) {
            criadaEm = Calendario.agora().minusMinutes(30);
        }
        observacoes.save(new Observacao(gestorId, colaboradorId, tipo, descricao, dataFato, criadaEm));
    }
}
