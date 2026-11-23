package br.edu.pucgoias.sgc.competencias.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Questionário de um cargo (RF-03): competências em ordem, perguntas de cada uma e escala com a descrição de cada
 * nível (RF-04). Os modelos prontos (RF-07) são questionários com {@code modelo = true} e sem cargo.
 *
 * <p>A regra "nenhum questionário incompleto é publicado" (UC-01, garantia mínima) fica em {@link #pendencias}.
 */
@Entity
@Table(name = "questionario")
public class Questionario {

    public static final int NOME_MAXIMO = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Long cargoId;
    private short escalaMax;

    @Enumerated(EnumType.STRING)
    private SituacaoQuestionario situacao = SituacaoQuestionario.RASCUNHO;

    private boolean modelo;
    private Long origemId;
    private OffsetDateTime criadoEm = OffsetDateTime.now();
    private OffsetDateTime publicadoEm;

    @ElementCollection
    @CollectionTable(name = "questionario_competencia", joinColumns = @JoinColumn(name = "questionario_id"))
    @OrderBy("ordem")
    private List<CompetenciaNoQuestionario> competencias = new ArrayList<>();

    @OneToMany(mappedBy = "questionario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("competenciaId, ordem")
    private List<Pergunta> perguntas = new ArrayList<>();

    @OneToMany(mappedBy = "questionario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("valor")
    private List<NivelEscala> niveis = new ArrayList<>();

    protected Questionario() {
    }

    public Questionario(String nome, Long cargoId, int escalaMax, boolean modelo) {
        this.nome = cortar(nome);
        this.cargoId = cargoId;
        this.modelo = modelo;
        definirEscala(escalaMax, List.of());
    }

    /** Cópia com a estrutura completa (competências, perguntas e escala), sem respostas, como rascunho (RF-07). */
    public Questionario copiar(String novoNome) {
        Questionario c = new Questionario(novoNome, modelo ? null : cargoId, escalaMax, false);
        c.origemId = id;
        c.niveis.clear();
        for (CompetenciaNoQuestionario item : competencias) {
            c.competencias.add(new CompetenciaNoQuestionario(item.getCompetenciaId(), item.getOrdem()));
        }
        for (Pergunta p : perguntas) {
            c.perguntas.add(p.copiarPara(c));
        }
        for (NivelEscala n : niveis) {
            c.niveis.add(new NivelEscala(c, n.getValor(), n.getDescricao()));
        }
        return c;
    }

    public void alterarDados(String nome, Long cargoId) {
        this.nome = cortar(nome);
        this.cargoId = cargoId;
    }

    /** Competências na ordem informada. */
    public void definirCompetencias(List<Long> competenciaIds) {
        competencias.clear();
        int ordem = 1;
        for (Long cid : competenciaIds.stream().filter(Objects::nonNull).distinct().toList()) {
            competencias.add(new CompetenciaNoQuestionario(cid, ordem++));
        }
    }

    public void incluirCompetencia(Long competenciaId) {
        if (!competenciaIds().contains(competenciaId)) {
            competencias.add(new CompetenciaNoQuestionario(competenciaId, competencias.size() + 1));
        }
    }

    /**
     * Escala de 1 a 5 ou de 1 a 10 (RF-04). Os níveis são alterados no lugar (e não apagados e recriados) por causa
     * da regra "um valor por questionário" do banco.
     */
    public void definirEscala(int maximo, List<String> descricoes) {
        if (maximo != 5 && maximo != 10) {
            throw new IllegalArgumentException("A escala deve ser de 1 a 5 ou de 1 a 10.");
        }
        this.escalaMax = (short) maximo;
        niveis.removeIf(n -> n.getValor() > maximo);
        for (int valor = 1; valor <= maximo; valor++) {
            String descricao = valor - 1 < descricoes.size() && descricoes.get(valor - 1) != null
                    ? descricoes.get(valor - 1).trim() : null;
            Optional<NivelEscala> existente = nivel(valor);
            if (existente.isPresent()) {
                if (descricao != null) {
                    existente.get().descrever(descricao);
                }
            } else {
                niveis.add(new NivelEscala(this, valor, descricao == null ? "" : descricao));
            }
        }
    }

    public Pergunta novaPergunta(Long competenciaId) {
        Pergunta p = new Pergunta(this, competenciaId);
        perguntas.add(p);
        return p;
    }

    public Optional<Pergunta> pergunta(Long perguntaId) {
        return perguntas.stream().filter(p -> Objects.equals(p.getId(), perguntaId)).findFirst();
    }

    /** Remove as perguntas que não estão na coleção informada. */
    public void manterSomente(Collection<Pergunta> manter) {
        perguntas.removeIf(p -> manter.stream().noneMatch(m -> m == p));
    }

    /**
     * O que falta para publicar (UC-01, passo 12 e extensão 12a), em linguagem simples. Lista vazia = pode publicar.
     *
     * @param competencia busca a competência do catálogo pelo id
     */
    public List<String> pendencias(Function<Long, Competencia> competencia) {
        List<String> faltas = new ArrayList<>();
        if (nome == null || nome.isBlank()) {
            faltas.add("Dê um nome ao questionário.");
        }
        if (!modelo && cargoId == null) {
            faltas.add("Escolha o cargo a que o questionário se destina.");
        }
        if (competencias.isEmpty()) {
            faltas.add("Escolha ao menos uma competência.");
        }
        for (Long cid : competenciaIds()) {
            Competencia c = competencia.apply(cid);
            String nomeC = c == null ? "sem nome" : c.getNome();
            if (c != null && !c.isAtiva()) {
                faltas.add("A competência “" + nomeC + "” foi desativada no catálogo. Tire-a do questionário ou reative-a.");
            }
            List<Pergunta> daCompetencia = perguntasDa(cid);
            if (daCompetencia.isEmpty()) {
                faltas.add("A competência “" + nomeC + "” ainda não tem nenhuma pergunta.");
            }
            for (Pergunta p : daCompetencia) {
                if (!p.seAplicaAAlguem()) {
                    faltas.add("Uma pergunta de “" + nomeC + "” não está marcada para nenhum tipo de avaliador.");
                }
            }
        }
        for (int valor = 1; valor <= escalaMax; valor++) {
            Optional<NivelEscala> n = nivel(valor);
            if (n.isEmpty() || n.get().getDescricao() == null || n.get().getDescricao().isBlank()) {
                faltas.add("Descreva o nível " + valor + " da escala.");
            }
        }
        return faltas;
    }

    public void publicar() {
        this.situacao = SituacaoQuestionario.PUBLICADO;
        this.publicadoEm = OffsetDateTime.now();
    }

    public void voltarARascunho() {
        this.situacao = SituacaoQuestionario.RASCUNHO;
        this.publicadoEm = null;
    }

    public List<Long> competenciaIds() {
        return competencias.stream().map(CompetenciaNoQuestionario::getCompetenciaId).toList();
    }

    public List<Pergunta> perguntasDa(Long competenciaId) {
        return perguntas.stream().filter(p -> Objects.equals(p.getCompetenciaId(), competenciaId))
                .sorted((a, b) -> Integer.compare(a.getOrdem(), b.getOrdem())).toList();
    }

    private Optional<NivelEscala> nivel(int valor) {
        return niveis.stream().filter(n -> n.getValor() == valor).findFirst();
    }

    private static String cortar(String texto) {
        String t = texto == null ? "" : texto.trim();
        return t.length() <= NOME_MAXIMO ? t : t.substring(0, NOME_MAXIMO);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Long getCargoId() { return cargoId; }
    public int getEscalaMax() { return escalaMax; }
    public SituacaoQuestionario getSituacao() { return situacao; }
    public boolean isPublicado() { return situacao == SituacaoQuestionario.PUBLICADO; }
    public boolean isModelo() { return modelo; }
    public Long getOrigemId() { return origemId; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getPublicadoEm() { return publicadoEm; }
    public List<Pergunta> getPerguntas() { return List.copyOf(perguntas); }
    public List<NivelEscala> getNiveis() {
        return niveis.stream().sorted((a, b) -> Integer.compare(a.getValor(), b.getValor())).toList();
    }
}
