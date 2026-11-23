package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Painel individual (UC-04, passos 4 a 6; TL-04). Já vem com as regras de exibição aplicadas: nenhuma média de
 * grupo com menos de 3 respostas e nenhuma autoria (RNF-01, RNF-02). A tela e os arquivos exportados usam este
 * mesmo objeto (RF-23).
 *
 * @param grupos colunas da tabela (grupos do ciclo, na ordem da TL-04)
 * @param gruposInsuficientes grupos que ficaram sem média em alguma competência
 */
public record PainelIndividual(PessoaResumo pessoa, CicloResumo ciclo, int escalaMax, List<TipoAvaliador> grupos,
                               List<LinhaCompetencia> linhas, BigDecimal mediaGeral, String pontoForte, String foco,
                               List<TipoAvaliador> gruposInsuficientes, DadosRadar radar,
                               List<ComentarioPainel> comentarios, Evolucao evolucao) {

    public String mediaGeralTexto() {
        return Formatos.nota(mediaGeral);
    }

    public String escalaTexto() {
        return escalaMax + ",0";
    }

    public String rotulo(TipoAvaliador grupo) {
        return Grupos.rotulo(grupo);
    }

    public String avisoInsuficientes() {
        if (gruposInsuficientes.isEmpty()) {
            return null;
        }
        return String.join(", ", gruposInsuficientes.stream().map(Grupos::rotulo).toList())
                + ": " + RegrasDeExibicao.AVISO_ANONIMATO + " (menos de " + RegrasDeExibicao.MINIMO_RESPOSTAS
                + " respostas). A média desse grupo não aparece e não entra na nota final.";
    }

    public String ajudaNotaFinal() {
        return RegrasDeExibicao.AJUDA_NOTA_FINAL;
    }
}
