package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.privacidade.servico.FonteDadosPessoais;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultados consolidados na cópia dos dados pessoais (UC-05, 7a): ciclo, competência, nota final e as médias dos
 * grupos exibíveis. Só ciclos já liberados e só médias agregadas que seguem o mínimo de 3 (RNF-01, RNF-02).
 */
@Component
@Order(30)
public class FonteResultados implements FonteDadosPessoais {

    private final ResultadoService resultados;

    public FonteResultados(ResultadoService resultados) {
        this.resultados = resultados;
    }

    @Override
    public String titulo() {
        return "Resultados das avaliações (médias, sem identificar quem respondeu)";
    }

    @Override
    public List<String> itens(Long usuarioId) {
        List<String> itens = new ArrayList<>();
        for (CicloResumo c : resultados.ciclosLiberadosDe(usuarioId)) {
            MontagemPainel.Montagem m = resultados.montagem(c.id(), usuarioId);
            for (LinhaCompetencia l : m.linhas()) {
                StringBuilder s = new StringBuilder(c.nome()).append(" · ").append(l.competencia())
                        .append(" · nota final ").append(l.notaFinalTexto())
                        .append(" (escala de 1 a ").append(m.escalaMax()).append(")");
                for (TipoAvaliador g : Grupos.ORDEM) {
                    NotaGrupo n = l.grupo(g);
                    if (n.media() != null) {
                        s.append(" · ").append(Grupos.rotulo(g).toLowerCase()).append(" ").append(n.texto());
                    }
                }
                itens.add(s.toString());
            }
        }
        return itens;
    }
}
