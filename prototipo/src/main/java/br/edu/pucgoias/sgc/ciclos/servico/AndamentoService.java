package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.ciclos.dominio.AvaliadorExterno;
import br.edu.pucgoias.sgc.ciclos.dominio.Ciclo;
import br.edu.pucgoias.sgc.ciclos.dominio.Designacao;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoCiclo;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoDesignacao;
import br.edu.pucgoias.sgc.ciclos.repositorio.AvaliadorExternoRepository;
import br.edu.pucgoias.sgc.ciclos.repositorio.CicloRepository;
import br.edu.pucgoias.sgc.ciclos.repositorio.DesignacaoRepository;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * O RH vê a organização inteira; o gestor vê só as designações em que o avaliado ou o avaliador é da sua equipe
 * direta, conferido no servidor. O painel mostra quem já respondeu, nunca o conteúdo das respostas.
 */
@Service
@Transactional(readOnly = true)
public class AndamentoService {

    private final CicloRepository ciclos;
    private final DesignacaoRepository designacoes;
    private final AvaliadorExternoRepository externos;
    private final CicloService cicloService;
    private final ConviteService convites;
    private final UsuarioService usuarios;
    private final ApplicationEventPublisher eventos;

    public AndamentoService(CicloRepository ciclos, DesignacaoRepository designacoes,
                            AvaliadorExternoRepository externos, CicloService cicloService, ConviteService convites,
                            UsuarioService usuarios, ApplicationEventPublisher eventos) {
        this.ciclos = ciclos;
        this.designacoes = designacoes;
        this.externos = externos;
        this.cicloService = cicloService;
        this.convites = convites;
        this.usuarios = usuarios;
        this.eventos = eventos;
    }

    /** Ciclos que podem ser acompanhados: abertos primeiro, depois os encerrados para consulta. */
    public List<CicloResumo> ciclosParaAcompanhar() {
        List<CicloResumo> lista = new ArrayList<>(cicloService.abertos());
        lista.addAll(cicloService.encerrados());
        return lista;
    }

    /**
     * Painel do ciclo para o usuário logado. {@code cicloId} nulo = o ciclo aberto mais recente (ou o encerrado
     * mais recente). {@code pessoaId} filtra as avaliações em que a pessoa avalia ou é avaliada.
     */
    public AndamentoCiclo andamento(UsuarioAutenticado eu, Long cicloId, Long pessoaId) {
        Ciclo c = escolher(cicloId);
        Escopo escopo = escopo(eu);
        PessoaResumo filtro = null;
        if (pessoaId != null) {
            if (!escopo.inclui(pessoaId)) {
                throw new AccessDeniedException("Pessoa fora da equipe do gestor.");
            }
            filtro = usuarios.buscar(pessoaId);
        }
        List<Designacao> ds = visiveis(c, escopo).stream()
                .filter(d -> pessoaId == null || pessoaId.equals(d.getAvaliadoId())
                        || pessoaId.equals(d.getAvaliadorId()))
                .toList();

        Set<Long> ids = new HashSet<>();
        ds.forEach(d -> {
            ids.add(d.getAvaliadoId());
            if (d.getAvaliadorId() != null) ids.add(d.getAvaliadorId());
        });
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(ids);
        Map<Long, AvaliadorExterno> porExterno = externos.findByCicloId(c.getId()).stream()
                .collect(Collectors.toMap(AvaliadorExterno::getId, Function.identity()));

        Map<String, List<Designacao>> porAvaliador = new LinkedHashMap<>();
        for (Designacao d : ds) {
            String chave = d.isExterna() ? "E" + d.getAvaliadorExternoId() : "U" + d.getAvaliadorId();
            porAvaliador.computeIfAbsent(chave, k -> new ArrayList<>()).add(d);
        }
        List<AvaliadorNoAndamento> linhas = new ArrayList<>();
        for (List<Designacao> grupo : porAvaliador.values()) {
            Designacao primeira = grupo.get(0);
            String nome;
            String email;
            if (primeira.isExterna()) {
                AvaliadorExterno e = porExterno.get(primeira.getAvaliadorExternoId());
                nome = e.getNome();
                email = e.getEmail();
            } else {
                PessoaResumo p = pessoas.get(primeira.getAvaliadorId());
                nome = p == null ? "—" : p.nome();
                email = p == null ? null : p.email();
            }
            List<ItemAndamento> itens = grupo.stream().map(d -> new ItemAndamento(d.getId(), d.getAvaliadoId(),
                            d.getAvaliadorId() != null && d.getAvaliadorId().equals(d.getAvaliadoId()) ? "Autoavaliação"
                                    : nomeDe(pessoas, d.getAvaliadoId()),
                            d.getRelacao(), d.getSituacao().name()))
                    .sorted(Comparator.comparing(ItemAndamento::enviada).thenComparing(ItemAndamento::avaliadoNome))
                    .toList();
            OffsetDateTime lembrete = grupo.stream().map(Designacao::getUltimoLembreteEm).filter(Objects::nonNull)
                    .max(Comparator.naturalOrder()).orElse(null);
            linhas.add(new AvaliadorNoAndamento(primeira.getAvaliadorId(), nome, email, primeira.isExterna(), itens,
                    lembrete == null ? null : Datas.dataHora(lembrete)));
        }
        // Quem tem pendências primeiro, depois por nome.
        linhas.sort(Comparator.comparing(AvaliadorNoAndamento::concluiu).thenComparing(AvaliadorNoAndamento::nome));

        int naoIniciadas = contar(ds, SituacaoDesignacao.NAO_INICIADA);
        int emPreenchimento = contar(ds, SituacaoDesignacao.EM_PREENCHIMENTO);
        int enviadas = contar(ds, SituacaoDesignacao.ENVIADA);
        List<PessoaResumo> filtraveis = escopo.todos ? pessoasDoCiclo(c)
                : usuarios.equipeDireta(eu.id());
        return new AndamentoCiclo(cicloService.resumo(c), ds.size(), naoIniciadas, emPreenchimento, enviadas, linhas,
                !escopo.todos, filtraveis, filtro, c.isAberto());
    }

    /**
     * Lembrete manual aos pendentes (RF-12): o RH lembra todos; o gestor, só os da própria equipe. Quem já recebeu
     * lembrete hoje não recebe de novo.
     */
    @Transactional
    public ConviteService.ResultadoEnvio enviarLembrete(UsuarioAutenticado eu, Long cicloId) {
        Ciclo c = cicloService.entidade(cicloId);
        if (!c.isAberto()) {
            throw new RegraDeNegocioException("Só é possível enviar lembretes enquanto o ciclo está aberto.");
        }
        Escopo escopo = escopo(eu);
        ConviteService.ResultadoEnvio r = convites.lembrar(c, visiveis(c, escopo));
        if (r.enviados() > 0) {
            eventos.publishEvent(EventoAuditoria.de("LEMBRETE_ENVIADO", CicloService.descricao(c),
                    "Lembrete manual a " + r.enviados() + (r.enviados() == 1 ? " avaliador" : " avaliadores")
                            + (escopo.todos ? " (organização)" : " (equipe de " + eu.nome() + ")")));
        }
        return r;
    }

    /** Página inicial do gestor: avaliações pendentes da equipe nos ciclos abertos. */
    public long pendentesDaEquipe(UsuarioAutenticado eu) {
        Escopo escopo = escopo(eu);
        return ciclos.findBySituacaoOrderByDataAberturaDescIdDesc(SituacaoCiclo.ABERTO).stream()
                .flatMap(c -> visiveis(c, escopo).stream()).filter(Designacao::isPendente).count();
    }

    // ==== apoio ====

    private record Escopo(boolean todos, Set<Long> equipe) {
        boolean inclui(Long pessoaId) {
            return todos || equipe.contains(pessoaId);
        }

        boolean ve(Designacao d) {
            return todos || equipe.contains(d.getAvaliadoId())
                    || (d.getAvaliadorId() != null && equipe.contains(d.getAvaliadorId()));
        }
    }

    private Escopo escopo(UsuarioAutenticado eu) {
        if (eu == null) {
            throw new AccessDeniedException("Sem usuário.");
        }
        if (eu.ehRh()) {
            return new Escopo(true, Set.of());
        }
        if (eu.ehGestor()) {
            return new Escopo(false, usuarios.equipeDireta(eu.id()).stream().map(PessoaResumo::id)
                    .collect(Collectors.toSet()));
        }
        throw new AccessDeniedException("Só RH e gestores acompanham o andamento.");
    }

    private List<Designacao> visiveis(Ciclo c, Escopo escopo) {
        return designacoes.findByCicloIdOrderById(c.getId()).stream().filter(escopo::ve).toList();
    }

    private Ciclo escolher(Long cicloId) {
        if (cicloId != null) {
            Ciclo c = cicloService.entidade(cicloId);
            if (!c.isAberto() && !c.isEncerrado()) {
                throw new NaoEncontradoException("Este ciclo ainda não foi aberto; não há andamento para mostrar.");
            }
            return c;
        }
        return ciclos.findBySituacaoInOrderByDataAberturaDescIdDesc(EnumSet.of(SituacaoCiclo.ABERTO)).stream()
                .findFirst()
                .or(() -> ciclos.findBySituacaoOrderByDataAberturaDescIdDesc(SituacaoCiclo.ENCERRADO).stream().findFirst())
                .orElseThrow(() -> new NaoEncontradoException("Ainda não há ciclo aberto ou encerrado para acompanhar."));
    }

    private List<PessoaResumo> pessoasDoCiclo(Ciclo c) {
        Set<Long> ids = new HashSet<>(c.getAvaliados().keySet());
        designacoes.findByCicloIdOrderById(c.getId()).stream().map(Designacao::getAvaliadorId)
                .filter(Objects::nonNull).forEach(ids::add);
        return usuarios.buscarPorIds(ids).values().stream().sorted(Comparator.comparing(PessoaResumo::nome)).toList();
    }

    private static String nomeDe(Map<Long, PessoaResumo> pessoas, Long id) {
        PessoaResumo p = pessoas.get(id);
        return p == null ? "—" : p.nome();
    }

    private static int contar(List<Designacao> ds, SituacaoDesignacao s) {
        return (int) ds.stream().filter(d -> d.getSituacao() == s).count();
    }
}
