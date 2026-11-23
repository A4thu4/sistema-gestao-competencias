package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.ciclos.servico.DesignacaoResumo;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;
import br.edu.pucgoias.sgc.competencias.servico.QuestionarioCompleto;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tudo o que a tela de responder avaliação mostra (UC-03, passo 4): o questionário do cargo do avaliado só com as
 * perguntas da relação (RF-11), a escala com a descrição dos níveis (RF-25), as respostas já salvas (RF-14), as
 * obrigatórias pendentes (RF-15) e, para o gestor do avaliado, as observações do período (RF-17) e o resultado
 * anterior (RF-18).
 *
 * @param editavel         ciclo aceita respostas e a avaliação não foi enviada
 * @param motivoBloqueio   por que não dá para editar (ciclo encerrado ou avaliação enviada), ou {@code null}
 * @param mostraContexto   relação CHEFE: mostra observações e resultado anterior (a autoavaliação não mostra, 4a)
 * @param observacoes      observações do período (vazia quando {@code mostraContexto} é falso)
 */
public record FormularioAvaliacao(DesignacaoResumo designacao, PessoaResumo avaliado, QuestionarioCompleto questionario,
                                  Map<Long, RespostaInformada> respostas, List<PerguntaResumo> pendentes,
                                  boolean editavel, String motivoBloqueio, boolean mostraContexto,
                                  List<ObservacaoResumo> observacoes, LocalDate inicioObservacoes,
                                  LocalDate fimObservacoes, ResumoResultadoAnterior resultadoAnterior,
                                  OffsetDateTime ultimoSalvamento) {

    public boolean anonima() {
        return Relacoes.anonima(designacao.relacao());
    }

    public String relacaoTexto() {
        return Relacoes.texto(designacao.relacao());
    }

    public boolean autoavaliacao() {
        return designacao.relacao() == TipoAvaliador.AUTO;
    }

    public RespostaInformada resposta(Long perguntaId) {
        return respostas.getOrDefault(perguntaId, RespostaInformada.VAZIA);
    }

    public boolean marcada(Long perguntaId, int valor) {
        Integer nota = resposta(perguntaId).nota();
        return nota != null && nota == valor;
    }

    public boolean naoAvalia(Long perguntaId) {
        return resposta(perguntaId).naoAvalia();
    }

    public String texto(Long perguntaId) {
        String t = resposta(perguntaId).texto();
        return t == null ? "" : t;
    }

    public boolean pendente(Long perguntaId) {
        return pendentesIds().contains(perguntaId);
    }

    public Set<Long> pendentesIds() {
        return pendentes.stream().map(PerguntaResumo::id).collect(Collectors.toSet());
    }

    public int totalPerguntas() {
        return questionario.totalPerguntas();
    }

    public long respondidas() {
        return questionario.perguntas().stream().filter(p -> !resposta(p.id()).vazia()).count();
    }

    public int percentual() {
        int total = totalPerguntas();
        return total == 0 ? 0 : (int) Math.round(100.0 * respondidas() / total);
    }

    public String ultimoSalvamentoTexto() {
        return ultimoSalvamento == null ? null
                : ultimoSalvamento.atZoneSameInstant(Calendario.ZONA).toLocalTime().withSecond(0).withNano(0).toString();
    }

    public String periodoObservacoes() {
        return Calendario.data(inicioObservacoes) + " a " + Calendario.data(fimObservacoes);
    }

    public String encerramentoTexto() {
        return Calendario.data(designacao.dataEncerramento());
    }
}
