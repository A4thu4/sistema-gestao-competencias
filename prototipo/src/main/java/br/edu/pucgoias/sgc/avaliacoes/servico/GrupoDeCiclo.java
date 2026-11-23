package br.edu.pucgoias.sgc.avaliacoes.servico;

import java.time.LocalDate;
import java.util.List;

/** Avaliações do avaliador num ciclo (UC-03, passo 2). {@code aceitaRespostas}: aberto e dentro do prazo. */
public record GrupoDeCiclo(Long cicloId, String cicloNome, LocalDate encerramento, boolean aceitaRespostas,
                           List<AvaliacaoNaLista> avaliacoes) {

    public long pendentes() {
        return avaliacoes.stream().filter(a -> !a.designacao().enviada()).count();
    }

    public long enviadas() {
        return avaliacoes.size() - pendentes();
    }

    public String encerramentoTexto() {
        return Calendario.data(encerramento);
    }
}
