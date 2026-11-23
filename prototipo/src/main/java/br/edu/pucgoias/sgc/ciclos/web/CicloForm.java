package br.edu.pucgoias.sgc.ciclos.web;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.DadosCiclo;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Formulário dos passos 4 e 5 do UC-02 (nome, tipos e período). */
public class CicloForm {

    private String nome;
    private Set<TipoAvaliador> tipos = new LinkedHashSet<>();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate abertura;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate encerramento;

    public static CicloForm novo() {
        CicloForm f = new CicloForm();
        f.tipos = new LinkedHashSet<>(EnumSet.of(TipoAvaliador.CHEFE, TipoAvaliador.AUTO));
        return f;
    }

    public static CicloForm de(CicloResumo c) {
        CicloForm f = new CicloForm();
        f.nome = c.nome();
        f.tipos = new LinkedHashSet<>(c.tiposAtivos());
        f.tipos.remove(TipoAvaliador.EXTERNO);
        f.abertura = c.dataAbertura();
        f.encerramento = c.dataEncerramento();
        return f;
    }

    public DadosCiclo paraDados() {
        return new DadosCiclo(nome, tipos, abertura, encerramento);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Set<TipoAvaliador> getTipos() { return tipos; }
    public void setTipos(Set<TipoAvaliador> tipos) { this.tipos = tipos == null ? new LinkedHashSet<>() : tipos; }
    public LocalDate getAbertura() { return abertura; }
    public void setAbertura(LocalDate abertura) { this.abertura = abertura; }
    public LocalDate getEncerramento() { return encerramento; }
    public void setEncerramento(LocalDate encerramento) { this.encerramento = encerramento; }
}
