package br.edu.pucgoias.sgc.competencias.web;

import br.edu.pucgoias.sgc.competencias.servico.DadosQuestionario;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Formulário do editor de questionário (UC-01, passos 7 a 11), com a competência nova da extensão 9a. */
public class QuestionarioForm {

    private String nome;
    private Long cargoId;
    private int escalaMax = 5;
    private List<Long> competencias = new ArrayList<>();
    private List<PerguntaForm> perguntas = new ArrayList<>();
    private List<String> niveis = new ArrayList<>();
    private boolean padraoDoCargo;
    private String novaNome;
    private String novaDescricao;
    private String novaTipo = "COMPORTAMENTAL";

    /**
     * Converte para o serviço. As competências que já estavam no questionário mantêm a ordem que tinham; as recém
     * marcadas vão para o fim, na ordem da tela.
     */
    DadosQuestionario paraDados(List<Long> ordemAtual) {
        List<Long> marcadas = competencias.stream().filter(Objects::nonNull).distinct().toList();
        List<Long> ordenadas = marcadas.stream()
                .sorted(Comparator.comparingInt(id -> ordemAtual.contains(id)
                        ? ordemAtual.indexOf(id) : ordemAtual.size() + marcadas.indexOf(id)))
                .toList();
        return new DadosQuestionario(nome, cargoId, escalaMax, ordenadas,
                perguntas.stream().filter(Objects::nonNull).map(PerguntaForm::paraDados).toList(),
                niveis, padraoDoCargo);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getCargoId() { return cargoId; }
    public void setCargoId(Long cargoId) { this.cargoId = cargoId; }
    public int getEscalaMax() { return escalaMax; }
    public void setEscalaMax(int escalaMax) { this.escalaMax = escalaMax; }
    public List<Long> getCompetencias() { return competencias; }
    public void setCompetencias(List<Long> competencias) { this.competencias = competencias; }
    public List<PerguntaForm> getPerguntas() { return perguntas; }
    public void setPerguntas(List<PerguntaForm> perguntas) { this.perguntas = perguntas; }
    public List<String> getNiveis() { return niveis; }
    public void setNiveis(List<String> niveis) { this.niveis = niveis; }
    public boolean isPadraoDoCargo() { return padraoDoCargo; }
    public void setPadraoDoCargo(boolean padraoDoCargo) { this.padraoDoCargo = padraoDoCargo; }
    public String getNovaNome() { return novaNome; }
    public void setNovaNome(String novaNome) { this.novaNome = novaNome; }
    public String getNovaDescricao() { return novaDescricao; }
    public void setNovaDescricao(String novaDescricao) { this.novaDescricao = novaDescricao; }
    public String getNovaTipo() { return novaTipo; }
    public void setNovaTipo(String novaTipo) { this.novaTipo = novaTipo; }
}
