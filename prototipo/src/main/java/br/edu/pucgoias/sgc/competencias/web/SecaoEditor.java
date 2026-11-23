package br.edu.pucgoias.sgc.competencias.web;

import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.PerguntaResumo;

import java.util.List;

/** Bloco de perguntas de uma competência no editor. {@code indice} é a posição da linha no formulário. */
public record SecaoEditor(CompetenciaResumo competencia, boolean selecionada, List<Linha> linhas) {

    public record Linha(int indice, PerguntaResumo pergunta) {
    }
}
