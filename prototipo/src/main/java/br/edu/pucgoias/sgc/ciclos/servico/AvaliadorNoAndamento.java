package br.edu.pucgoias.sgc.ciclos.servico;

import java.util.List;

/** Linha do painel de andamento (RF-13): um avaliador com as avaliações pendentes e enviadas. */
public record AvaliadorNoAndamento(Long pessoaId, String nome, String email, boolean externo, List<ItemAndamento> itens,
                                   String ultimoLembrete) {

    public long enviadas() {
        return itens.stream().filter(ItemAndamento::enviada).count();
    }

    public long pendentes() {
        return itens.size() - enviadas();
    }

    public boolean concluiu() {
        return pendentes() == 0;
    }

    public String iniciais() {
        String[] partes = nome.trim().split("\\s+");
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (partes[0].substring(0, 1) + ultima).toUpperCase();
    }
}
