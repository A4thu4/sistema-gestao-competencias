package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.avaliacoes.servico.ConsultaRespostasService;
import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.resultados.dominio.ResultadoConsolidado;
import br.edu.pucgoias.sgc.resultados.repositorio.ResultadoConsolidadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Quem vê o quê, conferido no servidor:</b> o RH e o gestor (equipe direta) veem assim que o ciclo é encerrado;
 * cada pessoa vê o próprio resultado só depois que o RH o libera. Qualquer outra combinação lança
 * {@link AccessDeniedException} (página 403 sem dados).
 *
 * <p>Cada abertura de painel, relatório, liberação e exportação vai para o log de auditoria.
 */
@Service
@Transactional(readOnly = true)
public class ResultadoService {

    private final ResultadoConsolidadoRepository resultados;
    private final CicloService ciclos;
    private final UsuarioService usuarios;
    private final CompetenciaService competencias;
    private final EmailService emails;
    private final ExportacaoResultados exportacao;
    private final ConsultaRespostasService respostas;
    private final ApplicationEventPublisher eventos;
    private final String urlBase;

    public ResultadoService(ResultadoConsolidadoRepository resultados, CicloService ciclos, UsuarioService usuarios,
                            CompetenciaService competencias, EmailService emails, ExportacaoResultados exportacao,
                            ConsultaRespostasService respostas, ApplicationEventPublisher eventos,
                            @Value("${sgc.url-base:http://localhost:8080}") String urlBase) {
        this.resultados = resultados;
        this.ciclos = ciclos;
        this.usuarios = usuarios;
        this.competencias = competencias;
        this.emails = emails;
        this.exportacao = exportacao;
        this.respostas = respostas;
        this.eventos = eventos;
        this.urlBase = urlBase;
    }

    // ==== Passo 2: ciclos com resultado para o perfil ====

    /** Ciclos encerrados que o usuário pode consultar e os abertos de que participa (para o aviso 2c). */
    public List<CicloDisponivel> ciclosDisponiveis(UsuarioAutenticado eu) {
        Set<Long> consolidados = new HashSet<>(resultados.ciclosConsolidados());
        Set<Long> meus = new HashSet<>(resultados.ciclosDoAvaliado(eu.id()));
        List<CicloDisponivel> lista = new ArrayList<>();
        for (CicloResumo c : ciclos.abertos()) {
            if (eu.ehRh() || eu.ehGestor() || ciclos.avaliadosDoCiclo(c.id()).contains(eu.id())) {
                lista.add(new CicloDisponivel(c, false, 0, false, false, false));
            }
        }
        for (CicloResumo c : ciclos.encerrados()) {
            boolean consolidado = consolidados.contains(c.id());
            boolean meu = meus.contains(c.id());
            int pessoas = consolidado ? pessoasVisiveis(eu, c).size() : 0;
            if (eu.ehRh() || meu || pessoas > 0) {
                lista.add(new CicloDisponivel(c, consolidado, pessoas, meu, meu && (eu.ehRh() || c.resultadoLiberado()),
                        eu.ehRh() && consolidado && !c.resultadoLiberado()));
            }
        }
        return lista;
    }

    /** Pessoas com resultado no ciclo que o usuário pode consultar. */
    public List<PessoaComResultado> pessoasDoCiclo(UsuarioAutenticado eu, Long cicloId) {
        CicloResumo c = ciclos.ciclo(cicloId);
        if (!c.encerrado()) {
            return List.of();
        }
        return pessoasVisiveis(eu, c);
    }

    /** Gestores com equipe direta, para o RH escolher o relatório de equipe. */
    public List<PessoaResumo> gestoresComEquipe() {
        Set<Long> ids = usuarios.listarAtivos().stream().map(PessoaResumo::gestorId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return usuarios.buscarPorIds(ids).values().stream()
                .sorted(Comparator.comparing(PessoaResumo::nome, MontagemPainel.ordemAlfabetica())).toList();
    }

    public List<String> areas() {
        return usuarios.areas();
    }

    // ==== 2a: liberação pelo RH ====

    /**
     * Marca o resultado do ciclo como liberado, avisa por e-mail cada pessoa com resultado e registra no log. Devolve
     * quantos avisos foram enviados.
     */
    @Transactional
    public int liberar(UsuarioAutenticado eu, Long cicloId) {
        if (!eu.ehRh()) {
            throw new AccessDeniedException("Só o RH libera resultados.");
        }
        CicloResumo c = ciclos.ciclo(cicloId);
        if (!c.encerrado()) {
            throw new RegraDeNegocioException("O resultado só pode ser liberado depois que o ciclo for encerrado.");
        }
        return liberarCiclo(cicloId);
    }

    /** Liberação sem conferir o usuário (a carga de exemplo usa direto; a tela passa por {@link #liberar}). */
    @Transactional
    public int liberarCiclo(Long cicloId) {
        CicloResumo c = ciclos.ciclo(cicloId);
        if (!resultados.existsByCicloId(cicloId)) {
            throw new RegraDeNegocioException("Ainda não há resultado calculado para este ciclo.");
        }
        ciclos.liberarResultado(cicloId);
        Collection<PessoaResumo> avaliados = usuarios.buscarPorIds(resultados.avaliadosComResultado(cicloId)).values();
        for (PessoaResumo p : avaliados) {
            emails.enviar(p.email(), "Seu resultado do " + c.nome() + " foi liberado",
                    "Olá, " + primeiroNome(p.nome()) + ".\n\nO resultado da sua avaliação no " + c.nome()
                            + " já está disponível. Veja as médias por competência, os comentários e a sua evolução em "
                            + urlBase + "/resultados/" + cicloId + "/pessoas/" + p.id() + ".\n\n"
                            + "As respostas de pares e subordinados aparecem só agrupadas, sem identificar quem "
                            + "respondeu.", "RESULTADO_LIBERADO");
        }
        eventos.publishEvent(EventoAuditoria.de("RESULTADO_LIBERADO", descricao(c) + ": " + avaliados.size()
                + " pessoas avisadas por e-mail"));
        return avaliados.size();
    }

    /** Ciclos encerrados com resultado calculado e ainda não liberado (cartão do RH). */
    public List<CicloResumo> aguardandoLiberacao() {
        Set<Long> consolidados = new HashSet<>(resultados.ciclosConsolidados());
        return ciclos.encerrados().stream().filter(c -> !c.resultadoLiberado() && consolidados.contains(c.id()))
                .toList();
    }

    // ==== Passos 4 a 6: painel individual ====

    /** Confere o acesso e registra a abertura no log. */
    @Transactional
    public PainelIndividual painel(UsuarioAutenticado eu, Long cicloId, Long pessoaId) {
        PainelIndividual p = painelSemRegistro(eu, cicloId, pessoaId);
        eventos.publishEvent(EventoAuditoria.de("RESULTADO_ACESSADO", "Resultado de " + p.pessoa().nome()
                + " (nº " + pessoaId + ") no " + descricao(p.ciclo())));
        return p;
    }

    /** O mesmo painel em PDF ou Excel, com as mesmas restrições; registra no log. */
    @Transactional
    public ArquivoExportado exportarPainel(UsuarioAutenticado eu, Long cicloId, Long pessoaId, String formato) {
        PainelIndividual p = painelSemRegistro(eu, cicloId, pessoaId);
        String nome = "resultado-" + Formatos.paraArquivo(p.ciclo().nome()) + "-" + Formatos.paraArquivo(p.pessoa().nome());
        ArquivoExportado arquivo = excel(formato)
                ? new ArquivoExportado(nome + ".xlsx", ArquivoExportado.EXCEL, exportacao.painelExcel(p))
                : new ArquivoExportado(nome + ".pdf", ArquivoExportado.PDF, exportacao.painelPdf(p));
        eventos.publishEvent(EventoAuditoria.de("RESULTADO_EXPORTADO", "Resultado de " + p.pessoa().nome()
                + " (nº " + pessoaId + ") no " + descricao(p.ciclo()) + " em " + (excel(formato) ? "Excel" : "PDF")));
        return arquivo;
    }

    private PainelIndividual painelSemRegistro(UsuarioAutenticado eu, Long cicloId, Long pessoaId) {
        conferirAcesso(eu, pessoaId);
        CicloResumo c = ciclos.ciclo(cicloId);
        conferirDisponivel(eu, c, pessoaId);
        PessoaResumo pessoa = usuarios.buscar(pessoaId);
        List<LinhaConsolidada> consolidado = consolidado(cicloId, pessoaId);
        MontagemPainel.Montagem m = MontagemPainel.montar(consolidado, nomesCompetencias(consolidado));

        Set<TipoAvaliador> presentes = EnumSet.noneOf(TipoAvaliador.class);
        presentes.addAll(c.tiposAtivos());
        consolidado.forEach(l -> presentes.add(l.grupo()));
        List<TipoAvaliador> colunas = Grupos.emOrdem(presentes);

        List<SerieGrafico> series = new ArrayList<>();
        for (TipoAvaliador g : colunas) {
            List<BigDecimal> valores = m.linhas().stream().map(l -> l.grupo(g).media()).toList();
            if (valores.stream().anyMatch(Objects::nonNull)) {
                series.add(new SerieGrafico(Grupos.rotulo(g), valores));
            }
        }
        DadosRadar radar = new DadosRadar(m.linhas().stream().map(LinhaCompetencia::competencia).toList(), series,
                m.escalaMax());
        return new PainelIndividual(pessoa, c, m.escalaMax(), colunas, m.linhas(), m.mediaGeral(), m.pontoForte(),
                m.foco(), Grupos.emOrdem(m.gruposInsuficientes()), radar, comentarios(cicloId, pessoaId),
                evolucao(eu, pessoaId, c));
    }

    /** Comentários das perguntas abertas, sem autor e sem os ocultados na moderação. */
    private List<ComentarioPainel> comentarios(Long cicloId, Long pessoaId) {
        return respostas.comentariosVisiveis(cicloId, pessoaId).stream()
                .map(c -> new ComentarioPainel(c.competenciaNome(), c.perguntaTexto(), c.texto())).toList();
    }

    /** Nota final por competência nos ciclos encerrados da pessoa que o usuário pode ver, até o atual. */
    private Evolucao evolucao(UsuarioAutenticado eu, Long pessoaId, CicloResumo atual) {
        List<CicloResumo> doHistorico = resultados.ciclosDoAvaliado(pessoaId).stream().map(ciclos::ciclo)
                .filter(c -> c.encerrado() && liberadoPara(eu, pessoaId, c))
                .filter(c -> !c.dataAbertura().isAfter(atual.dataAbertura()))
                .sorted(Comparator.comparing(CicloResumo::dataAbertura).thenComparing(CicloResumo::id))
                .toList();
        Map<Long, MontagemPainel.Montagem> porCiclo = new LinkedHashMap<>();
        Map<Long, String> nomes = new TreeMap<>();
        for (CicloResumo c : doHistorico) {
            List<LinhaConsolidada> consolidado = consolidado(c.id(), pessoaId);
            MontagemPainel.Montagem m = MontagemPainel.montar(consolidado, nomesCompetencias(consolidado));
            porCiclo.put(c.id(), m);
            m.linhas().forEach(l -> nomes.put(l.competenciaId(), l.competencia()));
        }
        boolean normalizada = porCiclo.values().stream().map(MontagemPainel.Montagem::escalaMax).distinct().count() > 1;
        List<SerieGrafico> series = nomes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(MontagemPainel.ordemAlfabetica()))
                .map(e -> new SerieGrafico(e.getValue(), porCiclo.values().stream().map(m -> {
                    BigDecimal nota = m.notasFinais().get(e.getKey());
                    return normalizada ? RegrasDeExibicao.percentualDaEscala(nota, m.escalaMax()) : nota;
                }).toList()))
                .toList();
        return new Evolucao(doHistorico.stream().map(CicloResumo::nome).toList(), series, normalizada);
    }

    // ==== 3a: relatório de equipe ou de área ====

    /** Equipe direta de um gestor: o gestor só a própria; o RH qualquer uma. */
    @Transactional
    public RelatorioEquipe relatorioEquipe(UsuarioAutenticado eu, Long cicloId, Long gestorId) {
        RelatorioEquipe r = relatorioEquipeSemRegistro(eu, cicloId, gestorId);
        registrarRelatorio(r, "RELATORIO_ACESSADO", null);
        return r;
    }

    /** Área inteira: só o RH. */
    @Transactional
    public RelatorioEquipe relatorioArea(UsuarioAutenticado eu, Long cicloId, String area) {
        RelatorioEquipe r = relatorioAreaSemRegistro(eu, cicloId, area);
        registrarRelatorio(r, "RELATORIO_ACESSADO", null);
        return r;
    }

    /** Relatório de equipe ({@code gestorId}) ou de área ({@code area}) em PDF ou Excel; registra no log. */
    @Transactional
    public ArquivoExportado exportarRelatorio(UsuarioAutenticado eu, Long cicloId, Long gestorId, String area,
                                              String formato) {
        RelatorioEquipe r = gestorId != null ? relatorioEquipeSemRegistro(eu, cicloId, gestorId)
                : relatorioAreaSemRegistro(eu, cicloId, area);
        String nome = "relatorio-" + Formatos.paraArquivo(r.ciclo().nome()) + "-" + Formatos.paraArquivo(r.titulo());
        ArquivoExportado arquivo = excel(formato)
                ? new ArquivoExportado(nome + ".xlsx", ArquivoExportado.EXCEL, exportacao.relatorioExcel(r))
                : new ArquivoExportado(nome + ".pdf", ArquivoExportado.PDF, exportacao.relatorioPdf(r));
        registrarRelatorio(r, "RESULTADO_EXPORTADO", excel(formato) ? "Excel" : "PDF");
        return arquivo;
    }

    private RelatorioEquipe relatorioEquipeSemRegistro(UsuarioAutenticado eu, Long cicloId, Long gestorId) {
        if (gestorId == null || !(eu.ehRh() || (eu.ehGestor() && eu.id().equals(gestorId)))) {
            throw new AccessDeniedException("Relatório de equipe fora do alcance do usuário.");
        }
        CicloResumo c = cicloEncerrado(cicloId);
        PessoaResumo gestor = usuarios.buscar(gestorId);
        return relatorio(c, "Equipe de " + gestor.nome(), "Equipe direta de " + gestor.nome() + " (" + gestor.area() + ")",
                usuarios.equipeDireta(gestorId));
    }

    private RelatorioEquipe relatorioAreaSemRegistro(UsuarioAutenticado eu, Long cicloId, String area) {
        if (!eu.ehRh()) {
            throw new AccessDeniedException("Só o RH consulta o relatório por área.");
        }
        if (area == null || area.isBlank()) {
            throw new RegraDeNegocioException("Escolha a área.");
        }
        CicloResumo c = cicloEncerrado(cicloId);
        return relatorio(c, "Área " + area, "Todas as pessoas da área " + area, usuarios.listarPorArea(area));
    }

    private RelatorioEquipe relatorio(CicloResumo c, String titulo, String descricao, List<PessoaResumo> membros) {
        Map<Long, PessoaResumo> porId = membros.stream()
                .collect(Collectors.toMap(PessoaResumo::id, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        Map<Long, List<LinhaConsolidada>> porPessoa = porId.isEmpty() ? Map.of()
                : resultados.findByCicloIdAndAvaliadoIdIn(c.id(), porId.keySet()).stream().map(ResultadoService::linha).collect(Collectors.groupingBy(LinhaConsolidada::avaliadoId));
        Map<Long, String> nomes = nomesCompetencias(porPessoa.values().stream().flatMap(List::stream).toList());

        List<RelatorioEquipe.PessoaNoRelatorio> pessoas = new ArrayList<>();
        Map<Long, List<BigDecimal>> notasPorCompetencia = new TreeMap<>();
        int escala = 0;
        for (PessoaResumo p : porId.values()) {
            List<LinhaConsolidada> dela = porPessoa.get(p.id());
            if (dela == null) {
                continue;
            }
            MontagemPainel.Montagem m = MontagemPainel.montar(dela, nomes);
            escala = Math.max(escala, m.escalaMax());
            m.notasFinais().forEach((comp, nota) -> notasPorCompetencia.computeIfAbsent(comp, k -> new ArrayList<>()).add(nota));
            pessoas.add(new RelatorioEquipe.PessoaNoRelatorio(p, m.notasFinais(), m.mediaGeral()));
        }
        Map<Long, String> colunas = new LinkedHashMap<>();
        notasPorCompetencia.keySet().stream()
                .sorted(Comparator.comparing((Long id) -> nomes.getOrDefault(id, ""), MontagemPainel.ordemAlfabetica()))
                .forEach(id -> colunas.put(id, nomes.getOrDefault(id, "Competência " + id)));
        Map<Long, BigDecimal> mediaEquipe = new LinkedHashMap<>();
        colunas.keySet().forEach(id -> mediaEquipe.put(id, RegrasDeExibicao.media(notasPorCompetencia.get(id))));
        BigDecimal mediaGeral = RegrasDeExibicao.media(pessoas.stream()
                .map(RelatorioEquipe.PessoaNoRelatorio::mediaGeral).toList());
        return new RelatorioEquipe(c, titulo, descricao, colunas, pessoas, mediaEquipe, mediaGeral, escala);
    }

    private void registrarRelatorio(RelatorioEquipe r, String acao, String formato) {
        eventos.publishEvent(EventoAuditoria.de(acao, "Relatório “" + r.titulo() + "” do " + descricao(r.ciclo())
                + (formato == null ? "" : " em " + formato)));
    }

    // ==== Apoio para o próprio pacote (plano, página inicial, cópia dos dados, resultado anterior) ====

    /** Montagem do resultado da pessoa no ciclo, sem conferir acesso (quem chama confere). */
    public MontagemPainel.Montagem montagem(Long cicloId, Long pessoaId) {
        List<LinhaConsolidada> consolidado = consolidado(cicloId, pessoaId);
        return MontagemPainel.montar(consolidado, nomesCompetencias(consolidado));
    }

    /** Ciclos encerrados e liberados em que a pessoa tem resultado, do mais recente para o mais antigo. */
    public List<CicloResumo> ciclosLiberadosDe(Long pessoaId) {
        Set<Long> dela = new HashSet<>(resultados.ciclosDoAvaliado(pessoaId));
        return ciclos.encerrados().stream().filter(c -> c.resultadoLiberado() && dela.contains(c.id())).toList();
    }

    /** Ciclos encerrados em que a pessoa tem resultado, do mais recente para o mais antigo (liberados ou não). */
    public List<CicloResumo> ciclosEncerradosComResultado(Long pessoaId) {
        Set<Long> dela = new HashSet<>(resultados.ciclosDoAvaliado(pessoaId));
        return ciclos.encerrados().stream().filter(c -> dela.contains(c.id())).toList();
    }

    /** Último ciclo liberado em que a pessoa tem resultado (base do plano de desenvolvimento). */
    public Optional<CicloResumo> ultimoLiberado(Long pessoaId) {
        return ciclosLiberadosDe(pessoaId).stream().findFirst();
    }

    // ==== Regras de acesso (RNF-03) ====

    /** Só a própria pessoa, o gestor direto dela ou o RH. */
    private void conferirAcesso(UsuarioAutenticado eu, Long pessoaId) {
        if (eu.ehRh() || eu.id().equals(pessoaId)) {
            return;
        }
        if (eu.ehGestor() && usuarios.ehGestorDireto(eu.id(), pessoaId)) {
            return;
        }
        throw new AccessDeniedException("Resultado de outra pessoa.");
    }

    private void conferirDisponivel(UsuarioAutenticado eu, CicloResumo c, Long pessoaId) {
        if (!c.encerrado()) {
            throw new ResultadoIndisponivelException("Resultado ainda não disponível",
                    "O resultado do " + c.nome() + " só fica disponível depois que o ciclo for encerrado e as "
                            + "respostas forem consolidadas.");
        }
        if (!liberadoPara(eu, pessoaId, c)) {
            throw new ResultadoIndisponivelException("Resultado ainda não liberado",
                    "O RH ainda não liberou o resultado do " + c.nome() + ". Você receberá um aviso por e-mail "
                            + "quando ele for liberado.");
        }
        if (!resultados.existsByCicloIdAndAvaliadoId(c.id(), pessoaId)) {
            throw new ResultadoIndisponivelException("Sem resultado neste ciclo",
                    "Não há resultado calculado para esta pessoa no " + c.nome() + ".");
        }
    }

    /** A própria pessoa só vê depois da liberação; gestor e RH veem desde o encerramento. */
    private static boolean liberadoPara(UsuarioAutenticado eu, Long pessoaId, CicloResumo c) {
        return c.resultadoLiberado() || eu.ehRh() || !eu.id().equals(pessoaId);
    }

    private List<PessoaComResultado> pessoasVisiveis(UsuarioAutenticado eu, CicloResumo c) {
        List<Long> comResultado = resultados.avaliadosComResultado(c.id());
        Set<Long> equipe = eu.ehGestor()
                ? usuarios.equipeDireta(eu.id()).stream().map(PessoaResumo::id).collect(Collectors.toSet())
                : Set.of();
        List<Long> visiveis = comResultado.stream()
                .filter(id -> eu.ehRh() || id.equals(eu.id()) || equipe.contains(id)).toList();
        return usuarios.buscarPorIds(visiveis).values().stream()
                .sorted(Comparator.comparing((PessoaResumo p) -> !p.id().equals(eu.id()))
                        .thenComparing(PessoaResumo::nome, MontagemPainel.ordemAlfabetica()))
                .map(p -> new PessoaComResultado(p, p.id().equals(eu.id()), !liberadoPara(eu, p.id(), c)))
                .toList();
    }

    private CicloResumo cicloEncerrado(Long cicloId) {
        CicloResumo c = ciclos.ciclo(cicloId);
        if (!c.encerrado()) {
            throw new ResultadoIndisponivelException("Resultado ainda não disponível",
                    "O relatório do " + c.nome() + " só fica disponível depois que o ciclo for encerrado.");
        }
        return c;
    }

    private List<LinhaConsolidada> consolidado(Long cicloId, Long pessoaId) {
        return resultados.findByCicloIdAndAvaliadoId(cicloId, pessoaId).stream().map(ResultadoService::linha).toList();
    }

    private Map<Long, String> nomesCompetencias(Collection<LinhaConsolidada> linhas) {
        Set<Long> ids = linhas.stream().map(LinhaConsolidada::competenciaId).collect(Collectors.toSet());
        return competencias.competenciasPorIds(ids).values().stream()
                .collect(Collectors.toMap(CompetenciaResumo::id, CompetenciaResumo::nome));
    }

    static LinhaConsolidada linha(ResultadoConsolidado r) {
        return new LinhaConsolidada(r.getAvaliadoId(), r.getCompetenciaId(), r.getGrupo(), r.getMedia(),
                r.getNumRespostas(), r.getEscalaMax());
    }

    private static boolean excel(String formato) {
        return "excel".equalsIgnoreCase(formato) || "xlsx".equalsIgnoreCase(formato);
    }

    static String descricao(CicloResumo c) {
        return c.nome() + " (ciclo nº " + c.id() + ")";
    }

    static String primeiroNome(String nome) {
        return nome.trim().split("\\s+")[0];
    }
}
