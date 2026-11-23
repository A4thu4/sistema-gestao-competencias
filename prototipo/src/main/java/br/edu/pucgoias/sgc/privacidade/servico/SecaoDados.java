package br.edu.pucgoias.sgc.privacidade.servico;

import java.util.List;

/** Grupo de dados sobre o titular guardado por outro pacote (ver {@link FonteDadosPessoais}). */
public record SecaoDados(String titulo, List<String> itens) {
}
