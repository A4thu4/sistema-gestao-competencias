package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Regras de exibição dos resultados, iguais na tela, nos gráficos, nos relatórios e nos arquivos exportados.
 *
 * <ul>
 *   <li><b>Mínimo de 3 (RNF-02):</b> a média de pares, subordinados e externos só aparece com pelo menos
 *       {@value #MINIMO_RESPOSTAS} avaliações no grupo. Chefe e autoavaliação não têm mínimo (são identificados por
 *       natureza);</li>
 *   <li><b>Nota final da competência:</b> média das médias dos grupos exibíveis, sem a autoavaliação (a visão da
 *       própria pessoa aparece ao lado, mas não pesa na nota). Sem nenhum grupo exibível além dela, não há nota
 *       final;</li>
 *   <li><b>Média geral:</b> média das notas finais das competências.</li>
 * </ul>
 */
public final class RegrasDeExibicao {

    public static final int MINIMO_RESPOSTAS = 3;

    public static final String AVISO_ANONIMATO = "respostas insuficientes para preservar o anonimato";

    public static final String AJUDA_NOTA_FINAL = "Nota final = média das notas do chefe, dos pares, dos "
            + "subordinados e dos externos (sem a autoavaliação). Médias de pares, subordinados e externos só "
            + "aparecem com pelo menos " + MINIMO_RESPOSTAS + " respostas, para preservar o anonimato.";

    private RegrasDeExibicao() {
    }

    /** Pares, subordinados e externos precisam do mínimo de respostas. */
    public static boolean exigeMinimo(TipoAvaliador grupo) {
        return grupo == TipoAvaliador.PAR || grupo == TipoAvaliador.SUBORDINADO || grupo == TipoAvaliador.EXTERNO;
    }

    public static boolean exibivel(TipoAvaliador grupo, int numRespostas) {
        return !exigeMinimo(grupo) || numRespostas >= MINIMO_RESPOSTAS;
    }

    /** Nota final a partir das médias já exibíveis de cada grupo (a autoavaliação é ignorada). */
    public static BigDecimal notaFinal(Map<TipoAvaliador, BigDecimal> mediasExibiveis) {
        return media(mediasExibiveis.entrySet().stream()
                .filter(e -> e.getKey() != TipoAvaliador.AUTO)
                .map(Map.Entry::getValue).toList());
    }

    /** Média simples com 2 casas (HALF_UP), ignorando vazios; {@code null} quando não há valores. */
    public static BigDecimal media(Collection<BigDecimal> valores) {
        var presentes = valores.stream().filter(Objects::nonNull).toList();
        if (presentes.isEmpty()) {
            return null;
        }
        BigDecimal soma = presentes.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(presentes.size()), CalculoConsolidacao.CASAS, RoundingMode.HALF_UP);
    }

    /** Nota em percentual da escala (para comparar ciclos com escalas diferentes, RF-26). */
    public static BigDecimal percentualDaEscala(BigDecimal nota, int escalaMax) {
        if (nota == null || escalaMax <= 0) {
            return null;
        }
        return nota.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(escalaMax), 1, RoundingMode.HALF_UP);
    }
}
