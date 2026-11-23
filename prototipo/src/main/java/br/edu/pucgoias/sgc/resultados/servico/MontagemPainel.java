package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Monta as linhas do painel a partir do consolidado de uma pessoa num ciclo, aplicando as
 * {@link RegrasDeExibicao}. Sem banco, para ser testada por unidade.
 */
public final class MontagemPainel {

    private static final Collator COLLATOR = Collator.getInstance(Locale.of("pt", "BR"));

    private MontagemPainel() {
    }

    /**
     * Resultado montado: linhas por competência (em ordem alfabética), média geral, ponto forte, foco de
     * desenvolvimento e os grupos que ficaram de fora em alguma competência por falta de respostas.
     */
    public record Montagem(List<LinhaCompetencia> linhas, BigDecimal mediaGeral, String pontoForte, String foco,
                           Set<TipoAvaliador> gruposInsuficientes, int escalaMax) {

        /** Nota final de cada competência (para evolução, relatórios e plano). */
        public Map<Long, BigDecimal> notasFinais() {
            Map<Long, BigDecimal> m = new LinkedHashMap<>();
            linhas.forEach(l -> m.put(l.competenciaId(), l.notaFinal()));
            return m;
        }
    }

    public static Montagem montar(Collection<LinhaConsolidada> consolidado, Map<Long, String> nomesCompetencias) {
        Map<Long, List<LinhaConsolidada>> porCompetencia = consolidado.stream()
                .collect(Collectors.groupingBy(LinhaConsolidada::competenciaId));
        Set<TipoAvaliador> insuficientes = new TreeSet<>();
        List<LinhaCompetencia> provisorias = new ArrayList<>();
        for (var e : porCompetencia.entrySet()) {
            Map<TipoAvaliador, NotaGrupo> grupos = new EnumMap<>(TipoAvaliador.class);
            Map<TipoAvaliador, BigDecimal> exibiveis = new EnumMap<>(TipoAvaliador.class);
            for (LinhaConsolidada l : e.getValue()) {
                boolean ok = RegrasDeExibicao.exibivel(l.grupo(), l.numRespostas()) && l.media() != null;
                if (ok) {
                    grupos.put(l.grupo(), new NotaGrupo(l.grupo(), l.media(), false));
                    exibiveis.put(l.grupo(), l.media());
                } else if (!RegrasDeExibicao.exibivel(l.grupo(), l.numRespostas())) {
                    grupos.put(l.grupo(), new NotaGrupo(l.grupo(), null, true));
                    insuficientes.add(l.grupo());
                }
            }
            String nome = nomesCompetencias.getOrDefault(e.getKey(), "Competência " + e.getKey());
            provisorias.add(new LinhaCompetencia(e.getKey(), nome, grupos, RegrasDeExibicao.notaFinal(exibiveis),
                    false, false));
        }
        provisorias.sort(Comparator.comparing(LinhaCompetencia::competencia, COLLATOR));

        LinhaCompetencia forte = provisorias.stream().filter(l -> l.notaFinal() != null)
                .max(Comparator.comparing(LinhaCompetencia::notaFinal)).orElse(null);
        LinhaCompetencia fraca = provisorias.stream().filter(l -> l.notaFinal() != null)
                .min(Comparator.comparing(LinhaCompetencia::notaFinal)).orElse(null);
        if (forte != null && fraca != null && forte.notaFinal().compareTo(fraca.notaFinal()) == 0) {
            fraca = null; // todas iguais: não há um foco de desenvolvimento destacado
        }
        List<LinhaCompetencia> linhas = new ArrayList<>();
        for (LinhaCompetencia l : provisorias) {
            linhas.add(new LinhaCompetencia(l.competenciaId(), l.competencia(), l.grupos(), l.notaFinal(),
                    l == forte, l == fraca));
        }
        BigDecimal mediaGeral = RegrasDeExibicao.media(linhas.stream().map(LinhaCompetencia::notaFinal).toList());
        int escala = consolidado.stream().mapToInt(LinhaConsolidada::escalaMax).max().orElse(0);
        return new Montagem(List.copyOf(linhas), mediaGeral, forte == null ? null : forte.competencia(),
                fraca == null ? null : fraca.competencia(), insuficientes, escala);
    }

    /** Nomes de competências em ordem alfabética do português. */
    public static Comparator<String> ordemAlfabetica() {
        return (a, b) -> COLLATOR.compare(Objects.toString(a, ""), Objects.toString(b, ""));
    }
}
