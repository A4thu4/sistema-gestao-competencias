package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.dominio.Observacao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.ObservacaoRepository;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UC-08 Registrar observações sobre a equipe (RF-17). O gestor registra, ao longo do ano, pontos positivos e de
 * melhoria sobre as pessoas da sua equipe direta; o sistema guarda a data do fato e a data do registro e mostra a
 * lista cronológica por colaborador. No UC-03 (passo 4) o gestor vê as observações do período ao avaliar
 * ({@link #observacoesDoPeriodo}).
 *
 * <p>Acesso (RNF-03): só a equipe direta do gestor logado; fora dela, {@link AccessDeniedException} (página 403).
 * Só quem registrou pode editar ou excluir, e só até a observação entrar numa avaliação de ciclo encerrado. O colaborador não vê estas anotações na tela; elas entram na cópia dos
 * dados pessoais dele (LGPD, {@link FonteObservacoes}).
 */
@Service
@Transactional(readOnly = true)
public class ObservacaoService {

    /** Quem está sem observação há mais que isso aparece em destaque para o gestor. */
    public static final int DIAS_SEM_OBSERVACAO = 60;
    /** Período usado no cartão da página inicial. */
    public static final int DIAS_PERIODO_RECENTE = 90;
    static final int TAMANHO_MAXIMO = 1000;

    private final ObservacaoRepository observacoes;
    private final UsuarioService usuarios;
    private final ApplicationEventPublisher eventos;
    private final CicloService ciclos;

    public ObservacaoService(ObservacaoRepository observacoes, UsuarioService usuarios,
                             ApplicationEventPublisher eventos, CicloService ciclos) {
        this.observacoes = observacoes;
        this.usuarios = usuarios;
        this.eventos = eventos;
        this.ciclos = ciclos;
    }

    // ---------------------------------------------------------------- consultas do gestor (telas do UC-08)

    /** A equipe direta do gestor logado, com o resumo das observações de cada pessoa. */
    public List<MembroEquipe> equipe(UsuarioAutenticado eu) {
        List<PessoaResumo> equipe = usuarios.equipeDireta(eu.id());
        Map<Long, List<Observacao>> porPessoa = observacoes
                .findByColaboradorIdIn(equipe.stream().map(PessoaResumo::id).toList()).stream()
                .collect(Collectors.groupingBy(Observacao::getColaboradorId));
        LocalDate limite = Calendario.hoje().minusDays(DIAS_SEM_OBSERVACAO);
        List<MembroEquipe> membros = new ArrayList<>();
        for (PessoaResumo p : equipe) {
            List<Observacao> lista = porPessoa.getOrDefault(p.id(), List.of());
            int positivos = (int) lista.stream().filter(o -> o.getTipo() == TipoObservacao.POSITIVO).count();
            LocalDate ultima = lista.stream().map(Observacao::getDataFato).max(Comparator.naturalOrder()).orElse(null);
            membros.add(new MembroEquipe(p, lista.size(), positivos, lista.size() - positivos, ultima,
                    ultima == null || ultima.isBefore(limite)));
        }
        return membros;
    }

    /** Quantas observações sobre a equipe direta têm data do fato a partir de {@code desde}. */
    public long registradasDesde(UsuarioAutenticado eu, LocalDate desde) {
        List<Long> ids = usuarios.equipeDireta(eu.id()).stream().map(PessoaResumo::id).toList();
        return observacoes.findByColaboradorIdIn(ids).stream().filter(o -> !o.getDataFato().isBefore(desde)).count();
    }

    /** A pessoa, se for da equipe direta do gestor logado. */
    public PessoaResumo colaborador(UsuarioAutenticado eu, Long colaboradorId) {
        exigirEquipe(eu.id(), colaboradorId);
        return usuarios.buscar(colaboradorId);
    }

    /** Lista cronológica (mais recente primeiro) das observações sobre a pessoa, com o autor de cada uma. */
    public List<ObservacaoNaLista> doColaborador(UsuarioAutenticado eu, Long colaboradorId) {
        exigirEquipe(eu.id(), colaboradorId);
        List<Observacao> lista = observacoes.findByColaboradorIdOrderByDataFatoDescCriadaEmDesc(colaboradorId);
        Map<Long, PessoaResumo> autores = usuarios.buscarPorIds(
                lista.stream().map(Observacao::getGestorId).collect(Collectors.toSet()));
        List<CicloResumo> avaliacoesEncerradas = ciclosEncerradosEmQueFoiAvaliado(colaboradorId);
        return lista.stream().map(o -> {
            PessoaResumo autor = autores.get(o.getGestorId());
            boolean minha = o.registradaPor(eu.id());
            String travadaPor = cicloQueTrava(o, avaliacoesEncerradas);
            return new ObservacaoNaLista(ObservacaoResumo.de(o), autor == null ? "—" : autor.nome(), minha,
                    minha && travadaPor == null, travadaPor);
        }).toList();
    }

    /** A observação para o formulário de edição; só o autor, e só sobre alguém da equipe dele. */
    public ObservacaoResumo paraEditar(UsuarioAutenticado eu, Long observacaoId) {
        return ObservacaoResumo.de(minhaParaAlterar(eu, observacaoId));
    }

    // ---------------------------------------------------------------- registrar, alterar e excluir

    @Transactional
    public Long registrar(UsuarioAutenticado eu, DadosObservacao dados) {
        if (dados.colaboradorId() == null) {
            throw new RegraDeNegocioException("Escolha a pessoa da equipe.");
        }
        exigirEquipe(eu.id(), dados.colaboradorId());
        Validado v = validar(dados);
        Observacao o = observacoes.save(new Observacao(eu.id(), dados.colaboradorId(), v.tipo(), v.descricao(),
                dados.dataFato(), Calendario.agora()));
        auditar("OBSERVACAO_REGISTRADA", o);
        return o.getId();
    }

    @Transactional
    public void alterar(UsuarioAutenticado eu, Long observacaoId, DadosObservacao dados) {
        Observacao o = minhaParaAlterar(eu, observacaoId);
        Validado v = validar(dados);
        o.alterar(v.tipo(), v.descricao(), dados.dataFato());
        auditar("OBSERVACAO_ALTERADA", o);
    }

    @Transactional
    public void excluir(UsuarioAutenticado eu, Long observacaoId) {
        Observacao o = minhaParaAlterar(eu, observacaoId);
        auditar("OBSERVACAO_EXCLUIDA", o);
        observacoes.delete(o);
    }

    // ---------------------------------------------------------------- interface para o UC-03 e a LGPD

    /**
     * UC-03, passo 4 (RF-17): observações sobre o colaborador com data do fato entre {@code inicio} e {@code fim}
     * (inclusive), da mais recente para a mais antiga. Só para o gestor direto do colaborador (RNF-03); para
     * qualquer outra pessoa lança {@link AccessDeniedException}.
     */
    public List<ObservacaoResumo> observacoesDoPeriodo(Long gestorId, Long colaboradorId, LocalDate inicio,
                                                       LocalDate fim) {
        exigirEquipe(gestorId, colaboradorId);
        return observacoes.findByColaboradorIdAndDataFatoBetweenOrderByDataFatoDescCriadaEmDesc(
                colaboradorId, inicio, fim).stream().map(ObservacaoResumo::de).toList();
    }

    /** Todas as observações sobre a pessoa, para a cópia dos dados pessoais do próprio titular (UC-05, 7a). */
    List<ObservacaoResumo> sobreOTitular(Long usuarioId) {
        return observacoes.findByColaboradorIdOrderByDataFatoDescCriadaEmDesc(usuarioId).stream()
                .map(ObservacaoResumo::de).toList();
    }

    // ---------------------------------------------------------------- regras

    private void exigirEquipe(Long gestorId, Long colaboradorId) {
        if (gestorId == null || colaboradorId == null || !usuarios.ehGestorDireto(gestorId, colaboradorId)) {
            throw new AccessDeniedException("Pessoa fora da equipe direta do gestor.");
        }
    }

    private Observacao minhaParaAlterar(UsuarioAutenticado eu, Long observacaoId) {
        Observacao o = observacoes.findById(observacaoId)
                .orElseThrow(() -> new NaoEncontradoException("Observação não encontrada."));
        exigirEquipe(eu.id(), o.getColaboradorId());
        if (!o.registradaPor(eu.id())) {
            throw new AccessDeniedException("Só quem registrou a observação pode alterá-la.");
        }
        String travadaPor = cicloQueTrava(o, ciclosEncerradosEmQueFoiAvaliado(o.getColaboradorId()));
        if (travadaPor != null) {
            throw new RegraDeNegocioException("Esta observação já fez parte da avaliação do ciclo “" + travadaPor
                    + "” e não pode mais ser alterada nem excluída.");
        }
        return o;
    }

    /**
     * Decisão de projeto: só o autor edita ou exclui, e só enquanto a observação não entrou numa avaliação. Ela fica
     * travada quando o colaborador foi avaliado num ciclo que já encerrou e cujo encerramento é igual ou posterior à
     * data do fato (a observação estava no período mostrado ao gestor, RF-17).
     */
    private static String cicloQueTrava(Observacao o, List<CicloResumo> encerradosDoColaborador) {
        return encerradosDoColaborador.stream()
                .filter(c -> !c.dataEncerramento().isBefore(o.getDataFato()))
                .map(CicloResumo::nome)
                .findFirst().orElse(null);
    }

    private List<CicloResumo> ciclosEncerradosEmQueFoiAvaliado(Long colaboradorId) {
        return ciclos.encerrados().stream()
                .filter(c -> ciclos.avaliadosDoCiclo(c.id()).contains(colaboradorId))
                .toList();
    }

    private record Validado(TipoObservacao tipo, String descricao) {
    }

    private static Validado validar(DadosObservacao dados) {
        List<String> erros = new ArrayList<>();
        TipoObservacao tipo = null;
        try {
            tipo = dados.tipo() == null ? null : TipoObservacao.valueOf(dados.tipo());
        } catch (IllegalArgumentException e) {
            // tratado abaixo
        }
        if (tipo == null) {
            erros.add("Escolha se é um ponto positivo ou um ponto de melhoria.");
        }
        String descricao = dados.descricao() == null ? "" : dados.descricao().trim();
        if (descricao.isEmpty()) {
            erros.add("Descreva o fato observado.");
        } else if (descricao.length() > TAMANHO_MAXIMO) {
            erros.add("A descrição pode ter no máximo " + TAMANHO_MAXIMO + " caracteres.");
        }
        if (dados.dataFato() == null) {
            erros.add("Informe a data em que o fato aconteceu.");
        } else if (dados.dataFato().isAfter(Calendario.hoje())) {
            erros.add("A data do fato não pode ser depois de hoje.");
        }
        if (!erros.isEmpty()) {
            throw new RegraDeNegocioException(String.join(" ", erros));
        }
        return new Validado(tipo, descricao);
    }

    private void auditar(String acao, Observacao o) {
        String nome = usuarios.buscar(o.getColaboradorId()).nome();
        eventos.publishEvent(EventoAuditoria.de(acao, "Observação nº " + o.getId() + " sobre " + nome + " ("
                + o.getTipo().getRotulo().toLowerCase() + ", fato em " + Calendario.data(o.getDataFato()) + ")"));
    }
}
