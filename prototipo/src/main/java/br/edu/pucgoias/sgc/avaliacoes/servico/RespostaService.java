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
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enquanto o avaliador preenche, cada resposta fica em {@code rascunho_resposta}, ligada à designação, para o
 * salvamento automático. No envio ({@link #enviarAvaliacao}) as respostas são copiadas para uma
 * {@code avaliacao_enviada} que não guarda designação, avaliador nem data (id UUID aleatório) e o rascunho é apagado,
 * tudo na mesma transação: depois disso nenhum perfil consegue ligar a resposta a quem respondeu.
 *
 * <p>Acesso: o avaliador interno só abre as designações dele (senão 403); o externo entra pelo token do link
 * individual, sem login.
 */
@Service
@Transactional(readOnly = true)
public class RespostaService {

    static final int TAMANHO_MAXIMO_TEXTO = 2000;

    private final RascunhoRespostaRepository rascunhos;
    private final AvaliacaoEnviadaRepository avaliacoes;
    private final RespostaRepository respostas;
    private final CicloService ciclos;
    private final QuestionarioService questionarios;
    private final UsuarioService usuarios;
    private final ObservacaoService observacoes;
    private final ObjectProvider<ResultadoAnterior> resultadoAnterior;
    private final Verificador verificador;

    public RespostaService(RascunhoRespostaRepository rascunhos, AvaliacaoEnviadaRepository avaliacoes,
                           RespostaRepository respostas, CicloService ciclos, QuestionarioService questionarios,
                           UsuarioService usuarios, ObservacaoService observacoes,
                           ObjectProvider<ResultadoAnterior> resultadoAnterior, Verificador verificador) {
        this.rascunhos = rascunhos;
        this.avaliacoes = avaliacoes;
        this.respostas = respostas;
        this.ciclos = ciclos;
        this.questionarios = questionarios;
        this.usuarios = usuarios;
        this.observacoes = observacoes;
        this.resultadoAnterior = resultadoAnterior;
        this.verificador = verificador;
    }

    // ---------------------------------------------------------------- passo 2: lista do avaliador

    /** Avaliações sob responsabilidade do usuário, agrupadas por ciclo (abertos primeiro, depois os mais recentes). */
    public List<GrupoDeCiclo> minhas(UsuarioAutenticado eu) {
        List<DesignacaoResumo> lista = ciclos.designacoesDoAvaliador(eu.id());
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(
                lista.stream().map(DesignacaoResumo::avaliadoId).collect(Collectors.toSet()));
        Map<Long, List<DesignacaoResumo>> porCiclo = lista.stream()
                .collect(Collectors.groupingBy(DesignacaoResumo::cicloId, LinkedHashMap::new, Collectors.toList()));
        List<GrupoDeCiclo> grupos = new ArrayList<>();
        porCiclo.forEach((cicloId, ds) -> {
            DesignacaoResumo primeira = ds.get(0);
            List<AvaliacaoNaLista> itens = ds.stream()
                    .sorted(Comparator.comparing(DesignacaoResumo::enviada)
                            .thenComparing(d -> d.relacao() != TipoAvaliador.AUTO)
                            .thenComparing(d -> pessoas.get(d.avaliadoId()).nome()))
                    .map(d -> new AvaliacaoNaLista(d, pessoas.get(d.avaliadoId()))).toList();
            grupos.add(new GrupoDeCiclo(cicloId, primeira.cicloNome(), primeira.dataEncerramento(),
                    ciclos.aceitaRespostas(cicloId), itens));
        });
        grupos.sort(Comparator.comparing(GrupoDeCiclo::aceitaRespostas).reversed()
                .thenComparing(GrupoDeCiclo::encerramento, Comparator.reverseOrder()));
        return grupos;
    }

    /** Avaliações ainda não enviadas em ciclos que aceitam respostas (cartão da página inicial). */
    public long pendentes(UsuarioAutenticado eu) {
        return ciclos.designacoesDoAvaliador(eu.id()).stream()
                .filter(d -> !d.enviada() && d.cicloAberto())
                .filter(d -> ciclos.aceitaRespostas(d.cicloId()))
                .count();
    }

    // ---------------------------------------------------------------- passo 4: abrir o formulário

    public FormularioAvaliacao abrir(UsuarioAutenticado eu, Long designacaoId) {
        return formulario(doAvaliador(eu, designacaoId));
    }

    /** Link inválido, avaliação já enviada ou ciclo encerrado: exceção. */
    public FormularioAvaliacao abrirExterno(String token) {
        return formulario(externaDisponivel(token));
    }

    // ---------------------------------------------------------------- passos 5 e 6: salvar (RF-14)

    /** Salvamento automático de uma resposta. Devolve a hora do salvamento. */
    @Transactional
    public OffsetDateTime salvar(UsuarioAutenticado eu, Long designacaoId, Long perguntaId, RespostaInformada r) {
        DesignacaoResumo d = doAvaliador(eu, designacaoId);
        return salvarUma(d, perguntaId, r);
    }

    @Transactional
    public OffsetDateTime salvarExterno(String token, Long perguntaId, RespostaInformada r) {
        return salvarUma(externaDisponivel(token), perguntaId, r);
    }

    /** "Salvar rascunho" sem JavaScript: grava o formulário inteiro (o que veio em branco é apagado). */
    @Transactional
    public void salvarTodas(UsuarioAutenticado eu, Long designacaoId, Map<Long, RespostaInformada> todas) {
        salvarFormulario(doAvaliador(eu, designacaoId), todas);
    }

    @Transactional
    public void salvarTodasExterno(String token, Map<Long, RespostaInformada> todas) {
        salvarFormulario(externaDisponivel(token), todas);
    }

    // ---------------------------------------------------------------- passos 7 a 10: enviar (CO-02)

    /**
     * Confere que a designação é do usuário, que o ciclo aceita respostas e que não há obrigatória pendente; cria a
     * avaliação enviada anônima, copia as respostas, apaga o rascunho e marca a designação como enviada no pacote
     * ciclos. Tudo numa transação.
     */
    @Transactional
    public void enviarAvaliacao(UsuarioAutenticado eu, Long designacaoId) {
        enviar(doAvaliador(eu, designacaoId));
    }

    @Transactional
    public void enviarExterno(String token) {
        enviar(externaDisponivel(token));
    }

    // ---------------------------------------------------------------- caminho interno dos dados de exemplo

    /**
     * Grava uma avaliação enviada do exemplo com as mesmas linhas de um envio normal, sem as conferências de prazo e
     * de situação (o ciclo de exemplo pode estar encerrado e a designação já vem marcada pelo pacote ciclos).
     */
    void gravarEnviadaDoExemplo(DesignacaoResumo d, Map<Long, RespostaInformada> todas) {
        registrarEnvio(d, questionarioPara(d), todas);
    }

    /** Rascunho parcial do exemplo, sem mudar a situação da designação (o ciclos já gravou EM_PREENCHIMENTO). */
    void gravarRascunhoDoExemplo(DesignacaoResumo d, Map<Long, RespostaInformada> todas, OffsetDateTime quando) {
        QuestionarioCompleto q = questionarioPara(d);
        todas.forEach((perguntaId, r) -> {
            PerguntaResumo p = pergunta(q, perguntaId);
            gravarRascunho(d, p, validar(q, p, r), quando);
        });
    }

    // ---------------------------------------------------------------- regras

    private DesignacaoResumo doAvaliador(UsuarioAutenticado eu, Long designacaoId) {
        DesignacaoResumo d = ciclos.designacao(designacaoId);
        if (d.externa() || !Objects.equals(d.avaliadorId(), eu.id())) {
            throw new AccessDeniedException("A avaliação não é deste avaliador.");
        }
        if (!d.cicloAberto() && !d.cicloEncerrado()) {
            throw new RegraDeNegocioException("Este ciclo de avaliação ainda não começou.");
        }
        return d;
    }

    private DesignacaoResumo externaDisponivel(String token) {
        DesignacaoResumo d = ciclos.designacaoPorToken(token).filter(DesignacaoResumo::externa)
                .orElseThrow(() -> new LinkExternoIndisponivelException("Link inválido",
                        "Este link de avaliação não é válido. Confira se copiou o endereço inteiro do e-mail que "
                                + "você recebeu.", false));
        if (d.enviada()) {
            throw new LinkExternoIndisponivelException("Avaliação já enviada",
                    "Esta avaliação já foi enviada. Obrigado.", true);
        }
        if (!ciclos.aceitaRespostas(d.cicloId())) {
            throw new LinkExternoIndisponivelException("Avaliação encerrada",
                    "O prazo desta avaliação terminou em " + Calendario.data(d.dataEncerramento())
                            + " e ela não aceita mais respostas.", false);
        }
        return d;
    }

    private void exigirEditavel(DesignacaoResumo d) {
        String motivo = motivoBloqueio(d);
        if (motivo != null) {
            throw new RegraDeNegocioException(motivo);
        }
    }

    private String motivoBloqueio(DesignacaoResumo d) {
        if (!ciclos.aceitaRespostas(d.cicloId())) {
            return "O ciclo “" + d.cicloNome() + "” foi encerrado em " + Calendario.data(d.dataEncerramento())
                    + " e não aceita mais respostas.";
        }
        if (d.enviada()) {
            return "Esta avaliação já foi enviada e não pode mais ser alterada. Se precisar corrigir algo, peça ao RH "
                    + "a reabertura.";
        }
        return null;
    }

    private QuestionarioCompleto questionarioPara(DesignacaoResumo d) {
        if (d.questionarioId() == null) {
            throw new RegraDeNegocioException("Esta avaliação está sem questionário. Avise o RH.");
        }
        return questionarios.detalharPara(d.questionarioId(), d.relacao());
    }

    private FormularioAvaliacao formulario(DesignacaoResumo d) {
        QuestionarioCompleto q = questionarioPara(d);
        List<RascunhoResposta> salvas = rascunhos.findByDesignacaoId(d.id());
        Map<Long, RespostaInformada> atuais = salvas.stream().collect(Collectors.toMap(
                RascunhoResposta::getPerguntaId, r -> new RespostaInformada(r.getNota(), r.isNaoAvalia(), r.getTexto())));
        OffsetDateTime ultimo = salvas.stream().map(RascunhoResposta::getAtualizadoEm).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);
        String motivo = motivoBloqueio(d);

        boolean contexto = d.relacao() == TipoAvaliador.CHEFE && d.avaliadorId() != null
                && usuarios.ehGestorDireto(d.avaliadorId(), d.avaliadoId());
        List<ObservacaoResumo> obs = List.of();
        LocalDate inicio = null;
        LocalDate fim = null;
        ResumoResultadoAnterior anterior = null;
        if (contexto) {
            CicloResumo ciclo = ciclos.ciclo(d.cicloId());
            inicio = inicioDasObservacoes(ciclo);
            fim = Calendario.hoje().isBefore(ciclo.dataEncerramento()) ? Calendario.hoje() : ciclo.dataEncerramento();
            obs = observacoes.observacoesDoPeriodo(d.avaliadorId(), d.avaliadoId(), inicio, fim);
            ResultadoAnterior fonte = resultadoAnterior.getIfAvailable();
            anterior = fonte == null ? null : fonte.doAvaliado(d.avaliadoId(), d.cicloId()).orElse(null);
        }
        return new FormularioAvaliacao(d, usuarios.buscar(d.avaliadoId()), q, atuais,
                motivo == null ? pendentes(q, atuais) : List.of(), motivo == null, motivo, contexto, obs, inicio, fim,
                anterior, ultimo);
    }

    /** Observações desde o encerramento do ciclo anterior (o dia seguinte) ou, sem ciclo anterior, dos últimos 12 meses. */
    private LocalDate inicioDasObservacoes(CicloResumo atual) {
        return ciclos.encerrados().stream()
                .filter(c -> !c.id().equals(atual.id()) && c.dataEncerramento().isBefore(atual.dataAbertura()))
                .map(CicloResumo::dataEncerramento)
                .max(Comparator.naturalOrder())
                .map(dia -> dia.plusDays(1))
                .orElse(Calendario.hoje().minusMonths(12));
    }

    private OffsetDateTime salvarUma(DesignacaoResumo d, Long perguntaId, RespostaInformada r) {
        exigirEditavel(d);
        QuestionarioCompleto q = questionarioPara(d);
        PerguntaResumo p = pergunta(q, perguntaId);
        OffsetDateTime agora = Calendario.agora();
        gravarRascunho(d, p, validar(q, p, r), agora);
        marcarInicio(d);
        return agora;
    }

    private void salvarFormulario(DesignacaoResumo d, Map<Long, RespostaInformada> todas) {
        exigirEditavel(d);
        QuestionarioCompleto q = questionarioPara(d);
        OffsetDateTime agora = Calendario.agora();
        List<String> erros = new ArrayList<>();
        for (PerguntaResumo p : q.perguntas()) {
            try {
                gravarRascunho(d, p, validar(q, p, todas.getOrDefault(p.id(), RespostaInformada.VAZIA)), agora);
            } catch (RegraDeNegocioException e) {
                erros.add(e.getMessage());
            }
        }
        if (!erros.isEmpty()) {
            throw new RegraDeNegocioException(String.join(" ", erros));
        }
        marcarInicio(d);
    }

    private void marcarInicio(DesignacaoResumo d) {
        if (d.naoIniciada() && !rascunhos.findByDesignacaoId(d.id()).isEmpty()) {
            ciclos.marcarEmPreenchimento(d.id());
        }
    }

    private void gravarRascunho(DesignacaoResumo d, PerguntaResumo p, RespostaInformada r, OffsetDateTime quando) {
        Optional<RascunhoResposta> atual = rascunhos.findByDesignacaoIdAndPerguntaId(d.id(), p.id());
        if (r.vazia()) {
            atual.ifPresent(rascunhos::delete);
            return;
        }
        RascunhoResposta rascunho = atual.orElseGet(() -> new RascunhoResposta(d.id(), p.id()));
        rascunho.preencher(r.nota(), r.naoAvalia(), r.texto(), quando);
        rascunhos.save(rascunho);
    }

    private static PerguntaResumo pergunta(QuestionarioCompleto q, Long perguntaId) {
        return q.perguntas().stream().filter(p -> p.id().equals(perguntaId)).findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Esta pergunta não faz parte da avaliação."));
    }

    /** Normaliza a resposta conforme o tipo da pergunta (escala: nota ou "não tenho como avaliar"; aberta: texto). */
    private static RespostaInformada validar(QuestionarioCompleto q, PerguntaResumo p, RespostaInformada r) {
        if (p.escala()) {
            if (r.naoAvalia()) {
                return new RespostaInformada(null, true, null);
            }
            if (r.nota() != null && (r.nota() < 1 || r.nota() > q.escalaMax())) {
                throw new RegraDeNegocioException("A nota deve ficar entre 1 e " + q.escalaMax() + ".");
            }
            return new RespostaInformada(r.nota(), false, null);
        }
        String texto = r.texto() == null ? null : r.texto().strip();
        if (texto != null && texto.length() > TAMANHO_MAXIMO_TEXTO) {
            throw new RegraDeNegocioException("O comentário pode ter no máximo " + TAMANHO_MAXIMO_TEXTO
                    + " caracteres.");
        }
        return new RespostaInformada(null, false, texto == null || texto.isEmpty() ? null : texto);
    }

    /** Obrigatórias sem nota nem "não tenho como avaliar" (escala) ou sem texto (aberta). */
    static List<PerguntaResumo> pendentes(QuestionarioCompleto q, Map<Long, RespostaInformada> atuais) {
        return q.perguntas().stream()
                .filter(PerguntaResumo::obrigatoria)
                .filter(p -> atuais.getOrDefault(p.id(), RespostaInformada.VAZIA).vazia())
                .toList();
    }

    private void enviar(DesignacaoResumo d) {
        exigirEditavel(d);
        QuestionarioCompleto q = questionarioPara(d);
        List<RascunhoResposta> salvas = rascunhos.findByDesignacaoId(d.id());
        Map<Long, RespostaInformada> atuais = salvas.stream().collect(Collectors.toMap(
                RascunhoResposta::getPerguntaId, r -> new RespostaInformada(r.getNota(), r.isNaoAvalia(), r.getTexto())));
        List<PerguntaResumo> pendentes = pendentes(q, atuais);
        if (!pendentes.isEmpty()) {
            throw new AvaliacaoComPendenciasException(pendentes);
        }
        registrarEnvio(d, q, atuais);
        rascunhos.deleteAll(salvas);
        ciclos.marcarEnviada(d.id());
    }

    /** Linhas do envio: avaliação sem designação, sem avaliador e sem data; uma resposta por pergunta respondida. */
    private void registrarEnvio(DesignacaoResumo d, QuestionarioCompleto q, Map<Long, RespostaInformada> todas) {
        AvaliacaoEnviada a = avaliacoes.save(new AvaliacaoEnviada(d.cicloId(), d.avaliadoId(), d.relacao(),
                d.questionarioId(), verificador.daDesignacao(d.id())));
        List<Resposta> linhas = new ArrayList<>();
        for (PerguntaResumo p : q.perguntas()) {
            RespostaInformada r = validar(q, p, todas.getOrDefault(p.id(), RespostaInformada.VAZIA));
            if (!r.vazia()) {
                linhas.add(new Resposta(a.getId(), p.id(), r.nota(), r.naoAvalia(), r.texto()));
            }
        }
        respostas.saveAll(linhas);
    }
}
