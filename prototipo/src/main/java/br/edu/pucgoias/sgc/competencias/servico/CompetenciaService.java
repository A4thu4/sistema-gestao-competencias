package br.edu.pucgoias.sgc.competencias.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.dominio.CargoCompetencia;
import br.edu.pucgoias.sgc.competencias.dominio.Competencia;
import br.edu.pucgoias.sgc.competencias.dominio.Questionario;
import br.edu.pucgoias.sgc.competencias.dominio.SituacaoQuestionario;
import br.edu.pucgoias.sgc.competencias.dominio.TipoCompetencia;
import br.edu.pucgoias.sgc.competencias.repositorio.CargoCompetenciaRepository;
import br.edu.pucgoias.sgc.competencias.repositorio.CompetenciaRepository;
import br.edu.pucgoias.sgc.competencias.repositorio.QuestionarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Catálogo de competências (RF-01) e conjunto padrão de competências de cada cargo (RF-02). UC-01, extensões 2c,
 * 9a, 9b e 9c. Competências são desativadas, nunca apagadas.
 */
@Service
@Transactional(readOnly = true)
public class CompetenciaService {

    static final int NOME_MAXIMO = 100;
    static final int DESCRICAO_MAXIMA = 500;

    private final CompetenciaRepository competencias;
    private final CargoCompetenciaRepository cargoCompetencias;
    private final QuestionarioRepository questionarios;
    private final ApplicationEventPublisher eventos;

    public CompetenciaService(CompetenciaRepository competencias, CargoCompetenciaRepository cargoCompetencias,
                              QuestionarioRepository questionarios, ApplicationEventPublisher eventos) {
        this.competencias = competencias;
        this.cargoCompetencias = cargoCompetencias;
        this.questionarios = questionarios;
        this.eventos = eventos;
    }

    // ---- Interface para os outros pacotes ----

    /** Competências ativas, em ordem alfabética. */
    public List<CompetenciaResumo> competenciasAtivas() {
        return competencias.findByAtivaTrueOrderByNome().stream().map(CompetenciaService::resumo).toList();
    }

    /** Competências (ativas ou não) pelos ids, para mostrar nomes em relatórios. */
    public Map<Long, CompetenciaResumo> competenciasPorIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return competencias.findAllById(ids).stream().map(CompetenciaService::resumo)
                .collect(Collectors.toMap(CompetenciaResumo::id, Function.identity()));
    }

    public CompetenciaResumo buscar(Long id) {
        return resumo(entidade(id));
    }

    // ---- Catálogo (extensão 2c) ----

    /** Catálogo completo, ativas primeiro; {@code busca} filtra por nome ou descrição. */
    public List<CompetenciaResumo> catalogo(String busca) {
        String termo = busca == null ? "" : busca.trim().toLowerCase(Locale.ROOT);
        return competencias.findAllByOrderByNome().stream()
                .filter(c -> termo.isEmpty() || c.getNome().toLowerCase(Locale.ROOT).contains(termo)
                        || c.getDescricao().toLowerCase(Locale.ROOT).contains(termo))
                .sorted(Comparator.comparing((Competencia c) -> !c.isAtiva()))
                .map(CompetenciaService::resumo).toList();
    }

    public long totalNoCatalogo() {
        return competencias.count();
    }

    /** Quantos questionários (rascunhos e publicados, sem contar modelos) usam cada competência. */
    public Map<Long, Long> usoPorCompetencia() {
        Map<Long, Long> uso = new HashMap<>();
        for (Object[] linha : questionarios.contarUsoPorCompetencia()) {
            uso.put((Long) linha[0], ((Number) linha[1]).longValue());
        }
        return uso;
    }

    /** Cadastra a competência (extensão 9a). Nome repetido gera {@link CompetenciaDuplicadaException} (9b). */
    @Transactional
    public Long cadastrar(DadosCompetencia dados) {
        Competencia c = validar(dados);
        comNome(c.getNome()).ifPresent(existente -> {
            throw new CompetenciaDuplicadaException(resumo(existente));
        });
        return competencias.save(c).getId();
    }

    @Transactional
    public void alterar(Long id, DadosCompetencia dados) {
        Competencia c = entidade(id);
        Competencia novos = validar(dados);
        comNome(novos.getNome())
                .filter(outra -> !outra.getId().equals(id))
                .ifPresent(outra -> {
                    throw new CompetenciaDuplicadaException(resumo(outra));
                });
        c.alterar(novos.getNome(), novos.getDescricao(), novos.getTipo());
    }

    /** Questionários publicados que usam a competência: a tela lista e pede confirmação antes de desativar. */
    public List<String> questionariosPublicadosQueUsam(Long competenciaId) {
        return questionarios.usandoCompetencia(competenciaId, SituacaoQuestionario.PUBLICADO).stream()
                .map(Questionario::getNome).toList();
    }

    /**
     * Desativa sem apagar: some do catálogo dos próximos questionários; os publicados e as respostas continuam
     * como estão.
     */
    @Transactional
    public void desativar(Long id) {
        Competencia c = entidade(id);
        if (c.isAtiva()) {
            c.desativar();
            eventos.publishEvent(EventoAuditoria.de("COMPETENCIA_DESATIVADA", "Competência “" + c.getNome() + "”"));
        }
    }

    @Transactional
    public void reativar(Long id) {
        Competencia c = entidade(id);
        if (!c.isAtiva()) {
            c.reativar();
            eventos.publishEvent(EventoAuditoria.de("COMPETENCIA_REATIVADA", "Competência “" + c.getNome() + "”"));
        }
    }

    // ---- Conjunto padrão do cargo (RF-02, extensão 9c) ----

    /** Competências padrão do cargo (só as ativas). */
    public Set<Long> padraoDoCargo(Long cargoId) {
        if (cargoId == null) {
            return Set.of();
        }
        Set<Long> ids = cargoCompetencias.findByCargoId(cargoId).stream()
                .map(CargoCompetencia::getCompetenciaId).collect(Collectors.toSet());
        return competencias.findAllById(ids).stream().filter(Competencia::isAtiva)
                .sorted(Comparator.comparing(Competencia::getNome))
                .map(Competencia::getId).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Padrão de todos os cargos que têm algum: cargo → competências. */
    public Map<Long, List<Long>> padroesPorCargo() {
        Set<Long> ativas = competencias.findByAtivaTrueOrderByNome().stream().map(Competencia::getId)
                .collect(Collectors.toSet());
        return cargoCompetencias.findAll().stream()
                .filter(cc -> ativas.contains(cc.getCompetenciaId()))
                .collect(Collectors.groupingBy(CargoCompetencia::getCargoId,
                        Collectors.mapping(CargoCompetencia::getCompetenciaId, Collectors.toList())));
    }

    /** Substitui o conjunto padrão do cargo pela seleção informada. */
    @Transactional
    public void definirPadraoDoCargo(Long cargoId, Collection<Long> competenciaIds) {
        Objects.requireNonNull(cargoId, "cargo");
        cargoCompetencias.deleteAll(cargoCompetencias.findByCargoId(cargoId));
        cargoCompetencias.flush();
        competenciaIds.stream().filter(Objects::nonNull).distinct()
                .forEach(cid -> cargoCompetencias.save(new CargoCompetencia(cargoId, cid)));
    }

    // ---- apoio ----

    private Competencia validar(DadosCompetencia dados) {
        String nome = dados.nome() == null ? "" : dados.nome().trim().replaceAll("\\s+", " ");
        String descricao = dados.descricao() == null ? "" : dados.descricao().trim();
        if (nome.isEmpty() || descricao.isEmpty()) {
            throw new RegraDeNegocioException("Informe o nome e a descrição da competência.");
        }
        if (nome.length() > NOME_MAXIMO) {
            throw new RegraDeNegocioException("O nome da competência pode ter até " + NOME_MAXIMO + " letras.");
        }
        if (descricao.length() > DESCRICAO_MAXIMA) {
            throw new RegraDeNegocioException("A descrição pode ter até " + DESCRICAO_MAXIMA + " letras.");
        }
        TipoCompetencia tipo;
        try {
            tipo = TipoCompetencia.valueOf(dados.tipo());
        } catch (RuntimeException e) {
            throw new RegraDeNegocioException("Escolha se a competência é técnica ou comportamental.");
        }
        return new Competencia(nome, descricao, tipo);
    }

    /** Busca por nome sem diferenciar maiúsculas (inclusive acentuadas, por isso em Java e não no banco). */
    private Optional<Competencia> comNome(String nome) {
        return competencias.findAll().stream().filter(c -> c.getNome().equalsIgnoreCase(nome)).findFirst();
    }

    Competencia entidade(Long id) {
        return competencias.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Competência não encontrada."));
    }

    static CompetenciaResumo resumo(Competencia c) {
        return new CompetenciaResumo(c.getId(), c.getNome(), c.getDescricao(), c.getTipo().name(), c.isAtiva());
    }
}
