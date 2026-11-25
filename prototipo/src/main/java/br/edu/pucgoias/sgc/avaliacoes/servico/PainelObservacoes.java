package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.identidade.servico.CartaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.ContribuicaoPainel;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cartão "Observações da equipe" na página inicial do gestor (UC-08): total registrado no período recente e quem
 * está sem observação há mais de {@value ObservacaoService#DIAS_SEM_OBSERVACAO} dias.
 */
@Component
public class PainelObservacoes implements ContribuicaoPainel {

    private final ObservacaoService observacoes;

    public PainelObservacoes(ObservacaoService observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public List<CartaoPainel> cartoes(UsuarioAutenticado eu) {
        if (!eu.ehGestor()) {
            return List.of();
        }
        List<MembroEquipe> equipe = observacoes.equipe(eu);
        if (equipe.isEmpty()) {
            return List.of();
        }
        long recentes = observacoes.registradasDesde(eu,
                Calendario.hoje().minusDays(ObservacaoService.DIAS_PERIODO_RECENTE));
        List<String> semObservacao = equipe.stream().filter(MembroEquipe::semObservacaoRecente)
                .map(m -> primeiroNome(m.pessoa().nome())).toList();
        String descricao = semObservacao.isEmpty()
                ? "nos últimos " + ObservacaoService.DIAS_PERIODO_RECENTE + " dias"
                : "sem observação há mais de " + ObservacaoService.DIAS_SEM_OBSERVACAO + " dias: "
                        + juntar(semObservacao);
        return List.of(new CartaoPainel(30, "Observações da equipe", String.valueOf(recentes), descricao,
                "/observacoes", semObservacao.isEmpty() ? "primary" : "warning"));
    }

    private static String primeiroNome(String nome) {
        return nome.trim().split("\\s+")[0];
    }

    private static String juntar(List<String> nomes) {
        if (nomes.size() == 1) {
            return nomes.get(0);
        }
        return String.join(", ", nomes.subList(0, nomes.size() - 1)) + " e " + nomes.get(nomes.size() - 1);
    }
}
