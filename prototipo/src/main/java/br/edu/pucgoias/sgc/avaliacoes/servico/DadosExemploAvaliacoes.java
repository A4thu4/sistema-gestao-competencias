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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
 *       ENVIADA (todas do Ciclo 2026.1 e parte do 2026.2) grava a avaliação enviada pelo mesmo caminho do envio
 *       ({@code RespostaService.gravarEnviadaDoExemplo}: UUID, verificador HMAC, sem designação nem avaliador); nas
 *       EM_PREENCHIMENTO grava um rascunho parcial. As NAO_INICIADA ficam para a demonstração ao vivo. Notas com
 *       perfil por pessoa (Carla forte em atendimento e fraca em resolução de problemas técnicos, Diego o inverso,
 *       Bruno bem avaliado em comunicação e pior em liderança), algum "não tenho como avaliar", comentários
 *       plausíveis e um comentário grosseiro sobre o Bruno no 2026.1, para a demonstração da moderação.
 *       {@link Random} com semente fixa: o exemplo é sempre o mesmo.</li>
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

    /** Comentários plausíveis sobre cada pessoa (perguntas abertas). */
    private static final Map<String, List<String>> COMENTARIOS = Map.of(
            "colaborador@exemplo.com", List.of(
                    "Os clientes elogiam muito a paciência dela no atendimento.",
                    "Às vezes resolve o chamado sem entender a causa e o problema volta. Vale olhar os dados antes.",
                    "É a pessoa que a equipe procura quando o cliente está nervoso."),
            "diego@exemplo.com", List.of(
                    "Muito bom em achar a causa dos problemas; os relatórios dele ajudam a equipe toda.",
                    "No telefone pode ter mais paciência com quem não entende de tecnologia.",
                    "Organizado e cuidadoso com a documentação dos chamados."),
            "elisa@exemplo.com", List.of(
                    "Explica as coisas com muita clareza, até para quem não é da área.",
                    "Ajuda a equipe a chegar a um acordo quando há divergência.",
                    "Poderia registrar os atendimentos no sistema com mais regularidade."),
            "fabio@exemplo.com", List.of(
                    "Conhece o produto como ninguém e orienta bem os clientes.",
                    "Precisa cumprir os prazos combinados e avisar quando vai atrasar.",
                    "Tranquilo e prestativo com os colegas."),
            "gestor@exemplo.com", List.of(
                    "Comunica as mudanças com antecedência e explica o porquê.",
                    "Gostaria de receber retorno sobre o meu trabalho com mais frequência, não só na avaliação.",
                    "Às vezes decide sozinho coisas que afetam a equipe toda; seria bom ouvir mais antes.",
                    "Muito bom com clientes difíceis; a equipe aprende vendo ele atender."),
            "gustavo@exemplo.com", List.of(
                    "Revisa o código com cuidado e as sugestões dele ensinam muito.",
                    "As reuniões poderiam ser mais curtas e ter pauta.",
                    "Protege a equipe das pressões de prazo e mantém o foco no que importa."),
            "igor@exemplo.com", List.of(
                    "Resolve problemas difíceis de desempenho muito rápido.",
                    "Precisa respeitar a revisão de código antes de publicar.",
                    "Sempre disposto a ajudar quando alguém trava."),
            "julia@exemplo.com", List.of(
                    "A documentação que ela escreve é referência para o time.",
                    "Conversa muito bem com as outras áreas.",
                    "Tem potencial para assumir tarefas maiores."));

    private static final List<String> AUTOAVALIACAO = List.of(
            "Quero organizar melhor as prioridades da semana.",
            "Acho que evoluí neste período, mas preciso estudar mais o produto.",
            "Gostaria de participar de mais treinamentos técnicos.");

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
        Random aleatorio = new Random(20261);
        int enviadas = 0;
        int parciais = 0;
        boolean grosseiroGravado = false;
        for (CicloResumo ciclo : comRespostas) {
            double evolucao = ciclo.encerrado() ? 0.0 : 0.2;
            for (DesignacaoResumo d : ciclos.designacoesDoCiclo(ciclo.id())) {
                PessoaResumo avaliado = pessoas.get(d.avaliadoId());
                if (d.questionarioId() == null || avaliado == null || !(d.enviada() || d.emPreenchimento())) {
                    continue;
                }
                QuestionarioCompleto q = questionarios.detalharPara(d.questionarioId(), d.relacao());
                boolean grosseiro = !grosseiroGravado && ciclo.encerrado() && d.relacao() == TipoAvaliador.SUBORDINADO
                        && avaliado.email().equals("gestor@exemplo.com");
                Map<Long, RespostaInformada> geradas = gerar(q, d, avaliado, evolucao, d.enviada(), grosseiro, aleatorio);
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
                                                      Random aleatorio) {
        Map<String, Double> perfil = PERFIS.getOrDefault(avaliado.email(), Map.of());
        double vies = switch (d.relacao()) {
            case AUTO -> 0.35;
            case CHEFE -> -0.1;
            default -> 0.0;
        };
        boolean podeNaoAvaliar = d.relacao() == TipoAvaliador.PAR || d.relacao() == TipoAvaliador.SUBORDINADO
                || d.relacao() == TipoAvaliador.EXTERNO;
        Map<Long, RespostaInformada> geradas = new LinkedHashMap<>();
        for (CompetenciaDoQuestionario c : q.competencias()) {
            double base = perfil.getOrDefault(c.nome(), 3.6) + vies + evolucao;
            for (PerguntaResumo p : c.perguntas()) {
                if (!completa && aleatorio.nextDouble() < 0.5) {
                    continue;
                }
                if (p.escala()) {
                    if (podeNaoAvaliar && aleatorio.nextDouble() < 0.07) {
                        geradas.put(p.id(), new RespostaInformada(null, true, null));
                    } else {
                        long nota = Math.round(base + aleatorio.nextGaussian() * 0.55);
                        int limitada = (int) Math.max(1, Math.min(q.escalaMax(), nota));
                        geradas.put(p.id(), new RespostaInformada(limitada, false, null));
                    }
                } else if (grosseiro) {
                    geradas.put(p.id(), new RespostaInformada(null, false, COMENTARIO_GROSSEIRO));
                } else if (aleatorio.nextDouble() < 0.6) {
                    List<String> opcoes = d.relacao() == TipoAvaliador.AUTO ? AUTOAVALIACAO
                            : COMENTARIOS.getOrDefault(avaliado.email(), AUTOAVALIACAO);
                    geradas.put(p.id(), new RespostaInformada(null, false, opcoes.get(aleatorio.nextInt(opcoes.size()))));
                }
            }
        }
        if (geradas.isEmpty() && !q.perguntas().isEmpty()) {
            geradas.put(q.perguntas().get(0).id(), new RespostaInformada(3, false, null));
        }
        return geradas;
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
