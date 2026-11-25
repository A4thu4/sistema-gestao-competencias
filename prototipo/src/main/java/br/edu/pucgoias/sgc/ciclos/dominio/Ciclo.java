package br.edu.pucgoias.sgc.ciclos.dominio;

import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Ciclo de avaliação. Guarda também quem é avaliado, com o cargo da pessoa no momento da escolha, e o questionário
 * publicado usado para cada cargo.
 *
 * <p>Transições: rascunho ⇄ agendado → aberto → encerrado. Abrir ou encerrar antes da hora não muda as datas (há um
 * {@code check} no banco), só a situação e {@code abertoEm}/{@code encerradoEm}.
 */
@Entity
@Table(name = "ciclo")
public class Ciclo {

    public static final int NOME_MAXIMO = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private boolean tipoChefe;
    private boolean tipoAuto;
    private boolean tipoPares;
    private boolean tipoSubordinados;
    private LocalDate dataAbertura;
    private LocalDate dataEncerramento;

    @Enumerated(EnumType.STRING)
    private SituacaoCiclo situacao = SituacaoCiclo.RASCUNHO;

    private boolean resultadoLiberado;
    private OffsetDateTime liberadoEm;
    private OffsetDateTime abertoEm;
    private OffsetDateTime encerradoEm;
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    /** Pessoa avaliada → cargo dela quando foi escolhida (o cargo decide o questionário). */
    @ElementCollection
    @CollectionTable(name = "ciclo_avaliado", joinColumns = @JoinColumn(name = "ciclo_id"))
    @MapKeyColumn(name = "avaliado_id")
    @Column(name = "cargo_id")
    private Map<Long, Long> avaliados = new HashMap<>();

    /** Cargo → questionário publicado usado no ciclo. */
    @ElementCollection
    @CollectionTable(name = "ciclo_questionario", joinColumns = @JoinColumn(name = "ciclo_id"))
    @MapKeyColumn(name = "cargo_id")
    @Column(name = "questionario_id")
    private Map<Long, Long> questionarioPorCargo = new HashMap<>();

    protected Ciclo() {
    }

    public Ciclo(String nome, Set<TipoAvaliador> tipos, LocalDate abertura, LocalDate encerramento) {
        definir(nome, tipos, abertura, encerramento);
    }

    public void alterarDados(String nome, Set<TipoAvaliador> tipos, LocalDate abertura, LocalDate encerramento) {
        exigirEditavel();
        definir(nome, tipos, abertura, encerramento);
    }

    private void definir(String nome, Set<TipoAvaliador> tipos, LocalDate abertura, LocalDate encerramento) {
        this.nome = nome.trim();
        this.tipoChefe = tipos.contains(TipoAvaliador.CHEFE);
        this.tipoAuto = tipos.contains(TipoAvaliador.AUTO);
        this.tipoPares = tipos.contains(TipoAvaliador.PAR);
        this.tipoSubordinados = tipos.contains(TipoAvaliador.SUBORDINADO);
        this.dataAbertura = abertura;
        this.dataEncerramento = encerramento;
    }

    /** Tipos de avaliação marcados no ciclo (sem o externo, que não é um tipo do RF-08). */
    public Set<TipoAvaliador> tipos() {
        EnumSet<TipoAvaliador> s = EnumSet.noneOf(TipoAvaliador.class);
        if (tipoChefe) s.add(TipoAvaliador.CHEFE);
        if (tipoAuto) s.add(TipoAvaliador.AUTO);
        if (tipoPares) s.add(TipoAvaliador.PAR);
        if (tipoSubordinados) s.add(TipoAvaliador.SUBORDINADO);
        return s;
    }

    public void retirarTipo(TipoAvaliador tipo) {
        exigirEditavel();
        Set<TipoAvaliador> s = tipos();
        s.remove(tipo);
        if (s.isEmpty()) {
            throw new RegraDeNegocioException("O ciclo precisa de ao menos um tipo de avaliação.");
        }
        definir(nome, s, dataAbertura, dataEncerramento);
    }

    /** Rascunho e agendado podem ser alterados à vontade. */
    public boolean isEditavel() {
        return situacao == SituacaoCiclo.RASCUNHO || situacao == SituacaoCiclo.AGENDADO;
    }

    public boolean isAberto() {
        return situacao == SituacaoCiclo.ABERTO;
    }

    public boolean isEncerrado() {
        return situacao == SituacaoCiclo.ENCERRADO;
    }

    public void exigirEditavel() {
        if (!isEditavel()) {
            throw new RegraDeNegocioException(isAberto()
                    ? "O ciclo já está aberto: só é possível mudar a data de encerramento e incluir avaliadores."
                    : "O ciclo está encerrado e fica disponível só para consulta.");
        }
    }

    /** Alteração em ciclo agendado o devolve a rascunho, para ser conferido e agendado de novo. */
    public boolean voltarARascunho() {
        if (situacao == SituacaoCiclo.AGENDADO) {
            situacao = SituacaoCiclo.RASCUNHO;
            return true;
        }
        return false;
    }

    public void agendar() {
        exigirEditavel();
        situacao = SituacaoCiclo.AGENDADO;
    }

    public void abrir() {
        if (situacao != SituacaoCiclo.AGENDADO) {
            throw new RegraDeNegocioException("Só um ciclo agendado pode ser aberto.");
        }
        situacao = SituacaoCiclo.ABERTO;
        abertoEm = OffsetDateTime.now();
    }

    public void encerrar() {
        if (situacao != SituacaoCiclo.ABERTO) {
            throw new RegraDeNegocioException("Só um ciclo aberto pode ser encerrado.");
        }
        situacao = SituacaoCiclo.ENCERRADO;
        encerradoEm = OffsetDateTime.now();
    }

    /** Em ciclo aberto, só o encerramento pode mudar. */
    public void alterarEncerramento(LocalDate novaData) {
        if (situacao != SituacaoCiclo.ABERTO) {
            throw new RegraDeNegocioException("A data de encerramento só é ajustada assim em ciclo aberto.");
        }
        this.dataEncerramento = novaData;
    }

    public void liberarResultado() {
        if (!isEncerrado()) {
            throw new RegraDeNegocioException("O resultado só pode ser liberado depois que o ciclo for encerrado.");
        }
        if (resultadoLiberado) {
            throw new RegraDeNegocioException("O resultado deste ciclo já foi liberado.");
        }
        resultadoLiberado = true;
        liberadoEm = OffsetDateTime.now();
    }

    /** Usado só pela carga de exemplo, para montar ciclos no passado. */
    public void forcarSituacao(SituacaoCiclo nova, OffsetDateTime abertoEm, OffsetDateTime encerradoEm) {
        this.situacao = nova;
        this.abertoEm = abertoEm;
        this.encerradoEm = encerradoEm;
    }

    // ==== avaliados e questionários ====

    public Map<Long, Long> getAvaliados() {
        return Collections.unmodifiableMap(avaliados);
    }

    public void incluirAvaliado(Long avaliadoId, Long cargoId) {
        avaliados.putIfAbsent(avaliadoId, cargoId);
    }

    public void removerAvaliado(Long avaliadoId) {
        avaliados.remove(avaliadoId);
    }

    public boolean avalia(Long pessoaId) {
        return avaliados.containsKey(pessoaId);
    }

    public Map<Long, Long> getQuestionarioPorCargo() {
        return Collections.unmodifiableMap(questionarioPorCargo);
    }

    public void definirQuestionario(Long cargoId, Long questionarioId) {
        if (questionarioId == null) {
            questionarioPorCargo.remove(cargoId);
        } else {
            questionarioPorCargo.put(cargoId, questionarioId);
        }
    }

    /** Questionário de quem é avaliado no ciclo (pelo cargo gravado na escolha), ou {@code null}. */
    public Long questionarioDo(Long avaliadoId) {
        Long cargo = avaliados.get(avaliadoId);
        return cargo == null ? null : questionarioPorCargo.get(cargo);
    }

    public void limparQuestionariosSemAvaliados() {
        questionarioPorCargo.keySet().retainAll(avaliados.values());
    }

    // ==== getters ====

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public LocalDate getDataEncerramento() { return dataEncerramento; }
    public SituacaoCiclo getSituacao() { return situacao; }
    public boolean isResultadoLiberado() { return resultadoLiberado; }
    public OffsetDateTime getLiberadoEm() { return liberadoEm; }
    public OffsetDateTime getAbertoEm() { return abertoEm; }
    public OffsetDateTime getEncerradoEm() { return encerradoEm; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
}
