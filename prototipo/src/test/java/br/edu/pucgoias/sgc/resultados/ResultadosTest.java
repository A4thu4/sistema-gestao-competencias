package br.edu.pucgoias.sgc.resultados;

import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.resultados.servico.ArquivoExportado;
import br.edu.pucgoias.sgc.resultados.servico.FonteResultados;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.CHEFE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-04 Consultar resultados: painel, mínimo de 3 (RNF-02), visibilidade por perfil (RNF-03), liberação (2a, 2b),
 * ciclo não encerrado (2c), relatório de equipe (3a), exportação (RF-23) e auditoria (RNF-05).
 */
class ResultadosTest extends CenarioResultados {

    @Autowired
    private CicloService ciclos;
    @Autowired
    private FonteResultados fonteResultados;

    private String painel(Long cicloId, Long pessoaId) {
        return "/resultados/" + cicloId + "/pessoas/" + pessoaId;
    }

    @Test
    void colaboradorVeOProprioPainelComCartoesTabelaEGrupoPequenoEscondido() throws Exception {
        mvc.perform(get(painel(ciclo, carla.id())).with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Resultado da Avaliação")))
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("Atendimento ao cliente")))
                .andExpect(content().string(containsString("4,6")))   // nota final do ponto forte
                .andExpect(content().string(containsString("4,0")))   // média geral
                .andExpect(content().string(containsString("nota-maior")))
                .andExpect(content().string(containsString("nota-menor")))
                .andExpect(content().string(containsString("sem a autoavaliação")))
                .andExpect(content().string(containsString("respostas insuficientes para preservar o anonimato")))
                .andExpect(content().string(containsString("graficoRadar")))
                // 6a: só um ciclo com resultado
                .andExpect(content().string(containsString("Ainda não há histórico para comparar")))
                // RNF-02: a média de pares com 2 respostas não aparece nem na tabela nem no gráfico
                .andExpect(content().string(not(containsString(PAR_OCULTO))))
                .andExpect(content().string(not(containsString(PAR_OCULTO_TELA))));
        assertThat(acoesAuditadas()).anyMatch(a -> a.startsWith("RESULTADO_ACESSADO") && a.contains("Carla Dias"));
    }

    @Test
    void externoComUmaRespostaNaoApareceParaORh() throws Exception {
        mvc.perform(get(painel(ciclo, diego.id())).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Diego Rocha")))
                .andExpect(content().string(containsString("Externos")))
                .andExpect(content().string(not(containsString(EXTERNO_OCULTO))))
                .andExpect(content().string(not(containsString("1,9"))));
    }

    @Test
    void colaboradorSemLiberacaoVeAvisoESemDados() throws Exception {
        liberado(false);
        mvc.perform(get(painel(ciclo, carla.id())).with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ainda não liberou")))
                .andExpect(content().string(containsString("aviso por e-mail")))
                .andExpect(content().string(not(containsString("Atendimento ao cliente"))));
        mvc.perform(get("/resultados").with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ciclo 2026.1")))
                .andExpect(content().string(containsString("Aguardando liberação pelo RH")))
                .andExpect(content().string(not(containsString("Meu resultado"))));
        // Gestor e RH já veem desde o encerramento (Q-05).
        mvc.perform(get(painel(ciclo, carla.id())).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Atendimento ao cliente")));
    }

    @Test
    void colaboradorNaoVeOResultadoDeOutraPessoa() throws Exception {
        mvc.perform(get(painel(ciclo, diego.id())).with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Diego Rocha"))))
                .andExpect(content().string(not(containsString("Atendimento ao cliente"))));
        mvc.perform(get(painel(ciclo, diego.id()) + "/excel").with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void gestorVeAEquipeDiretaENaoVeOutraEquipe() throws Exception {
        mvc.perform(get(painel(ciclo, carla.id())).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Carla Dias")));
        mvc.perform(get(painel(ciclo, igor.id())).with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Igor Matos"))));
        mvc.perform(get(painel(ciclo, carla.id())).with(como("gustavo@exemplo.com")))
                .andExpect(status().isForbidden());

        mvc.perform(get("/resultados").param("ciclo", ciclo.toString()).with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("Diego Rocha")))
                .andExpect(content().string(not(containsString("Igor Matos"))))
                .andExpect(content().string(not(containsString("Liberar resultado"))));
    }

    @Test
    void cicloAbertoInformaQueOResultadoSaiDepoisDoEncerramento() throws Exception {
        Long aberto = ciclos.abertos().get(0).id();
        mvc.perform(get(painel(aberto, carla.id())).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("depois que o ciclo for encerrado")));
        mvc.perform(get("/resultados").with(como("colaborador@exemplo.com")))
                .andExpect(content().string(containsString("o resultado sai depois do encerramento")));
    }

    @Test
    void rhLiberaOResultadoAvisaCadaAvaliadoEAudita() throws Exception {
        liberado(false);
        long antes = emailsPara("colaborador@exemplo.com", "foi liberado");
        mvc.perform(get("/resultados").param("ciclo", ciclo.toString()).with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Liberar resultado")))
                .andExpect(content().string(containsString("Igor Matos")));

        mvc.perform(post("/resultados/" + ciclo + "/liberar").with(csrf()).with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/resultados/" + ciclo + "/liberar").with(csrf()).with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden());

        mvc.perform(post("/resultados/" + ciclo + "/liberar").with(csrf()).with(como("rh@exemplo.com")))
                .andExpect(redirectedUrl("/resultados?ciclo=" + ciclo))
                .andExpect(flash().attribute("sucesso", containsString("3 pessoas foram avisadas")));

        assertThat(ciclos.ciclo(ciclo).resultadoLiberado()).isTrue();
        assertThat(emailsPara("colaborador@exemplo.com", "foi liberado")).isEqualTo(antes + 1);
        assertThat(emailsPara("diego@exemplo.com", "foi liberado")).isPositive();
        assertThat(emailsPara("igor@exemplo.com", "foi liberado")).isPositive();
        assertThat(acoesAuditadas()).anyMatch(a -> a.startsWith("RESULTADO_LIBERADO") && a.contains("Ciclo 2026.1"));

        // Liberar de novo não é permitido.
        mvc.perform(post("/resultados/" + ciclo + "/liberar").with(csrf()).with(como("rh@exemplo.com")))
                .andExpect(flash().attribute("erro", containsString("já foi liberado")));
        // Agora a Carla vê.
        mvc.perform(get("/resultados").with(como("colaborador@exemplo.com")))
                .andExpect(content().string(containsString("Meu resultado")));
    }

    @Test
    void exportaPdfEExcelComOMesmoFiltroDaTelaEAudita() throws Exception {
        MvcResult pdf = mvc.perform(get(painel(ciclo, carla.id()) + "/pdf").with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(ArquivoExportado.PDF))
                .andReturn();
        assertThat(new String(pdf.getResponse().getContentAsByteArray(), 0, 5, StandardCharsets.US_ASCII))
                .isEqualTo("%PDF-");
        assertThat(pdf.getResponse().getHeader("Content-Disposition")).contains("resultado-ciclo-2026-1-carla-dias.pdf");

        MvcResult excel = mvc.perform(get(painel(ciclo, carla.id()) + "/excel").with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(ArquivoExportado.EXCEL))
                .andReturn();
        List<String> celulas = celulas(excel.getResponse().getContentAsByteArray());
        assertThat(celulas).contains("Carla Dias", "Comunicação", "Atendimento ao cliente", "Pares", "4.63", "4.0");
        assertThat(celulas).noneMatch(c -> c.contains(PAR_OCULTO) || c.contains(PAR_OCULTO_TELA));
        assertThat(celulas).anyMatch(c -> c.contains("respostas insuficientes para preservar o anonimato"));

        assertThat(acoesAuditadas()).anyMatch(a -> a.startsWith("RESULTADO_EXPORTADO") && a.endsWith("em PDF"));
        assertThat(acoesAuditadas()).anyMatch(a -> a.startsWith("RESULTADO_EXPORTADO") && a.endsWith("em Excel"));
    }

    @Test
    void relatorioDaEquipeSoComAEquipeDireta() throws Exception {
        mvc.perform(get("/resultados/" + ciclo + "/equipe").with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Equipe de Bruno Lima")))
                .andExpect(content().string(containsString("Carla Dias")))
                .andExpect(content().string(containsString("Diego Rocha")))
                .andExpect(content().string(containsString("Média da equipe")))
                .andExpect(content().string(not(containsString("Igor Matos"))))
                .andExpect(content().string(not(containsString(PAR_OCULTO))));
        mvc.perform(get("/resultados/" + ciclo + "/equipe").param("gestor", gustavo.id().toString())
                        .with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/resultados/" + ciclo + "/equipe").param("area", "Desenvolvimento")
                        .with(como("gestor@exemplo.com")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/resultados/" + ciclo + "/equipe").with(como("colaborador@exemplo.com")))
                .andExpect(status().isForbidden());

        mvc.perform(get("/resultados/" + ciclo + "/equipe").param("area", "Desenvolvimento").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Área Desenvolvimento")))
                .andExpect(content().string(containsString("Igor Matos")))
                .andExpect(content().string(not(containsString("Carla Dias"))));

        MvcResult excel = mvc.perform(get("/resultados/" + ciclo + "/equipe/excel").with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(ArquivoExportado.EXCEL))
                .andReturn();
        List<String> celulas = celulas(excel.getResponse().getContentAsByteArray());
        assertThat(celulas).contains("Carla Dias", "Diego Rocha", "Média da equipe");
        assertThat(celulas).noneMatch(c -> c.contains(PAR_OCULTO) || c.contains(EXTERNO_OCULTO));
        mvc.perform(get("/resultados/" + ciclo + "/equipe/pdf").with(como("gestor@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(ArquivoExportado.PDF));
        assertThat(acoesAuditadas()).anyMatch(a -> a.startsWith("RELATORIO_ACESSADO") && a.contains("Equipe de Bruno Lima"));
    }

    @Test
    void evolucaoEntreCiclosComEscalasDiferentesVemEmPercentual() throws Exception {
        Long outro = idDoCiclo("Ciclo 2027.1");
        em.flush();
        jdbc.update("update ciclo set situacao = 'ENCERRADO', resultado_liberado = true, liberado_em = now() where id = ?",
                outro);
        em.clear();
        consolidado.save(linha(outro, carla, atendimento, CHEFE, "9.00", 1, 10));

        mvc.perform(get(painel(outro, carla.id())).with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("graficoEvolucao")))
                .andExpect(content().string(containsString("percentual")))
                .andExpect(content().string(containsString("92,6%")))   // 4,63 de 5 no 2026.1
                .andExpect(content().string(containsString("90,0%")))   // 9,00 de 10 no 2027.1
                .andExpect(content().string(not(containsString("Ainda não há histórico para comparar"))));
    }

    @Test
    void copiaDosDadosPessoaisTrazSoMediasExibiveis() {
        List<String> itens = fonteResultados.itens(carla.id());
        assertThat(itens).anyMatch(i -> i.contains("Ciclo 2026.1") && i.contains("Atendimento ao cliente")
                && i.contains("nota final 4,6"));
        assertThat(itens).noneMatch(i -> i.contains(PAR_OCULTO_TELA));
        liberado(false);
        assertThat(fonteResultados.itens(carla.id())).isEmpty();
    }

    @Test
    void paginaInicialAvisaResultadoLiberadoECiclosParaLiberar() throws Exception {
        mvc.perform(get("/").with(como("colaborador@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Seu resultado do Ciclo 2026.1 foi liberado")));
        liberado(false);
        mvc.perform(get("/").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Resultados para liberar")))
                .andExpect(content().string(containsString("Ciclo 2026.1")));
    }

    /** Todas as células da planilha como texto (números com o valor exato guardado). */
    private static List<String> celulas(byte[] xlsx) throws Exception {
        List<String> textos = new ArrayList<>();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            for (Sheet folha : wb) {
                for (Row row : folha) {
                    for (Cell cell : row) {
                        textos.add(cell.getCellType() == CellType.NUMERIC
                                ? String.valueOf(cell.getNumericCellValue()) : cell.getStringCellValue());
                    }
                }
            }
        }
        return textos;
    }
}
