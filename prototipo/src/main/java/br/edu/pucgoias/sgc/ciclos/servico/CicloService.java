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
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Interface do pacote ciclos para os outros (avaliacoes, resultados) e o ciclo de vida do ciclo: abertura com os
 * convites (UC-02, passos 13 e 14), encerramento (RF-09) com o {@link CicloEncerradoEvento}, liberação do resultado
 * (RF-24) e o andamento de cada designação (NAO_INICIADA → EM_PREENCHIMENTO → ENVIADA).
 */
@Service
@Transactional(readOnly = true)
public class CicloService {

    private final CicloRepository ciclos;
    private final DesignacaoRepository designacoes;
    private final AvaliadorExternoRepository externos;
    private final ConviteService convites;
    private final ApplicationEventPublisher eventos;

    public CicloService(CicloRepository ciclos, DesignacaoRepository designacoes, AvaliadorExternoRepository externos,
                        ConviteService convites, ApplicationEventPublisher eventos) {
        this.ciclos = ciclos;
        this.designacoes = designacoes;
        this.externos = externos;
        this.convites = convites;
        this.eventos = eventos;
    }

    // ==== Interface para os outros pacotes ====

    public CicloResumo ciclo(Long id) {
        return resumo(entidade(id));
    }

    /** Ciclos encerrados, do mais recente para o mais antigo. */
    public List<CicloResumo> encerrados() {
        return ciclos.findBySituacaoOrderByDataAberturaDescIdDesc(SituacaoCiclo.ENCERRADO).stream()
                .map(this::resumo).toList();
    }

    /** Ciclos abertos, do mais recente para o mais antigo. */
    public List<CicloResumo> abertos() {
        return ciclos.findBySituacaoOrderByDataAberturaDescIdDesc(SituacaoCiclo.ABERTO).stream()
                .map(this::resumo).toList();
    }

    /** O ciclo está aberto e o prazo não passou (RF-09): ainda aceita respostas. */
    public boolean aceitaRespostas(Long cicloId) {
        return ciclos.findById(cicloId)
                .map(c -> c.isAberto() && !Datas.hoje().isAfter(c.getDataEncerramento()))
                .orElse(false);
    }

    /** Avaliações que a pessoa tem a fazer ou já fez, nos ciclos abertos e encerrados. */
    public List<DesignacaoResumo> designacoesDoAvaliador(Long usuarioId) {
        return resumos(designacoes.doAvaliadorNosCiclos(usuarioId,
                EnumSet.of(SituacaoCiclo.ABERTO, SituacaoCiclo.ENCERRADO)));
    }

    public DesignacaoResumo designacao(Long id) {
        return resumos(List.of(designacaoEntidade(id))).get(0);
    }

    /** Designação do avaliador externo pelo token do link individual (RF-34). */
    public Optional<DesignacaoResumo> designacaoPorToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return designacoes.findByToken(token).map(d -> resumos(List.of(d)).get(0));
    }

    /** O avaliador começou a preencher (primeiro salvamento). Sem efeito se já estava em preenchimento. */
    @Transactional
    public void marcarEmPreenchimento(Long designacaoId) {
        Designacao d = designacaoEntidade(designacaoId);
        if (d.getSituacao() == SituacaoDesignacao.EM_PREENCHIMENTO) {
            return;
        }
        d.marcarEmPreenchimento();
    }

    /** O avaliador enviou a avaliação. Recusa um segundo envio (evita duas avaliações da mesma designação). */
    @Transactional
    public void marcarEnviada(Long designacaoId) {
        designacaoEntidade(designacaoId).marcarEnviada();
    }

    /** UC-09: ENVIADA → EM_PREENCHIMENTO. Quem chama (avaliacoes) audita e avisa o avaliador. */
    @Transactional
    public void reabrir(Long designacaoId) {
        designacaoEntidade(designacaoId).reabrir();
    }

    public List<DesignacaoResumo> designacoesDoCiclo(Long cicloId) {
        entidade(cicloId);
        return resumos(designacoes.findByCicloIdOrderById(cicloId));
    }

    /** Pessoas avaliadas no ciclo (ids de usuário). */
    public List<Long> avaliadosDoCiclo(Long cicloId) {
        return entidade(cicloId).getAvaliados().keySet().stream().sorted().toList();
    }

    /** Questionário usado para a pessoa no ciclo (o do cargo dela quando foi incluída), ou {@code null}. */
    public Long questionarioDoAvaliado(Long cicloId, Long avaliadoId) {
        return entidade(cicloId).questionarioDo(avaliadoId);
    }

    /** RF-24: marca o resultado do ciclo encerrado como liberado. Quem chama (resultados) audita e avisa por e-mail. */
    @Transactional
    public void liberarResultado(Long cicloId) {
        entidade(cicloId).liberarResultado();
    }

    // ==== Ciclo de vida ====

    /**
     * Abre o ciclo agendado e envia os convites (passo 14). {@code manual} = "Abrir agora" do RH, que não muda a
     * data de abertura.
     */
    @Transactional
    public ConviteService.ResultadoEnvio abrir(Long cicloId, boolean manual) {
        Ciclo c = entidade(cicloId);
        c.abrir();
        eventos.publishEvent(EventoAuditoria.de("CICLO_ABERTO", descricao(c),
                manual ? "Aberto antes da data pelo RH (\"Abrir agora\")" : "Data de abertura"));
        return convites.enviarConvitesPendentes(c);
    }

    /**
     * Encerra o ciclo aberto (RF-09) e publica o {@link CicloEncerradoEvento} na mesma transação.
     * {@code manual} = "Encerrar agora" do RH, que não muda a data de encerramento.
     */
    @Transactional
    public void encerrar(Long cicloId, boolean manual) {
        Ciclo c = entidade(cicloId);
        c.encerrar();
        eventos.publishEvent(EventoAuditoria.de("CICLO_ENCERRADO", descricao(c),
                manual ? "Encerrado antes da data pelo RH (\"Encerrar agora\")" : "Data de encerramento"));
        eventos.publishEvent(new CicloEncerradoEvento(c.getId()));
    }

    /** Extensão 14a: reenvia os convites que ficaram sem envio. */
    @Transactional
    public ConviteService.ResultadoEnvio reenviarConvites(Long cicloId) {
        Ciclo c = entidade(cicloId);
        if (!c.isAberto()) {
            return new ConviteService.ResultadoEnvio(0, 0, 0);
        }
        return convites.enviarConvitesPendentes(c);
    }

    /** Lembrete automático (RF-12): 3 dias antes do encerramento e no último dia. */
    @Transactional
    public ConviteService.ResultadoEnvio lembreteAutomatico(Long cicloId) {
        Ciclo c = entidade(cicloId);
        LocalDate hoje = Datas.hoje();
        boolean diaDeLembrar = hoje.equals(c.getDataEncerramento()) || hoje.equals(c.getDataEncerramento().minusDays(3));
        if (!c.isAberto() || !diaDeLembrar) {
            return new ConviteService.ResultadoEnvio(0, 0, 0);
        }
        return convites.lembrar(c, designacoes.findByCicloIdOrderById(cicloId));
    }

    /** Agendados cuja data de abertura chegou (para a rotina). */
    public List<Long> paraAbrir() {
        return ciclos.findBySituacaoAndDataAberturaLessThanEqual(SituacaoCiclo.AGENDADO, Datas.hoje()).stream()
                .map(Ciclo::getId).toList();
    }

    /** Abertos cuja data de encerramento já passou (para a rotina). */
    public List<Long> paraEncerrar() {
        return ciclos.findBySituacaoAndDataEncerramentoBefore(SituacaoCiclo.ABERTO, Datas.hoje()).stream()
                .map(Ciclo::getId).toList();
    }

    public List<Long> idsAbertos() {
        return ciclos.findBySituacaoOrderByDataAberturaDescIdDesc(SituacaoCiclo.ABERTO).stream()
                .map(Ciclo::getId).toList();
    }

    // ==== apoio (também usado pelos outros serviços do pacote) ====

    Ciclo entidade(Long id) {
        return ciclos.findById(id).orElseThrow(() -> new NaoEncontradoException("Ciclo não encontrado."));
    }

    private Designacao designacaoEntidade(Long id) {
        return designacoes.findById(id).orElseThrow(() -> new NaoEncontradoException("Avaliação não encontrada."));
    }

    static String descricao(Ciclo c) {
        return "Ciclo “" + c.getNome() + "” (nº " + c.getId() + ")";
    }

    CicloResumo resumo(Ciclo c) {
        Set<TipoAvaliador> tipos = c.tipos();
        if (!externos.findByCicloId(c.getId()).isEmpty()) {
            tipos.add(TipoAvaliador.EXTERNO);
        }
        return new CicloResumo(c.getId(), c.getNome(), c.getSituacao().name(), c.getDataAbertura(),
                c.getDataEncerramento(), c.isResultadoLiberado(), Set.copyOf(tipos));
    }

    List<DesignacaoResumo> resumos(Collection<Designacao> lista) {
        if (lista.isEmpty()) {
            return List.of();
        }
        Map<Long, Ciclo> porCiclo = new HashMap<>();
        Map<Long, AvaliadorExterno> porExterno = new HashMap<>();
        for (Designacao d : lista) {
            porCiclo.computeIfAbsent(d.getCicloId(), id -> {
                externos.findByCicloId(id).forEach(e -> porExterno.put(e.getId(), e));
                return entidade(id);
            });
        }
        return lista.stream().map(d -> {
            Ciclo c = porCiclo.get(d.getCicloId());
            AvaliadorExterno e = d.isExterna() ? porExterno.get(d.getAvaliadorExternoId()) : null;
            return new DesignacaoResumo(d.getId(), c.getId(), c.getNome(), c.getSituacao().name(),
                    c.getDataEncerramento(), d.getAvaliadoId(), d.getAvaliadorId(), d.getAvaliadorExternoId(),
                    e == null ? null : e.getNome(), e == null ? null : e.getEmail(), d.getRelacao(),
                    d.getSituacao().name(), c.questionarioDo(d.getAvaliadoId()));
        }).toList();
    }
}
