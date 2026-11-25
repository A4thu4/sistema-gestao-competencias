package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.dominio.AvaliacaoEnviada;
import br.edu.pucgoias.sgc.avaliacoes.dominio.RascunhoResposta;
import br.edu.pucgoias.sgc.avaliacoes.dominio.Resposta;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.AvaliacaoEnviadaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RascunhoRespostaRepository;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.RespostaRepository;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaDoQuestionario;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UC-09 Reabrir avaliação e moderar comentários (RF-16, RF-22, RNF-01, RNF-05). Só o RH ({@code /moderacao/**}).
 *
 * <p><b>Reabrir:</b> a avaliação enviada é achada pelo verificador (HMAC da designação, DA-06), as respostas voltam
 * para o rascunho do avaliador, a avaliação enviada é apagada e a designação volta a "em preenchimento". Fica no log
 * de auditoria (usuário, data, hora e motivo) e o avaliador recebe um e-mail. Ciclo encerrado não reabre.
 *
 * <p><b>Moderar:</b> o RH vê os comentários das perguntas abertas sem autor e sem relação e oculta (com motivo) ou
 * volta a exibir; comentário oculto some dos resultados ({@link ConsultaRespostasService#comentariosVisiveis}).
 * Em ciclo aberto, os comentários sobre quem tem menos de {@value #MINIMO_AVALIACOES} avaliações enviadas não
 * aparecem: a lista de enviadas da reabertura deixaria deduzir o autor.
 *
 * <p>O verificador e o id das avaliações enviadas nunca saem deste serviço: as telas usam o id da designação
 * (reabrir) e o id da resposta (comentário).
 */
@Service
@Transactional(readOnly = true)
public class ModeracaoService {

    static final int TAMANHO_MAXIMO_MOTIVO = 300;
    static final int MINIMO_AVALIACOES = 3;

    private final AvaliacaoEnviadaRepository avaliacoes;
    private final RespostaRepository respostas;
    private final RascunhoRespostaRepository rascunhos;
    private final CicloService ciclos;
    private final QuestionarioService questionarios;
    private final UsuarioService usuarios;
    private final EmailService email;
    private final ApplicationEventPublisher eventos;
    private final Verificador verificador;
    private final String urlBase;

    public ModeracaoService(AvaliacaoEnviadaRepository avaliacoes, RespostaRepository respostas,
                            RascunhoRespostaRepository rascunhos, CicloService ciclos,
                            QuestionarioService questionarios, UsuarioService usuarios, EmailService email,
                            ApplicationEventPublisher eventos, Verificador verificador,
                            @Value("${sgc.url-base:http://localhost:8080}") String urlBase) {
        this.avaliacoes = avaliacoes;
        this.respostas = respostas;
        this.rascunhos = rascunhos;
        this.ciclos = ciclos;
        this.questionarios = questionarios;
        this.usuarios = usuarios;
        this.email = email;
        this.eventos = eventos;
        this.verificador = verificador;
        this.urlBase = urlBase;
    }

    // ---------------------------------------------------------------- ciclos

    /** Ciclos em que dá para reabrir avaliações: os abertos. */
    public List<CicloResumo> ciclosParaReabrir() {
        return ciclos.abertos();
    }

    /** Ciclos com comentários para moderar: abertos e encerrados. */
    public List<CicloResumo> ciclosParaModerar() {
        return Stream.concat(ciclos.abertos().stream(), ciclos.encerrados().stream()).toList();
    }

    public CicloResumo ciclo(Long cicloId) {
        return ciclos.ciclo(cicloId);
    }

    // ---------------------------------------------------------------- reabrir

    /** Designações enviadas do ciclo, com avaliador e avaliado (sem respostas). */
    public List<EnviadaParaReabrir> enviadas(Long cicloId) {
        List<DesignacaoResumo> lista = ciclos.designacoesDoCiclo(cicloId).stream()
                .filter(DesignacaoResumo::enviada).toList();
        Map<Long, PessoaResumo> pessoas = pessoasDas(lista);
        return lista.stream().map(d -> para(d, pessoas))
                .sorted(Comparator.comparing(EnviadaParaReabrir::avaliadorNome)
                        .thenComparing(EnviadaParaReabrir::avaliadoNome))
                .toList();
    }

    public EnviadaParaReabrir paraReabrir(Long designacaoId) {
        DesignacaoResumo d = ciclos.designacao(designacaoId);
        return para(d, pessoasDas(List.of(d)));
    }

    /** UC-09: reabre a avaliação enviada da designação, com motivo obrigatório. */
    @Transactional
    public void reabrir(Long designacaoId, String motivo) {
        String m = motivoObrigatorio(motivo, "Informe o motivo da reabertura.");
        DesignacaoResumo d = ciclos.designacao(designacaoId);
        if (!d.enviada()) {
            throw new RegraDeNegocioException("Esta avaliação não está enviada; não há o que reabrir.");
        }
        if (!ciclos.aceitaRespostas(d.cicloId())) {
            throw new RegraDeNegocioException("O ciclo “" + d.cicloNome() + "” está encerrado: as avaliações dele "
                    + "não podem mais ser reabertas.");
        }
        AvaliacaoEnviada a = avaliacoes.findByVerificador(verificador.daDesignacao(d.id()))
                .orElseThrow(() -> new RegraDeNegocioException("Não encontramos as respostas desta avaliação."));
        OffsetDateTime agora = Calendario.agora();
        List<Resposta> lista = respostas.findByAvaliacaoEnviadaId(a.getId());
        List<RascunhoResposta> devolvidas = new ArrayList<>();
        for (Resposta r : lista) {
            RascunhoResposta rascunho = new RascunhoResposta(d.id(), r.getPerguntaId());
            rascunho.preencher(r.getNota(), r.isNaoAvalia(), r.getTexto(), agora);
            devolvidas.add(rascunho);
        }
        respostas.deleteAll(lista);
        avaliacoes.delete(a);
        rascunhos.saveAll(devolvidas);
        ciclos.reabrir(d.id());

        EnviadaParaReabrir e = para(d, pessoasDas(List.of(d)));
        eventos.publishEvent(EventoAuditoria.de("AVALIACAO_REABERTA", "Avaliação de " + e.avaliadorNome() + " sobre "
                + e.avaliadoNome() + " (" + e.relacaoRotulo().toLowerCase() + ") no ciclo “" + d.cicloNome() + "”", m));
        avisarAvaliador(d, e, m);
    }

    private void avisarAvaliador(DesignacaoResumo d, EnviadaParaReabrir e, String motivo) {
        String para = d.externa() ? d.externoEmail() : usuarios.buscar(d.avaliadorId()).email();
        String link = d.externa() ? "Use o mesmo link individual que você recebeu no convite."
                : "Acesse: " + urlBase + "/avaliacoes/" + d.id();
        String corpo = "Olá, " + e.avaliadorNome() + ".\n\n"
                + "O RH reabriu a sua avaliação de " + e.avaliadoNome() + " no ciclo “" + d.cicloNome() + "”, "
                + "como você pediu.\nMotivo: " + motivo + "\n\n"
                + "As suas respostas continuam lá. Corrija o que for preciso e envie de novo até "
                + Calendario.data(d.dataEncerramento()) + ".\n" + link + "\n";
        email.enviar(para, "Sua avaliação foi reaberta — " + d.cicloNome(), corpo, "REABERTURA");
    }

    // ---------------------------------------------------------------- moderar comentários

    /** Comentários do ciclo, sem autor e sem relação, por pessoa avaliada, competência e pergunta. */
    public List<ComentarioParaModeracao> comentarios(Long cicloId) {
        Set<Long> aguardando = aguardando(cicloId);
        List<AvaliacaoEnviada> enviadas = avaliacoes.findByCicloId(cicloId).stream()
                .filter(a -> !aguardando.contains(a.getAvaliadoId())).toList();
        Map<UUID, AvaliacaoEnviada> porId = enviadas.stream()
                .collect(Collectors.toMap(AvaliacaoEnviada::getId, Function.identity()));
        Map<Long, Local> locais = locaisDas(enviadas);
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(
                enviadas.stream().map(AvaliacaoEnviada::getAvaliadoId).collect(Collectors.toSet()));
        record Item(ComentarioParaModeracao c, int ordem) {
        }
        return respostas.findByAvaliacaoEnviadaIdIn(porId.keySet()).stream()
                .filter(r -> r.temComentario() && locais.containsKey(r.getPerguntaId()))
                .map(r -> {
                    Local l = locais.get(r.getPerguntaId());
                    PessoaResumo p = pessoas.get(porId.get(r.getAvaliacaoEnviadaId()).getAvaliadoId());
                    return new Item(new ComentarioParaModeracao(r.getId(), p == null ? "—" : p.nome(),
                            l.competencia(), l.pergunta(), r.getTexto().trim(), r.isComentarioOculto(),
                            r.getMotivoOcultacao()), l.ordem());
                })
                .sorted(Comparator.comparing((Item i) -> i.c().avaliadoNome()).thenComparing(Item::ordem)
                        .thenComparing(i -> i.c().texto()))
                .map(Item::c)
                .toList();
    }

    /** Quem tem comentário esperando: ciclo aberto e menos de {@value #MINIMO_AVALIACOES} avaliações enviadas. */
    public List<String> aguardandoAvaliacoes(Long cicloId) {
        Set<Long> aguardando = aguardando(cicloId);
        if (aguardando.isEmpty()) {
            return List.of();
        }
        Map<UUID, Long> avaliadoDa = avaliacoes.findByCicloId(cicloId).stream()
                .filter(a -> aguardando.contains(a.getAvaliadoId()))
                .collect(Collectors.toMap(AvaliacaoEnviada::getId, AvaliacaoEnviada::getAvaliadoId));
        Set<Long> comComentario = respostas.findByAvaliacaoEnviadaIdIn(avaliadoDa.keySet()).stream()
                .filter(Resposta::temComentario).map(r -> avaliadoDa.get(r.getAvaliacaoEnviadaId()))
                .collect(Collectors.toSet());
        return usuarios.buscarPorIds(comComentario).values().stream().map(PessoaResumo::nome).sorted().toList();
    }

    private Set<Long> aguardando(Long cicloId) {
        if (!ciclos.aceitaRespostas(cicloId)) {
            ciclos.ciclo(cicloId);
            return Set.of();
        }
        return avaliacoes.findByCicloId(cicloId).stream()
                .collect(Collectors.groupingBy(AvaliacaoEnviada::getAvaliadoId, Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() < MINIMO_AVALIACOES).map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /** Oculta o comentário nos resultados (RF-22), com motivo obrigatório. O texto continua guardado. */
    @Transactional
    public void ocultar(Long respostaId, String motivo) {
        String m = motivoObrigatorio(motivo, "Informe o motivo para ocultar o comentário.");
        Resposta r = comentario(respostaId);
        r.ocultar(m);
        eventos.publishEvent(EventoAuditoria.de("COMENTARIO_OCULTADO", descricao(r), m));
    }

    @Transactional
    public void exibir(Long respostaId) {
        Resposta r = comentario(respostaId);
        r.exibir();
        eventos.publishEvent(EventoAuditoria.de("COMENTARIO_EXIBIDO", descricao(r), null));
    }

    // ---------------------------------------------------------------- apoio

    private record Local(int ordem, String competencia, String pergunta) {
    }

    private Map<Long, Local> locaisDas(List<AvaliacaoEnviada> enviadas) {
        Map<Long, Local> locais = new HashMap<>();
        enviadas.stream().map(AvaliacaoEnviada::getQuestionarioId).distinct().forEach(q -> {
            int ordem = 0;
            for (CompetenciaDoQuestionario c : questionarios.detalhar(q).competencias()) {
                for (PerguntaResumo p : c.perguntas()) {
                    locais.putIfAbsent(p.id(), new Local(ordem++, c.nome(), p.texto()));
                }
            }
        });
        return locais;
    }

    private Resposta comentario(Long respostaId) {
        Resposta r = respostas.findById(respostaId)
                .orElseThrow(() -> new NaoEncontradoException("Comentário não encontrado."));
        if (!r.temComentario()) {
            throw new NaoEncontradoException("Comentário não encontrado.");
        }
        AvaliacaoEnviada a = avaliacoes.findById(r.getAvaliacaoEnviadaId())
                .orElseThrow(() -> new NaoEncontradoException("Comentário não encontrado."));
        if (aguardando(a.getCicloId()).contains(a.getAvaliadoId())) {
            throw new NaoEncontradoException("Comentário não encontrado.");
        }
        return r;
    }

    private String descricao(Resposta r) {
        AvaliacaoEnviada a = avaliacoes.findById(r.getAvaliacaoEnviadaId())
                .orElseThrow(() -> new NaoEncontradoException("Comentário não encontrado."));
        Local l = locaisDas(List.of(a)).get(r.getPerguntaId());
        return "Comentário nº " + r.getId() + " sobre " + usuarios.buscar(a.getAvaliadoId()).nome()
                + (l == null ? "" : " em “" + l.pergunta() + "” (" + l.competencia() + ")")
                + " no ciclo “" + ciclos.ciclo(a.getCicloId()).nome() + "”";
    }

    private Map<Long, PessoaResumo> pessoasDas(List<DesignacaoResumo> lista) {
        return usuarios.buscarPorIds(lista.stream()
                .flatMap(d -> Stream.of(d.avaliadoId(), d.avaliadorId()))
                .filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    private static EnviadaParaReabrir para(DesignacaoResumo d, Map<Long, PessoaResumo> pessoas) {
        String avaliador = d.externa() ? d.externoNome() + " (externo)" : nome(pessoas.get(d.avaliadorId()));
        return new EnviadaParaReabrir(d, avaliador, nome(pessoas.get(d.avaliadoId())));
    }

    private static String nome(PessoaResumo p) {
        return p == null ? "—" : p.nome();
    }

    private static String motivoObrigatorio(String motivo, String mensagem) {
        String m = motivo == null ? "" : motivo.strip();
        if (m.isEmpty()) {
            throw new RegraDeNegocioException(mensagem);
        }
        if (m.length() > TAMANHO_MAXIMO_MOTIVO) {
            throw new RegraDeNegocioException("O motivo pode ter no máximo " + TAMANHO_MAXIMO_MOTIVO + " caracteres.");
        }
        return m;
    }
}
