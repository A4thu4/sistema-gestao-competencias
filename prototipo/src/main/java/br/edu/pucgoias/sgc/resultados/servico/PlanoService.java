package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.resultados.dominio.MetaAtualizacao;
import br.edu.pucgoias.sgc.resultados.dominio.MetaDesenvolvimento;
import br.edu.pucgoias.sgc.resultados.dominio.SituacaoMeta;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaAtualizacaoRepository;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaDesenvolvimentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * UC-06 Gerenciar plano de desenvolvimento (RF-27, RF-28).
 *
 * <ul>
 *   <li>O gestor escolhe uma pessoa da equipe direta cujo resultado do último ciclo já foi liberado, vê as
 *       competências de menor nota final sugeridas e cria metas (competência, descrição, ações, prazo, situação);</li>
 *   <li>a pessoa e o gestor direto registram o progresso e mudam a situação; cada atualização guarda data, autor e
 *       texto (linha do tempo);</li>
 *   <li>lembrete trimestral: meta aberta sem atualização há {@value #DIAS_LEMBRETE} dias gera e-mail ao gestor e à
 *       pessoa, no máximo uma vez a cada {@value #DIAS_LEMBRETE} dias; o gestor também pode mandar na hora;</li>
 *   <li>RNF-03: a pessoa vê só o próprio plano; o gestor, os da equipe direta; o RH consulta todos sem alterar.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class PlanoService {

    public static final int DIAS_LEMBRETE = 90;
    static final int MAX_SUGESTOES = 3;
    static final int DESCRICAO_MAXIMA = 300;
    static final int TEXTO_MAXIMO = 1000;
    private static final Set<SituacaoMeta> ABERTAS = EnumSet.of(SituacaoMeta.NAO_INICIADA, SituacaoMeta.EM_ANDAMENTO);

    private final MetaDesenvolvimentoRepository metas;
    private final MetaAtualizacaoRepository atualizacoes;
    private final ResultadoService resultados;
    private final UsuarioService usuarios;
    private final CompetenciaService competencias;
    private final CicloService ciclos;
    private final EmailService emails;
    private final ApplicationEventPublisher eventos;
    private final String urlBase;

    public PlanoService(MetaDesenvolvimentoRepository metas, MetaAtualizacaoRepository atualizacoes,
                        ResultadoService resultados, UsuarioService usuarios, CompetenciaService competencias,
                        CicloService ciclos, EmailService emails, ApplicationEventPublisher eventos,
                        @Value("${sgc.url-base:http://localhost:8080}") String urlBase) {
        this.metas = metas;
        this.atualizacoes = atualizacoes;
        this.resultados = resultados;
        this.usuarios = usuarios;
        this.competencias = competencias;
        this.ciclos = ciclos;
        this.emails = emails;
        this.eventos = eventos;
        this.urlBase = urlBase;
    }

    // ==== Consultas ====

    /** Gestor: a equipe direta; RH: todas as pessoas (só consulta). Colaborador não tem lista. */
    public List<MembroPlano> pessoas(UsuarioAutenticado eu) {
        List<PessoaResumo> lista;
        if (eu.ehRh()) {
            lista = usuarios.listarAtivos();
        } else if (eu.ehGestor()) {
            lista = usuarios.equipeDireta(eu.id());
        } else {
            throw new AccessDeniedException("Lista de planos só para gestor e RH.");
        }
        if (lista.isEmpty()) {
            return List.of();
        }
        Map<Long, List<MetaDesenvolvimento>> porPessoa = metas.findByColaboradorIdIn(
                lista.stream().map(PessoaResumo::id).toList()).stream()
                .collect(Collectors.groupingBy(MetaDesenvolvimento::getColaboradorId));
        return lista.stream().map(p -> {
            List<MetaDesenvolvimento> dela = porPessoa.getOrDefault(p.id(), List.of());
            return new MembroPlano(p, resultados.ultimoLiberado(p.id()).orElse(null),
                    dela.stream().filter(m -> m.getSituacao().aberta()).count(),
                    dela.stream().filter(m -> m.getSituacao() == SituacaoMeta.CONCLUIDA).count(), dela.size());
        }).toList();
    }

    public PlanoDesenvolvimento plano(UsuarioAutenticado eu, Long colaboradorId) {
        boolean proprio = eu.id().equals(colaboradorId);
        boolean gestorDireto = gestorDireto(eu, colaboradorId);
        if (!proprio && !gestorDireto && !eu.ehRh()) {
            throw new AccessDeniedException("Plano de outra pessoa.");
        }
        PessoaResumo colaborador = usuarios.buscar(colaboradorId);
        CicloResumo base = resultados.ultimoLiberado(colaboradorId).orElse(null);
        List<SugestaoMeta> sugestoes = new ArrayList<>();
        Map<Long, String> opcoes = new LinkedHashMap<>();
        if (base != null) {
            List<LinhaCompetencia> linhas = resultados.montagem(base.id(), colaboradorId).linhas();
            linhas.forEach(l -> opcoes.put(l.competenciaId(), l.competencia()));
            linhas.stream().filter(l -> l.notaFinal() != null)
                    .sorted(Comparator.comparing(LinhaCompetencia::notaFinal))
                    .limit(MAX_SUGESTOES)
                    .forEach(l -> sugestoes.add(new SugestaoMeta(l.competenciaId(), l.competencia(), l.notaFinal())));
        }
        return new PlanoDesenvolvimento(colaborador, base, sugestoes, opcoes, metasDe(colaboradorId),
                gestorDireto && base != null, proprio || gestorDireto, proprio);
    }

    /** Metas da pessoa com a linha do tempo, sem conferir acesso (quem chama confere). */
    public List<MetaResumo> metasDe(Long colaboradorId) {
        return resumos(metas.findByColaboradorIdOrderByPrazoAscIdAsc(colaboradorId));
    }

    /** Metas em andamento das pessoas da equipe direta (cartão da página inicial). */
    public long metasDaEquipeEmAndamento(Long gestorId) {
        List<Long> equipe = usuarios.equipeDireta(gestorId).stream().map(PessoaResumo::id).toList();
        if (equipe.isEmpty()) {
            return 0;
        }
        return metas.findByColaboradorIdIn(equipe).stream()
                .filter(m -> m.getSituacao() == SituacaoMeta.EM_ANDAMENTO).count();
    }

    public long metasAbertasDe(Long colaboradorId) {
        return metas.findByColaboradorIdOrderByPrazoAscIdAsc(colaboradorId).stream()
                .filter(m -> m.getSituacao().aberta()).count();
    }

    /** Situações possíveis (nome → rótulo), na ordem da tela. */
    public static Map<String, String> situacoes() {
        Map<String, String> m = new LinkedHashMap<>();
        for (SituacaoMeta s : SituacaoMeta.values()) {
            m.put(s.name(), s.getRotulo());
        }
        return m;
    }

    public static String rotuloSituacao(String situacao) {
        return SituacaoMeta.valueOf(situacao).getRotulo();
    }

    // ==== Alterações ====

    /** O gestor direto cria uma meta ligada a uma competência do último resultado liberado. */
    @Transactional
    public Long criarMeta(UsuarioAutenticado eu, Long colaboradorId, DadosMeta dados) {
        if (!gestorDireto(eu, colaboradorId)) {
            throw new AccessDeniedException("Só o gestor direto cria metas.");
        }
        PessoaResumo colaborador = usuarios.buscar(colaboradorId);
        CicloResumo base = resultados.ultimoLiberado(colaboradorId).orElseThrow(() -> new RegraDeNegocioException(
                "O plano é montado depois que o resultado do último ciclo da pessoa for liberado pelo RH."));
        Set<Long> permitidas = resultados.montagem(base.id(), colaboradorId).notasFinais().keySet();
        if (dados.competenciaId() == null || !permitidas.contains(dados.competenciaId())) {
            throw new RegraDeNegocioException("Escolha a competência da meta.");
        }
        String descricao = obrigatorio(dados.descricao(), "Descreva a meta.", DESCRICAO_MAXIMA, "A descrição");
        String acoes = obrigatorio(dados.acoes(), "Informe as ações previstas (por exemplo, um curso).", TEXTO_MAXIMO,
                "As ações");
        if (dados.prazo() == null) {
            throw new RegraDeNegocioException("Informe o prazo da meta.");
        }
        if (dados.prazo().isBefore(Formatos.hoje())) {
            throw new RegraDeNegocioException("O prazo não pode ser uma data que já passou.");
        }
        SituacaoMeta situacao = dados.situacao() == null || dados.situacao().isBlank()
                ? SituacaoMeta.NAO_INICIADA : situacao(dados.situacao());
        MetaDesenvolvimento meta = metas.save(new MetaDesenvolvimento(colaboradorId, eu.id(), base.id(),
                dados.competenciaId(), descricao, acoes, dados.prazo(), situacao, OffsetDateTime.now()));
        String competencia = nomeCompetencia(dados.competenciaId());
        emails.enviar(colaborador.email(), "Nova meta no seu plano de desenvolvimento",
                "Olá, " + ResultadoService.primeiroNome(colaborador.nome()) + ".\n\n" + eu.nome()
                        + " registrou uma meta no seu plano de desenvolvimento:\n\n" + competencia + ": " + descricao
                        + "\nAções: " + acoes + "\nPrazo: " + Formatos.data(dados.prazo()) + "\n\nRegistre o seu "
                        + "progresso em " + urlBase + "/planos/" + colaboradorId + ".", "PLANO_META");
        eventos.publishEvent(EventoAuditoria.de("META_CRIADA", "Meta nº " + meta.getId() + " (" + competencia
                + ") no plano de " + colaborador.nome()));
        return meta.getId();
    }

    /** A pessoa ou o gestor direto registra o progresso e a situação da meta (RF-28). Devolve o dono da meta. */
    @Transactional
    public Long atualizarMeta(UsuarioAutenticado eu, Long metaId, String situacao, String progresso) {
        MetaDesenvolvimento meta = metas.findById(metaId)
                .orElseThrow(() -> new NaoEncontradoException("Meta não encontrada."));
        if (!eu.id().equals(meta.getColaboradorId()) && !gestorDireto(eu, meta.getColaboradorId())) {
            throw new AccessDeniedException("Meta de outra pessoa.");
        }
        SituacaoMeta nova = situacao(situacao);
        String texto = obrigatorio(progresso, "Escreva o progresso desta atualização.", TEXTO_MAXIMO, "O progresso");
        meta.mudarSituacao(nova);
        atualizacoes.save(new MetaAtualizacao(meta.getId(), eu.id(), nova, texto, OffsetDateTime.now()));
        return meta.getColaboradorId();
    }

    /** Botão do gestor: lembrete de revisão das metas abertas agora, sem esperar o trimestre. */
    @Transactional
    public int lembrarAgora(UsuarioAutenticado eu, Long colaboradorId) {
        if (!gestorDireto(eu, colaboradorId)) {
            throw new AccessDeniedException("Só o gestor direto envia o lembrete.");
        }
        List<MetaDesenvolvimento> abertas = metas.findByColaboradorIdOrderByPrazoAscIdAsc(colaboradorId).stream()
                .filter(m -> m.getSituacao().aberta()).toList();
        if (abertas.isEmpty()) {
            throw new RegraDeNegocioException("Não há metas abertas para revisar.");
        }
        PessoaResumo colaborador = usuarios.buscar(colaboradorId);
        lembrar(colaborador, abertas, OffsetDateTime.now());
        eventos.publishEvent(EventoAuditoria.de("LEMBRETE_METAS", "Lembrete de revisão do plano de "
                + colaborador.nome() + " enviado pelo gestor (" + abertas.size() + " metas abertas)"));
        return abertas.size();
    }

    /** Rotina diária (RF-28). */
    @Transactional
    public int enviarLembretesTrimestrais() {
        return enviarLembretesTrimestrais(OffsetDateTime.now());
    }

    /**
     * Lembra as metas abertas sem atualização há {@value #DIAS_LEMBRETE} dias ou mais e sem lembrete nesse período.
     * Um e-mail para a pessoa e outro para o gestor, com todas as metas dela que estão paradas. Devolve quantas
     * metas foram lembradas.
     */
    @Transactional
    public int enviarLembretesTrimestrais(OffsetDateTime agora) {
        OffsetDateTime limite = agora.minusDays(DIAS_LEMBRETE);
        List<MetaDesenvolvimento> abertas = metas.findBySituacaoIn(ABERTAS);
        if (abertas.isEmpty()) {
            return 0;
        }
        Map<Long, OffsetDateTime> ultimaAtualizacao = new HashMap<>();
        atualizacoes.findByMetaIdInOrderByCriadaEmDescIdDesc(abertas.stream().map(MetaDesenvolvimento::getId).toList())
                .forEach(a -> ultimaAtualizacao.putIfAbsent(a.getMetaId(), a.getCriadaEm()));
        Map<Long, List<MetaDesenvolvimento>> paradas = new TreeMap<>();
        for (MetaDesenvolvimento m : abertas) {
            OffsetDateTime atividade = ultimaAtualizacao.getOrDefault(m.getId(), m.getCriadaEm());
            boolean semAtualizacao = !atividade.isAfter(limite);
            boolean semLembreteRecente = m.getUltimoLembreteEm() == null || !m.getUltimoLembreteEm().isAfter(limite);
            if (semAtualizacao && semLembreteRecente) {
                paradas.computeIfAbsent(m.getColaboradorId(), k -> new ArrayList<>()).add(m);
            }
        }
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(paradas.keySet());
        int total = 0;
        for (var e : paradas.entrySet()) {
            PessoaResumo p = pessoas.get(e.getKey());
            if (p == null || !p.ativo()) {
                continue;
            }
            lembrar(p, e.getValue(), agora);
            total += e.getValue().size();
        }
        if (total > 0) {
            eventos.publishEvent(EventoAuditoria.de("LEMBRETE_METAS", "Lembrete trimestral automático: " + total
                    + " metas de " + paradas.size() + " pessoas"));
        }
        return total;
    }

    // ==== apoio ====

    private void lembrar(PessoaResumo colaborador, List<MetaDesenvolvimento> lista, OffsetDateTime agora) {
        StringBuilder itens = new StringBuilder();
        for (MetaDesenvolvimento m : lista) {
            itens.append("- ").append(nomeCompetencia(m.getCompetenciaId())).append(": ").append(m.getDescricao())
                    .append(" (prazo ").append(Formatos.data(m.getPrazo())).append(", ")
                    .append(m.getSituacao().getRotulo().toLowerCase()).append(")\n");
        }
        String link = urlBase + "/planos/" + colaborador.id();
        emails.enviar(colaborador.email(), "Lembrete: revise as metas do seu plano de desenvolvimento",
                "Olá, " + ResultadoService.primeiroNome(colaborador.nome()) + ".\n\nEstá na hora de revisar o seu "
                        + "plano de desenvolvimento com o seu gestor. Metas abertas:\n\n" + itens
                        + "\nRegistre o progresso de cada ação em " + link + ".", "LEMBRETE_METAS");
        Set<Long> gestores = new HashSet<>();
        if (colaborador.gestorId() != null) {
            gestores.add(colaborador.gestorId());
        } else {
            lista.stream().map(MetaDesenvolvimento::getGestorId).filter(Objects::nonNull).forEach(gestores::add);
        }
        for (PessoaResumo g : usuarios.buscarPorIds(gestores).values()) {
            emails.enviar(g.email(), "Lembrete: revise as metas do plano de " + colaborador.nome(),
                    "Olá, " + ResultadoService.primeiroNome(g.nome()) + ".\n\nEstá na hora de revisar com "
                            + colaborador.nome() + " as metas do plano de desenvolvimento:\n\n" + itens
                            + "\nAtualize a situação de cada meta em " + link + ".", "LEMBRETE_METAS");
        }
        lista.forEach(m -> m.registrarLembrete(agora));
    }

    private List<MetaResumo> resumos(List<MetaDesenvolvimento> lista) {
        if (lista.isEmpty()) {
            return List.of();
        }
        Map<Long, List<MetaAtualizacao>> historico = atualizacoes.findByMetaIdInOrderByCriadaEmDescIdDesc(
                lista.stream().map(MetaDesenvolvimento::getId).toList()).stream()
                .collect(Collectors.groupingBy(MetaAtualizacao::getMetaId, LinkedHashMap::new, Collectors.toList()));
        Set<Long> autores = historico.values().stream().flatMap(Collection::stream).map(MetaAtualizacao::getAutorId)
                .collect(Collectors.toSet());
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(autores);
        Map<Long, String> nomes = competencias.competenciasPorIds(lista.stream().map(MetaDesenvolvimento::getCompetenciaId)
                .collect(Collectors.toSet())).values().stream()
                .collect(Collectors.toMap(CompetenciaResumo::id, CompetenciaResumo::nome));
        Map<Long, String> nomesCiclos = new HashMap<>();
        return lista.stream().map(m -> new MetaResumo(m.getId(), m.getColaboradorId(), m.getCompetenciaId(),
                nomes.getOrDefault(m.getCompetenciaId(), "—"), m.getDescricao(), m.getAcoes(), m.getPrazo(),
                m.getSituacao().name(), m.getSituacao().getRotulo(),
                m.getCicloId() == null ? null : nomesCiclos.computeIfAbsent(m.getCicloId(), id -> ciclos.ciclo(id).nome()),
                m.getCriadaEm(),
                historico.getOrDefault(m.getId(), List.of()).stream().map(a -> new AtualizacaoMeta(
                        pessoas.containsKey(a.getAutorId()) ? pessoas.get(a.getAutorId()).nome() : "—",
                        a.getSituacao().name(), a.getSituacao().getRotulo(), a.getProgresso(), a.getCriadaEm()))
                        .toList()))
                .toList();
    }

    private boolean gestorDireto(UsuarioAutenticado eu, Long colaboradorId) {
        return eu.ehGestor() && !eu.id().equals(colaboradorId) && usuarios.ehGestorDireto(eu.id(), colaboradorId);
    }

    private String nomeCompetencia(Long id) {
        CompetenciaResumo c = competencias.competenciasPorIds(List.of(id)).get(id);
        return c == null ? "Competência" : c.nome();
    }

    private static SituacaoMeta situacao(String valor) {
        try {
            return SituacaoMeta.valueOf(valor);
        } catch (RuntimeException e) {
            throw new RegraDeNegocioException("Escolha a situação da meta.");
        }
    }

    private static String obrigatorio(String valor, String mensagemVazio, int maximo, String nomeCampo) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.isEmpty()) {
            throw new RegraDeNegocioException(mensagemVazio);
        }
        if (texto.length() > maximo) {
            throw new RegraDeNegocioException(nomeCampo + " pode ter até " + maximo + " letras.");
        }
        return texto;
    }
}
