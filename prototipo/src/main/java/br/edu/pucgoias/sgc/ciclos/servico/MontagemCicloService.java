package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.ciclos.dominio.AvaliadorExterno;
import br.edu.pucgoias.sgc.ciclos.dominio.Ciclo;
import br.edu.pucgoias.sgc.ciclos.dominio.Designacao;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoDesignacao;
import br.edu.pucgoias.sgc.ciclos.repositorio.AvaliadorExternoRepository;
import br.edu.pucgoias.sgc.ciclos.repositorio.CicloRepository;
import br.edu.pucgoias.sgc.ciclos.repositorio.DesignacaoRepository;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.CargoResumo;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Montagem do ciclo pelo RH: dados, avaliados, questionários, avaliadores, validação e agendamento.
 *
 * <p>Tudo o que o RH informa fica gravado como rascunho. Alterar um ciclo agendado o devolve a rascunho, para ser
 * conferido e agendado de novo.
 */
@Service
@Transactional
public class MontagemCicloService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");
    private static final int MINIMO_ANONIMATO = 3;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final CicloRepository ciclos;
    private final DesignacaoRepository designacoes;
    private final AvaliadorExternoRepository externos;
    private final CicloService cicloService;
    private final ConviteService convites;
    private final UsuarioService usuarios;
    private final QuestionarioService questionarios;
    private final ApplicationEventPublisher eventos;

    public MontagemCicloService(CicloRepository ciclos, DesignacaoRepository designacoes,
                                AvaliadorExternoRepository externos, CicloService cicloService,
                                ConviteService convites, UsuarioService usuarios, QuestionarioService questionarios,
                                ApplicationEventPublisher eventos) {
        this.ciclos = ciclos;
        this.designacoes = designacoes;
        this.externos = externos;
        this.cicloService = cicloService;
        this.convites = convites;
        this.usuarios = usuarios;
        this.questionarios = questionarios;
        this.eventos = eventos;
    }

    /** Resultado do agendamento: se o ciclo abriu na hora (13a) e quantos convites saíram. */
    public record ResultadoAgendamento(boolean abriu, LocalDate abertura, ConviteService.ResultadoEnvio envio) {
    }

    // ==== Passo 2: lista ====

    @Transactional(readOnly = true)
    public List<CicloNaLista> listar() {
        return ciclos.findAllByOrderByDataAberturaDescIdDesc().stream().map(c -> {
            List<Designacao> ds = designacoes.findByCicloIdOrderById(c.getId());
            int enviadas = (int) ds.stream().filter(d -> d.getSituacao() == SituacaoDesignacao.ENVIADA).count();
            return new CicloNaLista(cicloService.resumo(c), c.getAvaliados().size(), ds.size(), enviadas);
        }).toList();
    }

    // ==== Passos 4 a 6: dados do ciclo ====

    public Long criar(DadosCiclo dados) {
        validarDados(dados);
        Ciclo c = ciclos.save(new Ciclo(dados.nome(), dados.tipos(), dados.abertura(), dados.encerramento()));
        eventos.publishEvent(EventoAuditoria.de("CICLO_CRIADO", CicloService.descricao(c)));
        return c.getId();
    }

    /**
     * Extensão 2a: altera os dados de um ciclo em rascunho ou agendado. Tipos novos recebem os avaliadores
     * sugeridos; tipos retirados perdem as designações. Devolve {@code true} se o ciclo agendado voltou a rascunho.
     */
    public boolean alterarDados(Long cicloId, DadosCiclo dados) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        validarDados(dados);
        Set<TipoAvaliador> antes = c.tipos();
        c.alterarDados(dados.nome(), dados.tipos(), dados.abertura(), dados.encerramento());
        for (TipoAvaliador t : antes) {
            if (!dados.tipos().contains(t)) {
                apagar(designacoes.findByCicloIdOrderById(cicloId).stream().filter(d -> d.getRelacao() == t).toList());
            }
        }
        Set<TipoAvaliador> novos = emOrdem(dados.tipos());
        novos.removeAll(antes);
        if (!novos.isEmpty()) {
            sugerir(c, c.getAvaliados().keySet(), novos);
        }
        return c.voltarARascunho();
    }

    /** Em ciclo aberto, só a data de encerramento muda. */
    public void alterarEncerramento(Long cicloId, LocalDate novaData) {
        Ciclo c = cicloService.entidade(cicloId);
        Map<String, String> erros = new LinkedHashMap<>();
        if (novaData == null) {
            erros.put("encerramento", "Informe a nova data de encerramento.");
        } else if (!novaData.isAfter(c.getDataAbertura())) {
            erros.put("encerramento", "O encerramento deve ser depois da abertura (" + Datas.data(c.getDataAbertura()) + ").");
        } else if (novaData.isBefore(Datas.hoje())) {
            erros.put("encerramento", "O encerramento não pode ser anterior a hoje.");
        }
        if (!erros.isEmpty()) {
            throw new DadosInvalidosException(erros);
        }
        LocalDate antes = c.getDataEncerramento();
        c.alterarEncerramento(novaData);
        eventos.publishEvent(EventoAuditoria.de("CICLO_ALTERADO", CicloService.descricao(c),
                "Encerramento mudou de " + Datas.data(antes) + " para " + Datas.data(novaData)));
    }

    /** Exige nome, ao menos um tipo, abertura não anterior a hoje e encerramento depois da abertura. */
    public static Map<String, String> errosDosDados(DadosCiclo dados, LocalDate hoje) {
        Map<String, String> erros = new LinkedHashMap<>();
        if (dados.nome() == null || dados.nome().isBlank()) {
            erros.put("nome", "Dê um nome ao ciclo.");
        } else if (dados.nome().trim().length() > Ciclo.NOME_MAXIMO) {
            erros.put("nome", "Use no máximo " + Ciclo.NOME_MAXIMO + " caracteres no nome.");
        }
        if (dados.tipos().isEmpty() || dados.tipos().contains(TipoAvaliador.EXTERNO) && dados.tipos().size() == 1) {
            erros.put("tipos", "Marque ao menos um tipo de avaliação.");
        }
        if (dados.abertura() == null) {
            erros.put("abertura", "Informe a data de abertura.");
        } else if (dados.abertura().isBefore(hoje)) {
            erros.put("abertura", "A abertura não pode ser anterior a hoje.");
        }
        if (dados.encerramento() == null) {
            erros.put("encerramento", "Informe a data de encerramento.");
        } else if (dados.abertura() != null && !dados.encerramento().isAfter(dados.abertura())) {
            erros.put("encerramento", "O encerramento deve ser depois da abertura.");
        }
        return erros;
    }

    private void validarDados(DadosCiclo dados) {
        Map<String, String> erros = errosDosDados(dados, Datas.hoje());
        if (!erros.isEmpty()) {
            throw new DadosInvalidosException(erros);
        }
    }

    // ==== Passos 7 e 8: avaliados e questionários ====

    /**
     * Passo 7: avaliados por cargo, por área ou individualmente, e o questionário publicado de cada cargo. Quem
     * entra recebe os avaliadores sugeridos (passo 8); quem sai perde as designações. Devolve {@code true} se o
     * ciclo agendado voltou a rascunho.
     */
    public boolean salvarAvaliados(Long cicloId, Collection<Long> pessoas, Collection<Long> cargos,
                                   Collection<String> areas, Map<Long, Long> escolhaDeQuestionario) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        Map<Long, PessoaResumo> escolhidos = new LinkedHashMap<>();
        if (pessoas != null) {
            usuarios.buscarPorIds(pessoas.stream().filter(Objects::nonNull).toList()).values().stream()
                    .filter(PessoaResumo::ativo).forEach(p -> escolhidos.put(p.id(), p));
        }
        if (cargos != null) {
            cargos.stream().filter(Objects::nonNull).distinct()
                    .forEach(cargo -> usuarios.listarPorCargo(cargo).forEach(p -> escolhidos.put(p.id(), p)));
        }
        if (areas != null) {
            areas.stream().filter(a -> a != null && !a.isBlank()).distinct()
                    .forEach(a -> usuarios.listarPorArea(a).forEach(p -> escolhidos.put(p.id(), p)));
        }

        Set<Long> saem = new HashSet<>(c.getAvaliados().keySet());
        saem.removeAll(escolhidos.keySet());
        for (Long id : saem) {
            removerAvaliado(c, id);
        }
        List<Long> entram = new ArrayList<>();
        for (PessoaResumo p : escolhidos.values()) {
            if (!c.avalia(p.id())) {
                c.incluirAvaliado(p.id(), p.cargoId());
                entram.add(p.id());
            }
        }
        c.limparQuestionariosSemAvaliados();
        escolherQuestionarios(c, escolhaDeQuestionario == null ? Map.of() : escolhaDeQuestionario);
        sugerir(c, entram, c.tipos());
        return c.voltarARascunho();
    }

    /** Tira do ciclo as pessoas de um cargo sem questionário publicado. */
    public boolean removerCargo(Long cicloId, Long cargoId) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        c.getAvaliados().entrySet().stream().filter(e -> e.getValue().equals(cargoId)).map(Map.Entry::getKey)
                .toList().forEach(id -> removerAvaliado(c, id));
        c.limparQuestionariosSemAvaliados();
        return c.voltarARascunho();
    }

    /** Retira do ciclo um tipo sem perguntas no questionário. */
    public boolean retirarTipo(Long cicloId, TipoAvaliador tipo) {
        Ciclo c = cicloService.entidade(cicloId);
        c.retirarTipo(tipo);
        apagar(designacoes.findByCicloIdOrderById(cicloId).stream().filter(d -> d.getRelacao() == tipo).toList());
        return c.voltarARascunho();
    }

    private void escolherQuestionarios(Ciclo c, Map<Long, Long> escolha) {
        for (Long cargo : new HashSet<>(c.getAvaliados().values())) {
            List<QuestionarioResumo> opcoes = questionarios.publicadosDoCargo(cargo);
            Set<Long> validos = opcoes.stream().map(QuestionarioResumo::id).collect(Collectors.toSet());
            Long pedido = escolha.get(cargo);
            Long atual = c.getQuestionarioPorCargo().get(cargo);
            if (pedido != null && validos.contains(pedido)) {
                c.definirQuestionario(cargo, pedido);
            } else if (atual == null || !validos.contains(atual)) {
                c.definirQuestionario(cargo, opcoes.isEmpty() ? null : opcoes.get(0).id());
            }
        }
    }

    private void removerAvaliado(Ciclo c, Long avaliadoId) {
        apagar(designacoes.findByCicloIdAndAvaliadoIdOrderById(c.getId(), avaliadoId));
        c.removerAvaliado(avaliadoId);
    }

    // ==== Passos 8 e 9: avaliadores ====

    /**
     * Passo 8 (RF-10): chefe = gestor direto; subordinados = equipe direta; pares = pessoas com o mesmo gestor;
     * autoavaliação = a própria pessoa. Quem não tem chefe ou subordinados fica pendente (9a).
     */
    private void sugerir(Ciclo c, Collection<Long> avaliados, Set<TipoAvaliador> tipos) {
        if (avaliados.isEmpty() || tipos.isEmpty()) {
            return;
        }
        Set<String> existentes = designacoes.findByCicloIdOrderById(c.getId()).stream()
                .filter(d -> !d.isExterna()).map(MontagemCicloService::chave).collect(Collectors.toSet());
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(avaliados);
        for (Long id : avaliados) {
            PessoaResumo p = pessoas.get(id);
            if (p == null) {
                continue;
            }
            for (TipoAvaliador t : tipos) {
                for (Long avaliador : sugestoes(p, t)) {
                    Designacao d = Designacao.interna(c.getId(), id, avaliador, t);
                    if (existentes.add(chave(d))) {
                        designacoes.save(d);
                    }
                }
            }
        }
    }

    private List<Long> sugestoes(PessoaResumo p, TipoAvaliador tipo) {
        return switch (tipo) {
            case AUTO -> List.of(p.id());
            case CHEFE -> {
                if (p.gestorId() == null) {
                    yield List.of();
                }
                PessoaResumo gestor = usuarios.buscar(p.gestorId());
                yield gestor.ativo() ? List.of(gestor.id()) : List.of();
            }
            case PAR -> usuarios.pares(p.id()).stream().map(PessoaResumo::id).toList();
            case SUBORDINADO -> usuarios.equipeDireta(p.id()).stream().map(PessoaResumo::id).toList();
            case EXTERNO -> List.of();
        };
    }

    /** Refaz a sugestão de uma pessoa (ou de todas, com {@code avaliadoId} nulo), descartando os ajustes manuais. */
    public boolean refazerSugestao(Long cicloId, Long avaliadoId) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        List<Long> alvo = avaliadoId == null ? List.copyOf(c.getAvaliados().keySet()) : List.of(avaliadoId);
        for (Long id : alvo) {
            if (!c.avalia(id)) {
                throw new NaoEncontradoException("Esta pessoa não está no ciclo.");
            }
            apagar(designacoes.findByCicloIdAndAvaliadoIdOrderById(cicloId, id).stream()
                    .filter(d -> !d.isExterna()).toList());
        }
        designacoes.flush();
        sugerir(c, alvo, c.tipos());
        return c.voltarARascunho();
    }

    /**
     * Passo 9: inclui um avaliador interno para a pessoa, no tipo indicado. Em ciclo aberto (2b), o convite sai na
     * hora. Devolve {@code true} se o ciclo agendado voltou a rascunho.
     */
    public boolean incluirAvaliador(Long cicloId, Long avaliadoId, TipoAvaliador tipo, Long avaliadorId) {
        Ciclo c = cicloService.entidade(cicloId);
        exigirInclusaoPermitida(c);
        if (!c.avalia(avaliadoId)) {
            throw new NaoEncontradoException("Esta pessoa não está no ciclo.");
        }
        if (tipo == null || tipo == TipoAvaliador.EXTERNO || !c.tipos().contains(tipo)) {
            throw new RegraDeNegocioException("Escolha um dos tipos de avaliação do ciclo.");
        }
        if (avaliadorId == null) {
            throw new RegraDeNegocioException("Escolha quem vai avaliar.");
        }
        PessoaResumo avaliador = usuarios.buscar(avaliadorId);
        if (!avaliador.ativo()) {
            throw new RegraDeNegocioException(avaliador.nome() + " está inativo(a) e não pode avaliar.");
        }
        Designacao nova = Designacao.interna(c.getId(), avaliadoId, avaliadorId, tipo);
        boolean repetida = designacoes.findByCicloIdAndAvaliadoIdOrderById(cicloId, avaliadoId).stream()
                .anyMatch(d -> chave(d).equals(chave(nova)));
        if (repetida) {
            throw new RegraDeNegocioException(avaliador.nome() + " já está como avaliador(a) deste tipo.");
        }
        designacoes.save(nova);
        if (c.isAberto()) {
            PessoaResumo avaliado = usuarios.buscar(avaliadoId);
            eventos.publishEvent(EventoAuditoria.de("CICLO_ALTERADO", CicloService.descricao(c),
                    "Avaliador incluído com o ciclo aberto: " + avaliador.nome() + " avalia " + avaliado.nome()
                            + " (" + tipo.rotulo().toLowerCase() + ")"));
            convites.enviarConvitesPendentes(c);
        }
        return c.voltarARascunho();
    }

    /** Extensão 9b (RF-34): avaliador externo (só nome e e-mail) com link individual seguro. */
    public boolean incluirExterno(Long cicloId, Long avaliadoId, String nome, String email) {
        Ciclo c = cicloService.entidade(cicloId);
        exigirInclusaoPermitida(c);
        if (!c.avalia(avaliadoId)) {
            throw new NaoEncontradoException("Esta pessoa não está no ciclo.");
        }
        Map<String, String> erros = new LinkedHashMap<>();
        String n = nome == null ? "" : nome.trim();
        String e = email == null ? "" : email.trim().toLowerCase();
        if (n.isEmpty()) {
            erros.put("externoNome", "Informe o nome do avaliador externo.");
        } else if (n.length() > 120) {
            erros.put("externoNome", "Use no máximo 120 caracteres no nome.");
        }
        if (!emailValido(e)) {
            erros.put("externoEmail", "Informe um e-mail válido, como nome@empresa.com.");
        }
        if (!erros.isEmpty()) {
            throw new DadosInvalidosException(erros);
        }
        AvaliadorExterno externo = externos.save(new AvaliadorExterno(cicloId, n, e));
        designacoes.save(Designacao.externa(cicloId, avaliadoId, externo.getId(), novoToken()));
        if (c.isAberto()) {
            PessoaResumo avaliado = usuarios.buscar(avaliadoId);
            eventos.publishEvent(EventoAuditoria.de("CICLO_ALTERADO", CicloService.descricao(c),
                    "Avaliador externo incluído com o ciclo aberto para avaliar " + avaliado.nome()));
            convites.enviarConvitesPendentes(c);
        }
        return c.voltarARascunho();
    }

    /** Passo 9: remove um avaliador (só em rascunho ou agendado; em ciclo aberto só se inclui). */
    public boolean removerDesignacao(Long cicloId, Long designacaoId) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        Designacao d = designacoes.findById(designacaoId)
                .filter(x -> x.getCicloId().equals(cicloId))
                .orElseThrow(() -> new NaoEncontradoException("Avaliador não encontrado neste ciclo."));
        apagar(List.of(d));
        return c.voltarARascunho();
    }

    private static void exigirInclusaoPermitida(Ciclo c) {
        if (c.isEncerrado()) {
            throw new RegraDeNegocioException("O ciclo está encerrado e fica disponível só para consulta.");
        }
    }

    /** Token do link do avaliador externo: 32 bytes aleatórios, em Base64 próprio para URL (43 caracteres). */
    static String novoToken() {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static boolean emailValido(String email) {
        return email != null && email.length() <= 160 && EMAIL.matcher(email).matches();
    }

    private void apagar(List<Designacao> lista) {
        Set<Long> externosApagar = lista.stream().filter(Designacao::isExterna)
                .map(Designacao::getAvaliadorExternoId).collect(Collectors.toSet());
        designacoes.deleteAll(lista);
        designacoes.flush();
        if (!externosApagar.isEmpty()) {
            externos.deleteAllById(externosApagar);
        }
    }

    private static String chave(Designacao d) {
        return d.getAvaliadoId() + "|" + d.getAvaliadorId() + "|" + d.getRelacao();
    }

    // ==== Passos 10 a 13: validação, resumo e agendamento ====

    /** Tudo o que as telas de montagem e o resumo mostram, com a validação dos passos 8 e 10. */
    @Transactional(readOnly = true)
    public MontagemCiclo montagem(Long cicloId) {
        Ciclo c = cicloService.entidade(cicloId);
        List<Designacao> ds = designacoes.findByCicloIdOrderById(cicloId);
        Map<Long, AvaliadorExterno> porExterno = externos.findByCicloId(cicloId).stream()
                .collect(Collectors.toMap(AvaliadorExterno::getId, Function.identity()));
        Set<Long> ids = new HashSet<>(c.getAvaliados().keySet());
        ds.stream().map(Designacao::getAvaliadorId).filter(Objects::nonNull).forEach(ids::add);
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(ids);
        Map<Long, String> cargos = usuarios.cargos().stream()
                .collect(Collectors.toMap(CargoResumo::id, CargoResumo::nome));
        Map<Long, QuestionarioResumo> qs = new HashMap<>();
        c.getQuestionarioPorCargo().values().forEach(q -> qs.computeIfAbsent(q, questionarios::resumo));
        Map<Long, List<Designacao>> porAvaliado = ds.stream()
                .collect(Collectors.groupingBy(Designacao::getAvaliadoId));

        List<String> pendencias = new ArrayList<>();
        List<String> alertas = new ArrayList<>();
        if (c.getAvaliados().isEmpty()) {
            pendencias.add("Escolha ao menos uma pessoa para ser avaliada.");
        }

        List<Verificacao.CargoSemQuestionario> semQuestionario = new ArrayList<>();
        List<Verificacao.TipoSemPerguntas> semPerguntas = new ArrayList<>();
        Map<Long, Boolean> temEquipe = new HashMap<>();
        c.getAvaliados().keySet().forEach(id -> temEquipe.put(id, !usuarios.equipeDireta(id).isEmpty()));
        List<QuestionarioDoCargo> porCargo = new ArrayList<>();
        Map<Long, List<Long>> avaliadosPorCargo = c.getAvaliados().entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        for (var e : avaliadosPorCargo.entrySet().stream()
                .sorted(Comparator.comparing(x -> cargos.getOrDefault(x.getKey(), ""))).toList()) {
            Long cargo = e.getKey();
            String cargoNome = cargos.getOrDefault(cargo, "Cargo nº " + cargo);
            Long qid = c.getQuestionarioPorCargo().get(cargo);
            QuestionarioResumo q = qid == null ? null : qs.get(qid);
            porCargo.add(new QuestionarioDoCargo(cargo, cargoNome, e.getValue().size(), qid,
                    q == null ? null : q.nome(), c.isEditavel() ? questionarios.publicadosDoCargo(cargo) : List.of()));
            if (q == null) {
                semQuestionario.add(new Verificacao.CargoSemQuestionario(cargo, cargoNome));
                pendencias.add("O cargo " + cargoNome + " não tem questionário publicado. Publique um em "
                        + "Competências ou tire essas pessoas do ciclo.");
                continue;
            }
            Set<TipoAvaliador> usados = emOrdem(c.tipos());
            if (usados.contains(TipoAvaliador.SUBORDINADO)
                    && e.getValue().stream().noneMatch(id -> temEquipe.getOrDefault(id, false))) {
                usados.remove(TipoAvaliador.SUBORDINADO);
            }
            boolean comExterno = e.getValue().stream().anyMatch(id -> porAvaliado.getOrDefault(id, List.of())
                    .stream().anyMatch(Designacao::isExterna));
            if (comExterno) {
                usados.add(TipoAvaliador.EXTERNO);
            }
            for (TipoAvaliador t : usados) {
                if (!questionarios.temPerguntasPara(qid, t)) {
                    semPerguntas.add(new Verificacao.TipoSemPerguntas(cargo, cargoNome, qid, q.nome(), t));
                    pendencias.add("O questionário “" + q.nome() + "” (" + cargoNome + ") não tem perguntas para "
                            + rotuloTipo(t) + ".");
                }
            }
        }

        List<AvaliadoNoCiclo> avaliados = new ArrayList<>();
        Map<TipoAvaliador, Integer> totalPorTipo = new EnumMap<>(TipoAvaliador.class);
        for (Long id : c.getAvaliados().keySet()) {
            PessoaResumo p = pessoas.get(id);
            String nome = p == null ? "Pessoa nº " + id : p.nome();
            boolean equipe = temEquipe.getOrDefault(id, false);
            Map<TipoAvaliador, List<AvaliadorNoCiclo>> porTipo = new EnumMap<>(TipoAvaliador.class);
            List<String> pendPessoa = new ArrayList<>();
            List<String> alertaPessoa = new ArrayList<>();
            for (Designacao d : porAvaliado.getOrDefault(id, List.of())) {
                AvaliadorNoCiclo a;
                if (d.isExterna()) {
                    AvaliadorExterno ex = porExterno.get(d.getAvaliadorExternoId());
                    a = new AvaliadorNoCiclo(d.getId(), null, ex.getNome(), ex.getEmail(), true,
                            d.getSituacao().name(), d.getConviteEnviadoEm() != null);
                    if (!emailValido(ex.getEmail())) {
                        pendPessoa.add("O avaliador externo " + ex.getNome() + " está sem e-mail válido.");
                    }
                } else {
                    PessoaResumo av = pessoas.get(d.getAvaliadorId());
                    a = new AvaliadorNoCiclo(d.getId(), d.getAvaliadorId(), av == null ? "—" : av.nome(),
                            av == null ? null : av.email(), false, d.getSituacao().name(),
                            d.getConviteEnviadoEm() != null);
                    if (av == null || !av.ativo()) {
                        pendPessoa.add("Um avaliador de " + nome + " está inativo; troque-o.");
                    } else if (!emailValido(av.email())) {
                        pendPessoa.add(av.nome() + " está sem e-mail válido para receber o convite.");
                    }
                    if (d.getRelacao() != TipoAvaliador.AUTO && Objects.equals(d.getAvaliadorId(), id)) {
                        pendPessoa.add(nome + " não pode avaliar a si mesmo(a) fora da autoavaliação.");
                    }
                }
                porTipo.computeIfAbsent(d.getRelacao(), k -> new ArrayList<>()).add(a);
                totalPorTipo.merge(d.getRelacao(), 1, Integer::sum);
            }
            porTipo.values().forEach(l -> l.sort(Comparator.comparing(AvaliadorNoCiclo::nome)));
            for (TipoAvaliador t : c.tipos()) {
                int n = porTipo.getOrDefault(t, List.of()).size();
                boolean seAplica = t != TipoAvaliador.SUBORDINADO || equipe;
                if (seAplica && n == 0) {
                    pendPessoa.add(switch (t) {
                        case CHEFE -> nome + " está sem chefe para avaliar. Indique quem vai avaliar.";
                        case SUBORDINADO -> nome + " tem equipe, mas nenhum subordinado foi indicado.";
                        case PAR -> nome + " está sem pares. Indique ao menos um colega.";
                        default -> nome + " está sem a autoavaliação.";
                    });
                } else if (seAplica && n < MINIMO_ANONIMATO
                        && (t == TipoAvaliador.PAR || t == TipoAvaliador.SUBORDINADO)) {
                    alertaPessoa.add(nome + " tem " + n + (t == TipoAvaliador.PAR ? " par(es)" : " subordinado(s)")
                            + ": o resultado desse grupo só aparece com no mínimo 3 respostas.");
                }
            }
            pendencias.addAll(pendPessoa);
            alertas.addAll(alertaPessoa);
            Long qid = c.questionarioDo(id);
            QuestionarioResumo q = qid == null ? null : qs.get(qid);
            avaliados.add(new AvaliadoNoCiclo(id, nome, p == null ? null : p.email(),
                    cargos.getOrDefault(c.getAvaliados().get(id), "—"), p == null ? "—" : p.area(), qid,
                    q == null ? null : q.nome(), equipe, porTipo, pendPessoa, alertaPessoa));
        }
        avaliados.sort(Comparator.comparing(AvaliadoNoCiclo::nome));

        int convitesPendentes = c.isAberto()
                ? (int) ds.stream().filter(d -> d.getConviteEnviadoEm() == null).count() : 0;
        List<TipoAvaliador> tipos = new ArrayList<>(c.tipos());
        return new MontagemCiclo(cicloService.resumo(c), tipos, avaliados, porCargo,
                new Verificacao(pendencias, alertas, semQuestionario, semPerguntas), totalPorTipo, ds.size(),
                porExterno.size(), convitesPendentes);
    }

    /**
     * Passos 12 e 13: agenda o ciclo sem pendências (10b). Com abertura hoje, abre na hora e envia os convites (13a).
     */
    public ResultadoAgendamento agendar(Long cicloId) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        List<String> pendencias = new ArrayList<>();
        errosDosDados(new DadosCiclo(c.getNome(), c.tipos(), c.getDataAbertura(), c.getDataEncerramento()),
                Datas.hoje()).values().forEach(pendencias::add);
        pendencias.addAll(montagem(cicloId).verificacao().pendencias());
        if (!pendencias.isEmpty()) {
            throw new CicloComPendenciasException(pendencias);
        }
        c.agendar();
        eventos.publishEvent(EventoAuditoria.de("CICLO_AGENDADO", CicloService.descricao(c),
                "Abertura em " + Datas.data(c.getDataAbertura()) + " e encerramento em "
                        + Datas.data(c.getDataEncerramento())));
        if (!c.getDataAbertura().isAfter(Datas.hoje())) {
            ConviteService.ResultadoEnvio envio = cicloService.abrir(cicloId, false);
            return new ResultadoAgendamento(true, c.getDataAbertura(), envio);
        }
        return new ResultadoAgendamento(false, c.getDataAbertura(), new ConviteService.ResultadoEnvio(0, 0, 0));
    }

    /** Extensão 12a: fica (ou volta a ser) rascunho, sem convites. Devolve {@code true} se estava agendado. */
    public boolean salvarRascunho(Long cicloId) {
        Ciclo c = cicloService.entidade(cicloId);
        c.exigirEditavel();
        return c.voltarARascunho();
    }

    static String rotuloTipo(TipoAvaliador t) {
        return switch (t) {
            case CHEFE -> "o chefe";
            case AUTO -> "a autoavaliação";
            case PAR -> "os pares";
            case SUBORDINADO -> "os subordinados";
            case EXTERNO -> "os avaliadores externos";
        };
    }

    private static Set<TipoAvaliador> emOrdem(Collection<TipoAvaliador> tipos) {
        Set<TipoAvaliador> s = new LinkedHashSet<>();
        for (TipoAvaliador t : TipoAvaliador.values()) {
            if (tipos.contains(t)) s.add(t);
        }
        return s;
    }
}
