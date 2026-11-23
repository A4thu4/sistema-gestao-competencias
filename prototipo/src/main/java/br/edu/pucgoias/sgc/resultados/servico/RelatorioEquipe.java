package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Relatório de uma equipe ou área num ciclo (UC-04, 3a; RF-21): nota final de cada pessoa em cada competência e a
 * média da equipe por competência. Só notas finais, que já seguem o mínimo de 3; nenhum respondente aparece.
 *
 * @param competencias competências (id → nome) em ordem alfabética
 * @param mediaEquipe  média das notas finais das pessoas, por competência
 */
public record RelatorioEquipe(CicloResumo ciclo, String titulo, String descricao, Map<Long, String> competencias,
                              List<PessoaNoRelatorio> pessoas, Map<Long, BigDecimal> mediaEquipe,
                              BigDecimal mediaGeralEquipe, int escalaMax) {

    public record PessoaNoRelatorio(PessoaResumo pessoa, Map<Long, BigDecimal> notas, BigDecimal mediaGeral) {

        public String nota(Long competenciaId) {
            return Formatos.nota(notas.get(competenciaId));
        }

        public String mediaGeralTexto() {
            return Formatos.nota(mediaGeral);
        }
    }

    public String mediaEquipe(Long competenciaId) {
        return Formatos.nota(mediaEquipe.get(competenciaId));
    }

    public String mediaGeralEquipeTexto() {
        return Formatos.nota(mediaGeralEquipe);
    }

    public boolean vazio() {
        return pessoas.isEmpty();
    }
}
