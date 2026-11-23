package br.edu.pucgoias.sgc.competencias.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.dominio.Competencia;
import br.edu.pucgoias.sgc.competencias.dominio.Pergunta;
import br.edu.pucgoias.sgc.competencias.dominio.Questionario;
import br.edu.pucgoias.sgc.competencias.dominio.SituacaoQuestionario;
import br.edu.pucgoias.sgc.competencias.dominio.TipoPergunta;
import br.edu.pucgoias.sgc.competencias.repositorio.CompetenciaRepository;
import br.edu.pucgoias.sgc.competencias.repositorio.QuestionarioRepository;
import br.edu.pucgoias.sgc.identidade.servico.CargoResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * UC-01: questionários por cargo (RF-03), escala com níveis descritos (RF-04), perguntas abertas (RF-05), modelos
 * prontos e cópia de questionário anterior (RF-07), validação antes de publicar (passo 12) e bloqueio de questionário
 * já usado (extensão 2b).
 *
 * <p>Também é a interface do pacote para ciclos, avaliacoes e resultados: {@link #publicadosDoCargo},
 * {@link #publicados}, {@link #resumo}, {@link #detalhar}, {@link #detalharPara} e {@link #temPerguntasPara}.
 */
@Service
@Transactional(readOnly = true)
public class QuestionarioService {

    private static final int TEXTO_PERGUNTA_MAXIMO = 500;
    private static final int DESCRICAO_NIVEL_MAXIMA = 200;

    private final QuestionarioRepository questionarios;
    private final CompetenciaRepository competencias;
    private final CompetenciaService catalogo;
    private final UsuarioService usuarios;
    private final ObjectProvider<ConsultaUsoQuestionario> consultasDeUso;
    private final ApplicationEventPublisher eventos;

    public QuestionarioService(QuestionarioRepository questionarios, CompetenciaRepository competencias,
                               CompetenciaService catalogo, UsuarioService usuarios,
                               ObjectProvider<ConsultaUsoQuestionario> consultasDeUso,
                               ApplicationEventPublisher eventos) {
        this.questionarios = questionarios;
        this.competencias = competencias;
        this.catalogo = catalogo;
        this.usuarios = usuarios;
        this.consultasDeUso = consultasDeUso;
        this.eventos = eventos;
    }

    // ==== Interface para os outros pacotes ====

    /** Questionários publicados de um cargo, para a criação de ciclos (UC-02). */
    public List<QuestionarioResumo> publicadosDoCargo(Long cargoId) {
        return questionarios.findByModeloFalseAndSituacaoAndCargoIdOrderByNome(SituacaoQuestionario.PUBLICADO, cargoId)
                .stream().map(QuestionarioService::resumo).toList();
    }

    /** Todos os questionários publicados (sem os modelos prontos). */
    public List<QuestionarioResumo> publicados() {
        return questionarios.findByModeloFalseAndSituacaoOrderByNome(SituacaoQuestionario.PUBLICADO)
                .stream().map(QuestionarioService::resumo).toList();
    }

    public QuestionarioResumo resumo(Long id) {
        return resumo(entidade(id));
    }

    /** Questionário inteiro: escala, níveis e competências em ordem com todas as perguntas. */
    public QuestionarioCompleto detalhar(Long questionarioId) {
        return completo(entidade(questionarioId), null);
    }

    /**
     * Só as perguntas que se aplicam a este tipo de avaliador (RF-11), sem as competências que ficariam vazias.
     * É o formulário que o avaliador recebe no UC-03.
     */
    public QuestionarioCompleto detalharPara(Long questionarioId, TipoAvaliador tipo) {
        return completo(entidade(questionarioId), Objects.requireNonNull(tipo));
    }

    /** Se há ao menos uma pergunta para este tipo de avaliador (útil para não designar avaliador sem perguntas). */
    public boolean temPerguntasPara(Long questionarioId, TipoAvaliador tipo) {
        return entidade(questionarioId).getPerguntas().stream().anyMatch(p -> avaliadores(p).contains(tipo));
    }

    // ==== Caso de uso (telas do RH) ====

    /** Lista do passo 2: nome, cargo, situação e ciclos em que foi usado. */
    public List<QuestionarioNaLista> listar() {
        Map<Long, String> cargos = usuarios.cargos().stream()
                .collect(Collectors.toMap(CargoResumo::id, CargoResumo::nome));
        Map<Long, String> nomes = questionarios.findAll().stream()
                .collect(Collectors.toMap(Questionario::getId, Questionario::getNome));
        return questionarios.findByModeloFalseOrderByNome().stream().map(q -> {
            UsoQuestionario uso = uso(q.getId());
            return new QuestionarioNaLista(q.getId(), q.getNome(), cargos.get(q.getCargoId()), q.getSituacao().name(),
                    q.competenciaIds().size(), q.getPerguntas().size(),
                    q.getOrigemId() == null ? null : nomes.get(q.getOrigemId()), uso.ciclos(), uso.bloqueado());
        }).toList();
    }

    /** Modelos prontos (RF-07): chefe–colaborador, pares e 360º. */
    public List<QuestionarioCompleto> modelos() {
        return questionarios.findByModeloTrueOrderByNome().stream().map(q -> completo(q, null)).toList();
    }

    /** Questionários que podem ser copiados (extensão 5b). */
    public List<QuestionarioResumo> anteriores() {
        return questionarios.findByModeloFalseOrderByNome().stream().map(QuestionarioService::resumo).toList();
    }

    public long contarPublicados() {
        return questionarios.countByModeloFalseAndSituacao(SituacaoQuestionario.PUBLICADO);
    }

    public long contarRascunhos() {
        return questionarios.countByModeloFalseAndSituacao(SituacaoQuestionario.RASCUNHO);
    }

    /** Se o questionário já tem respostas ou está em algum ciclo (extensão 2b), pelas implementações de {@link ConsultaUsoQuestionario}. */
    public UsoQuestionario uso(Long questionarioId) {
        List<ConsultaUsoQuestionario> consultas = consultasDeUso.orderedStream().toList();
        boolean respostas = consultas.stream().anyMatch(c -> c.possuiRespostas(questionarioId));
        List<String> ciclos = consultas.stream().flatMap(c -> c.ciclosQueUsam(questionarioId).stream())
                .distinct().toList();
        return new UsoQuestionario(respostas, ciclos);
    }

    /** Passos 5 a 8: rascunho em branco com as competências padrão do cargo já escolhidas (RF-02). */
    @Transactional
    public Long criarEmBranco(String nome, Long cargoId, int escalaMax) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("Dê um nome ao questionário.");
        }
        if (cargoId == null) {
            throw new RegraDeNegocioException("Escolha o cargo a que o questionário se destina.");
        }
        usuarios.cargo(cargoId);
        Questionario q = new Questionario(nome, cargoId, escalaMax == 10 ? 10 : 5, false);
        q.definirCompetencias(new ArrayList<>(catalogo.padraoDoCargo(cargoId)));
        return questionarios.save(q).getId();
    }

    /** Extensão 5a: novo rascunho a partir de um modelo pronto. O RH escolhe o cargo no editor. */
    @Transactional
    public Long criarDeModelo(Long modeloId) {
        Questionario modelo = entidade(modeloId);
        if (!modelo.isModelo()) {
            throw new NaoEncontradoException("Modelo não encontrado.");
        }
        return questionarios.save(modelo.copiar(modelo.getNome())).getId();
    }

    /** Extensão 5b e 2b: cópia com a estrutura completa do original, como rascunho e sem respostas (RF-07). */
    @Transactional
    public Long duplicar(Long questionarioId) {
        Questionario original = entidade(questionarioId);
        return questionarios.save(original.copiar(original.getNome() + " (cópia)")).getId();
    }

    /**
     * Grava o que o RH informou no editor (passos 7 a 11). Sempre fica como rascunho: um questionário publicado que
     * for alterado volta a rascunho até ser publicado de novo, para nunca haver publicado incompleto.
     */
    @Transactional
    public void salvar(Long questionarioId, DadosQuestionario dados) {
        Questionario q = editavel(questionarioId);
        if (q.isPublicado()) {
            q.voltarARascunho();
        }
        if (dados.cargoId() != null) {
            usuarios.cargo(dados.cargoId());
        }
        q.alterarDados(dados.nome(), dados.cargoId());

        List<String> niveis = dados.niveis() == null ? List.of()
                : dados.niveis().stream().map(n -> cortar(n, DESCRICAO_NIVEL_MAXIMA)).toList();
        q.definirEscala(dados.escalaMax() == 10 ? 10 : 5, niveis);

        List<Long> pedidas = dados.competenciaIds() == null ? List.of()
                : dados.competenciaIds().stream().filter(Objects::nonNull).distinct().toList();
        Set<Long> existentes = competencias.findAllById(pedidas).stream().map(Competencia::getId)
                .collect(Collectors.toSet());
        List<Long> escolhidas = pedidas.stream().filter(existentes::contains).toList();
        q.definirCompetencias(escolhidas);

        Map<Long, Integer> ordemPorCompetencia = new HashMap<>();
        List<Pergunta> manter = new ArrayList<>();
        for (DadosPergunta d : dados.perguntas() == null ? List.<DadosPergunta>of() : dados.perguntas()) {
            if (d == null || d.texto() == null || d.texto().isBlank() || !escolhidas.contains(d.competenciaId())) {
                continue;
            }
            Pergunta p = d.id() == null ? null : q.pergunta(d.id()).filter(x -> !manter.contains(x)).orElse(null);
            if (p == null) {
                p = q.novaPergunta(d.competenciaId());
            }
            Set<TipoAvaliador> av = d.avaliadores() == null ? Set.of() : d.avaliadores();
            int ordem = ordemPorCompetencia.merge(d.competenciaId(), 1, Integer::sum);
            p.definir(d.competenciaId(), cortar(d.texto(), TEXTO_PERGUNTA_MAXIMO),
                    "ABERTA".equals(d.tipo()) ? TipoPergunta.ABERTA : TipoPergunta.ESCALA, d.obrigatoria(), ordem,
                    av.contains(TipoAvaliador.CHEFE), av.contains(TipoAvaliador.AUTO), av.contains(TipoAvaliador.PAR),
                    av.contains(TipoAvaliador.SUBORDINADO), av.contains(TipoAvaliador.EXTERNO));
            manter.add(p);
        }
        q.manterSomente(manter);

        if (dados.padraoDoCargo() && dados.cargoId() != null) {
            catalogo.definirPadraoDoCargo(dados.cargoId(), escolhidas);
        }
    }

    /** Inclui uma competência no questionário (depois de cadastrá-la sem sair do fluxo, extensão 9a). */
    @Transactional
    public void incluirCompetencia(Long questionarioId, Long competenciaId) {
        Questionario q = editavel(questionarioId);
        catalogo.entidade(competenciaId);
        q.incluirCompetencia(competenciaId);
    }

    /** Pendências que impedem a publicação (passo 12, extensão 12a). Vazio = pode publicar. */
    public List<String> pendencias(Long questionarioId) {
        return pendencias(entidade(questionarioId));
    }

    /**
     * Publica (passos 14 e 15). Se houver pendências, não publica, mantém o rascunho e devolve a lista delas.
     */
    @Transactional
    public List<String> publicar(Long questionarioId) {
        Questionario q = entidade(questionarioId);
        if (q.isPublicado()) {
            return List.of();
        }
        q = editavel(questionarioId);
        List<String> faltas = pendencias(q);
        if (!faltas.isEmpty()) {
            return faltas;
        }
        q.publicar();
        eventos.publishEvent(EventoAuditoria.de("QUESTIONARIO_PUBLICADO",
                "Questionário “" + q.getNome() + "” (nº " + q.getId() + ")"));
        return List.of();
    }

    /** Usado pela carga de exemplo: cria um modelo pronto (sem cargo) já disponível. */
    @Transactional
    Long criarModelo(String nome, DadosQuestionario dados) {
        Questionario q = questionarios.save(new Questionario(nome, null, dados.escalaMax(), true));
        salvar(q.getId(), dados);
        q.alterarDados(nome, null);
        q.publicar();
        return q.getId();
    }

    // ==== apoio ====

    private Questionario editavel(Long questionarioId) {
        Questionario q = entidade(questionarioId);
        if (q.isModelo() && !q.isPublicado()) {
            return q;
        }
        if (q.isModelo()) {
            throw new RegraDeNegocioException("Modelos prontos não são alterados. Crie um questionário a partir dele.");
        }
        UsoQuestionario uso = uso(questionarioId);
        if (uso.bloqueado()) {
            throw new RegraDeNegocioException(uso.motivo());
        }
        return q;
    }

    private List<String> pendencias(Questionario q) {
        Map<Long, Competencia> porId = competencias.findAllById(q.competenciaIds()).stream()
                .collect(Collectors.toMap(Competencia::getId, Function.identity()));
        return q.pendencias(porId::get);
    }

    private Questionario entidade(Long id) {
        return questionarios.findById(id).orElseThrow(() -> new NaoEncontradoException("Questionário não encontrado."));
    }

    private QuestionarioCompleto completo(Questionario q, TipoAvaliador filtro) {
        Map<Long, Competencia> porId = competencias.findAllById(q.competenciaIds()).stream()
                .collect(Collectors.toMap(Competencia::getId, Function.identity()));
        List<CompetenciaDoQuestionario> lista = new ArrayList<>();
        for (Long cid : q.competenciaIds()) {
            Competencia c = porId.get(cid);
            List<PerguntaResumo> perguntas = q.perguntasDa(cid).stream().map(QuestionarioService::resumo)
                    .filter(p -> filtro == null || p.seAplicaA(filtro)).toList();
            if (c == null || (filtro != null && perguntas.isEmpty())) {
                continue;
            }
            lista.add(new CompetenciaDoQuestionario(c.getId(), c.getNome(), c.getDescricao(), c.getTipo().name(),
                    c.isAtiva(), perguntas));
        }
        List<NivelResumo> niveis = q.getNiveis().stream().map(n -> new NivelResumo(n.getValor(), n.getDescricao())).toList();
        return new QuestionarioCompleto(q.getId(), q.getNome(), q.getCargoId(), q.getEscalaMax(), q.getSituacao().name(),
                q.isModelo(), q.getOrigemId(), niveis, List.copyOf(lista));
    }

    private static QuestionarioResumo resumo(Questionario q) {
        return new QuestionarioResumo(q.getId(), q.getNome(), q.getCargoId(), q.getEscalaMax(), q.getSituacao().name());
    }

    private static PerguntaResumo resumo(Pergunta p) {
        return new PerguntaResumo(p.getId(), p.getCompetenciaId(), p.getTexto(), p.getTipo().name(), p.isObrigatoria(),
                avaliadores(p), p.getOrdem());
    }

    private static Set<TipoAvaliador> avaliadores(Pergunta p) {
        EnumSet<TipoAvaliador> s = EnumSet.noneOf(TipoAvaliador.class);
        if (p.isParaChefe()) s.add(TipoAvaliador.CHEFE);
        if (p.isParaAuto()) s.add(TipoAvaliador.AUTO);
        if (p.isParaPar()) s.add(TipoAvaliador.PAR);
        if (p.isParaSubordinado()) s.add(TipoAvaliador.SUBORDINADO);
        if (p.isParaExterno()) s.add(TipoAvaliador.EXTERNO);
        return Collections.unmodifiableSet(s);
    }

    private static String cortar(String texto, int maximo) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        return t.length() <= maximo ? t : t.substring(0, maximo);
    }
}
