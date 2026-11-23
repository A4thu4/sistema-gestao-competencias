package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Ciclo para os outros pacotes. {@code situacao} é "RASCUNHO", "AGENDADO", "ABERTO" ou "ENCERRADO".
 * {@code tiposAtivos} traz os tipos marcados no ciclo (RF-08) e também {@code EXTERNO} quando há avaliador externo.
 */
public record CicloResumo(Long id, String nome, String situacao, LocalDate dataAbertura, LocalDate dataEncerramento,
                          boolean resultadoLiberado, Set<TipoAvaliador> tiposAtivos) {

    public boolean aberto() {
        return "ABERTO".equals(situacao);
    }

    public boolean encerrado() {
        return "ENCERRADO".equals(situacao);
    }

    public boolean rascunho() {
        return "RASCUNHO".equals(situacao);
    }

    public boolean agendado() {
        return "AGENDADO".equals(situacao);
    }

    /** Rascunho e agendado podem ser alterados (UC-02, 2a). */
    public boolean editavel() {
        return rascunho() || agendado();
    }

    public String situacaoRotulo() {
        return switch (situacao) {
            case "RASCUNHO" -> "Rascunho";
            case "AGENDADO" -> "Agendado";
            case "ABERTO" -> "Aberto";
            default -> "Encerrado";
        };
    }

    /** Tipos na ordem da tela (chefe, autoavaliação, pares, subordinados, externo). */
    public List<TipoAvaliador> tiposEmOrdem() {
        return Arrays.stream(TipoAvaliador.values()).filter(tiposAtivos::contains).toList();
    }

    public String periodo() {
        return Datas.data(dataAbertura) + " a " + Datas.data(dataEncerramento);
    }
}
