package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exportação dos resultados (RF-23). Recebe o mesmo {@link PainelIndividual} ou {@link RelatorioEquipe} que a tela
 * mostra, já com as regras de exibição aplicadas: o arquivo nunca tem média de grupo com menos de 3 respostas nem
 * autoria (RNF-01, RNF-02). No Excel as notas ficam como número (formato com uma casa, como na tela); as vazias,
 * como "—".
 */
@Component
public class ExportacaoResultados {

    private static final String RODAPE = "Sistema de Gestão de Competências. As respostas de pares, subordinados e "
            + "externos aparecem só agrupadas, sem identificar quem respondeu.";

    // ==== PDF ====

    public byte[] painelPdf(PainelIndividual p) {
        return pdf("Resultado da avaliação", doc -> {
            doc.add(new Paragraph("Resultado da avaliação", Fontes.TITULO));
            doc.add(new Paragraph("Colaborador: " + p.pessoa().nome() + " · " + p.pessoa().cargoNome()
                    + " · Ciclo: " + p.ciclo().nome(), Fontes.NORMAL));
            doc.add(Chunk.NEWLINE);

            PdfPTable resumo = new PdfPTable(3);
            resumo.setWidthPercentage(100);
            resumo.addCell(celula("Média geral\n" + p.mediaGeralTexto() + " / " + p.escalaTexto(), Fontes.NEGRITO));
            resumo.addCell(celula("Principal ponto forte\n" + texto(p.pontoForte()), Fontes.NEGRITO));
            resumo.addCell(celula("Foco de desenvolvimento\n" + texto(p.foco()), Fontes.NEGRITO));
            doc.add(resumo);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Detalhamento das notas", Fontes.SECAO));
            PdfPTable tabela = new PdfPTable(p.grupos().size() + 2);
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(6);
            tabela.addCell(cabecalho("Competência"));
            p.grupos().forEach(g -> tabela.addCell(cabecalho(Grupos.rotulo(g))));
            tabela.addCell(cabecalho("Nota final"));
            for (LinhaCompetencia l : p.linhas()) {
                tabela.addCell(celula(l.competencia(), Fontes.NORMAL));
                for (TipoAvaliador g : p.grupos()) {
                    tabela.addCell(numero(l.grupo(g).texto(), Fontes.NORMAL));
                }
                tabela.addCell(numero(l.notaFinalTexto(), Fontes.NEGRITO));
            }
            doc.add(tabela);
            doc.add(new Paragraph(p.ajudaNotaFinal(), Fontes.DISCRETO));
            if (p.avisoInsuficientes() != null) {
                doc.add(new Paragraph(p.avisoInsuficientes(), Fontes.DISCRETO));
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Comentários (sem identificação do autor)", Fontes.SECAO));
            if (p.comentarios().isEmpty()) {
                doc.add(new Paragraph("Nenhum comentário.", Fontes.NORMAL));
            }
            for (ComentarioPainel c : p.comentarios()) {
                doc.add(new Paragraph(c.competencia() + " · " + c.pergunta(), Fontes.DISCRETO));
                doc.add(new Paragraph("“" + c.texto() + "”", Fontes.NORMAL));
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Evolução entre ciclos", Fontes.SECAO));
            Evolucao ev = p.evolucao();
            if (!ev.temHistorico()) {
                doc.add(new Paragraph("Ainda não há ciclo anterior para comparar.", Fontes.NORMAL));
            } else {
                doc.add(new Paragraph(ev.legenda(), Fontes.DISCRETO));
                PdfPTable evolucao = new PdfPTable(ev.ciclos().size() + 1);
                evolucao.setWidthPercentage(100);
                evolucao.setSpacingBefore(6);
                evolucao.addCell(cabecalho("Competência"));
                ev.ciclos().forEach(c -> evolucao.addCell(cabecalho(c)));
                for (SerieGrafico s : ev.series()) {
                    evolucao.addCell(celula(s.rotulo(), Fontes.NORMAL));
                    s.valores().forEach(v -> evolucao.addCell(numero(ev.texto(v), Fontes.NORMAL)));
                }
                doc.add(evolucao);
            }
        });
    }

    public byte[] relatorioPdf(RelatorioEquipe r) {
        return pdf("Relatório de resultados", doc -> {
            doc.add(new Paragraph("Relatório de resultados · " + r.titulo(), Fontes.TITULO));
            doc.add(new Paragraph(r.descricao() + " · Ciclo: " + r.ciclo().nome(), Fontes.NORMAL));
            doc.add(Chunk.NEWLINE);
            if (r.vazio()) {
                doc.add(new Paragraph("Nenhuma pessoa com resultado neste ciclo.", Fontes.NORMAL));
                return;
            }
            PdfPTable tabela = new PdfPTable(r.competencias().size() + 2);
            tabela.setWidthPercentage(100);
            tabela.addCell(cabecalho("Pessoa"));
            r.competencias().values().forEach(n -> tabela.addCell(cabecalho(n)));
            tabela.addCell(cabecalho("Média geral"));
            for (RelatorioEquipe.PessoaNoRelatorio p : r.pessoas()) {
                tabela.addCell(celula(p.pessoa().nome(), Fontes.NORMAL));
                r.competencias().keySet().forEach(id -> tabela.addCell(numero(p.nota(id), Fontes.NORMAL)));
                tabela.addCell(numero(p.mediaGeralTexto(), Fontes.NEGRITO));
            }
            tabela.addCell(celula("Média da equipe", Fontes.NEGRITO));
            r.competencias().keySet().forEach(id -> tabela.addCell(numero(r.mediaEquipe(id), Fontes.NEGRITO)));
            tabela.addCell(numero(r.mediaGeralEquipeTexto(), Fontes.NEGRITO));
            doc.add(tabela);
            doc.add(new Paragraph("Notas finais por competência (escala de 1 a " + r.escalaMax() + "). "
                    + RegrasDeExibicao.AJUDA_NOTA_FINAL, Fontes.DISCRETO));
        });
    }

    // ==== Excel ====

    public byte[] painelExcel(PainelIndividual p) {
        return excel(wb -> {
            Estilos e = new Estilos(wb);
            Sheet folha = wb.createSheet("Resultado");
            int n = 0;
            linha(folha, n++, e.titulo, "Resultado da avaliação");
            linha(folha, n++, null, "Colaborador", p.pessoa().nome());
            linha(folha, n++, null, "Cargo", p.pessoa().cargoNome());
            linha(folha, n++, null, "Ciclo", p.ciclo().nome());
            Row media = folha.createRow(n++);
            media.createCell(0).setCellValue("Média geral (escala de 1 a " + p.escalaMax() + ")");
            nota(media, 1, p.mediaGeral(), e.nota);
            linha(folha, n++, null, "Principal ponto forte", texto(p.pontoForte()));
            linha(folha, n++, null, "Foco de desenvolvimento", texto(p.foco()));
            n++;
            List<String> cab = new ArrayList<>();
            cab.add("Competência");
            p.grupos().forEach(g -> cab.add(Grupos.rotulo(g)));
            cab.add("Nota final");
            linha(folha, n++, e.cabecalho, cab.toArray(String[]::new));
            for (LinhaCompetencia l : p.linhas()) {
                Row row = folha.createRow(n++);
                row.createCell(0).setCellValue(l.competencia());
                int col = 1;
                for (TipoAvaliador g : p.grupos()) {
                    nota(row, col++, l.grupo(g).media(), e.nota);
                }
                nota(row, col, l.notaFinal(), e.nota);
            }
            n++;
            linha(folha, n++, null, p.ajudaNotaFinal());
            if (p.avisoInsuficientes() != null) {
                linha(folha, n++, null, p.avisoInsuficientes());
            }
            linha(folha, n, null, RODAPE);
            folha.setColumnWidth(0, 34 * 256);
            for (int i = 1; i <= p.grupos().size() + 1; i++) {
                folha.setColumnWidth(i, 15 * 256);
            }

            Sheet comentarios = wb.createSheet("Comentários");
            linha(comentarios, 0, e.cabecalho, "Competência", "Pergunta", "Comentário (sem autor)");
            int c = 1;
            for (ComentarioPainel com : p.comentarios()) {
                linha(comentarios, c++, null, com.competencia(), com.pergunta(), com.texto());
            }
            if (p.comentarios().isEmpty()) {
                linha(comentarios, 1, null, "Nenhum comentário.");
            }
            comentarios.setColumnWidth(0, 28 * 256);
            comentarios.setColumnWidth(1, 45 * 256);
            comentarios.setColumnWidth(2, 80 * 256);

            Sheet evolucao = wb.createSheet("Evolução");
            Evolucao ev = p.evolucao();
            if (!ev.temHistorico()) {
                linha(evolucao, 0, null, "Ainda não há ciclo anterior para comparar.");
            } else {
                List<String> cabEv = new ArrayList<>();
                cabEv.add("Competência");
                cabEv.addAll(ev.ciclos());
                linha(evolucao, 0, e.cabecalho, cabEv.toArray(String[]::new));
                int r = 1;
                for (SerieGrafico s : ev.series()) {
                    Row row = evolucao.createRow(r++);
                    row.createCell(0).setCellValue(s.rotulo());
                    for (int i = 0; i < s.valores().size(); i++) {
                        nota(row, i + 1, s.valores().get(i), e.nota);
                    }
                }
                linha(evolucao, r + 1, null, ev.legenda());
            }
            evolucao.setColumnWidth(0, 34 * 256);
        });
    }

    public byte[] relatorioExcel(RelatorioEquipe r) {
        return excel(wb -> {
            Estilos e = new Estilos(wb);
            Sheet folha = wb.createSheet("Relatório");
            int n = 0;
            linha(folha, n++, e.titulo, "Relatório de resultados · " + r.titulo());
            linha(folha, n++, null, "Ciclo", r.ciclo().nome());
            linha(folha, n++, null, r.descricao());
            n++;
            List<String> cab = new ArrayList<>();
            cab.add("Pessoa");
            cab.addAll(r.competencias().values());
            cab.add("Média geral");
            linha(folha, n++, e.cabecalho, cab.toArray(String[]::new));
            for (RelatorioEquipe.PessoaNoRelatorio p : r.pessoas()) {
                Row row = folha.createRow(n++);
                row.createCell(0).setCellValue(p.pessoa().nome());
                int col = 1;
                for (Long id : r.competencias().keySet()) {
                    nota(row, col++, p.notas().get(id), e.nota);
                }
                nota(row, col, p.mediaGeral(), e.nota);
            }
            Row media = folha.createRow(n++);
            media.createCell(0).setCellValue("Média da equipe");
            int col = 1;
            for (Map.Entry<Long, BigDecimal> m : r.mediaEquipe().entrySet()) {
                nota(media, col++, m.getValue(), e.nota);
            }
            nota(media, col, r.mediaGeralEquipe(), e.nota);
            n++;
            linha(folha, n++, null, "Notas finais por competência (escala de 1 a " + r.escalaMax() + "). "
                    + RegrasDeExibicao.AJUDA_NOTA_FINAL);
            linha(folha, n, null, RODAPE);
            folha.setColumnWidth(0, 30 * 256);
            for (int i = 1; i <= r.competencias().size() + 1; i++) {
                folha.setColumnWidth(i, 16 * 256);
            }
        });
    }

    // ==== apoio ====

    private interface ConteudoPdf {
        void escrever(Document doc) throws Exception;
    }

    private interface ConteudoExcel {
        void escrever(XSSFWorkbook wb);
    }

    private static byte[] pdf(String titulo, ConteudoPdf conteudo) {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
            PdfWriter.getInstance(doc, saida);
            doc.addTitle(titulo);
            doc.open();
            conteudo.escrever(doc);
            doc.add(new Paragraph(" "));
            Paragraph rodape = new Paragraph(RODAPE + " Gerado em " + Formatos.dataHora(java.time.OffsetDateTime.now())
                    + ".", Fontes.DISCRETO);
            rodape.setAlignment(Element.ALIGN_LEFT);
            doc.add(rodape);
            doc.close();
            return saida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar o PDF.", e);
        }
    }

    private static byte[] excel(ConteudoExcel conteudo) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            conteudo.escrever(wb);
            wb.write(saida);
            return saida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gerar a planilha.", e);
        }
    }

    private static String texto(String s) {
        return s == null ? Formatos.SEM_NOTA : s;
    }

    private static PdfPCell celula(String texto, Font fonte) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fonte));
        c.setPadding(5);
        c.setBorderColor(new Color(0xe4, 0xe7, 0xec));
        return c;
    }

    private static PdfPCell numero(String texto, Font fonte) {
        PdfPCell c = celula(texto, fonte);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    private static PdfPCell cabecalho(String texto) {
        PdfPCell c = celula(texto, Fontes.CABECALHO);
        c.setBackgroundColor(new Color(0xf9, 0xfa, 0xfb));
        return c;
    }

    private static void linha(Sheet folha, int numero, CellStyle estilo, String... valores) {
        Row row = folha.createRow(numero);
        for (int i = 0; i < valores.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(valores[i]);
            if (estilo != null) {
                cell.setCellStyle(estilo);
            }
        }
    }

    /** Nota como número (com o mesmo arredondamento da tela no formato) ou "—" quando não pode aparecer. */
    private static void nota(Row row, int coluna, BigDecimal valor, CellStyle estilo) {
        Cell cell = row.createCell(coluna);
        if (valor == null) {
            cell.setCellValue(Formatos.SEM_NOTA);
            return;
        }
        cell.setCellValue(valor.doubleValue());
        cell.setCellStyle(estilo);
    }

    private static final class Fontes {
        static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(0x1d, 0x24, 0x33));
        static final Font SECAO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0x2f, 0x4b, 0xcc));
        static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
        static final Font NEGRITO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        static final Font CABECALHO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(0x66, 0x70, 0x85));
        static final Font DISCRETO = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(0x66, 0x70, 0x85));
    }

    private static final class Estilos {
        final CellStyle titulo;
        final CellStyle cabecalho;
        final CellStyle nota;

        Estilos(XSSFWorkbook wb) {
            titulo = wb.createCellStyle();
            var fonteTitulo = wb.createFont();
            fonteTitulo.setBold(true);
            fonteTitulo.setFontHeightInPoints((short) 14);
            titulo.setFont(fonteTitulo);
            cabecalho = wb.createCellStyle();
            var fonteCab = wb.createFont();
            fonteCab.setBold(true);
            cabecalho.setFont(fonteCab);
            nota = wb.createCellStyle();
            nota.setDataFormat(wb.createDataFormat().getFormat("0.0"));
        }
    }
}
