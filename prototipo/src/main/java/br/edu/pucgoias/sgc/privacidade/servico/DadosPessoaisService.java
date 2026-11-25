package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Os dados pessoais que o sistema mantém sobre o titular e a cópia desses dados, na tela ou em PDF. Só o necessário à
 * avaliação e sem autoria de respostas de pares e subordinados.
 */
@Service
@Transactional(readOnly = true)
public class DadosPessoaisService {

    /** Explicação mostrada na cópia e no PDF sobre o anonimato das avaliações. */
    public static final String NOTA_ANONIMATO = "As respostas que colegas, subordinados e avaliadores externos deram "
            + "sobre você aparecem só somadas nos resultados, sem dizer quem respondeu. As respostas que você deu "
            + "como avaliador de outras pessoas também são anônimas e, por isso, não estão ligadas ao seu nome.";

    private final UsuarioService usuarios;
    private final ConsentimentoService consentimento;
    private final SolicitacaoService solicitacoes;
    private final ObjectProvider<FonteDadosPessoais> fontes;

    public DadosPessoaisService(UsuarioService usuarios, ConsentimentoService consentimento,
                                SolicitacaoService solicitacoes, ObjectProvider<FonteDadosPessoais> fontes) {
        this.usuarios = usuarios;
        this.consentimento = consentimento;
        this.solicitacoes = solicitacoes;
        this.fontes = fontes;
    }

    /** Dados cadastrais mantidos sobre a pessoa logada (só o necessário). */
    public List<ItemDado> dadosCadastrais(UsuarioAutenticado eu) {
        return dadosCadastrais(eu.id());
    }

    private List<ItemDado> dadosCadastrais(Long usuarioId) {
        PessoaResumo p = usuarios.buscar(usuarioId);
        String gestor = p.gestorId() == null ? "—" : usuarios.buscar(p.gestorId()).nome();
        return List.of(
                new ItemDado("Nome", p.nome()),
                new ItemDado("E-mail", p.email()),
                new ItemDado("Perfil de acesso", p.perfil().getRotulo()),
                new ItemDado("Cargo", p.cargoNome()),
                new ItemDado("Área", p.area()),
                new ItemDado("Gestor direto", gestor));
    }

    public CopiaDados gerarCopia(UsuarioAutenticado eu) {
        List<SecaoDados> secoes = new ArrayList<>();
        fontes.orderedStream().forEach(f -> secoes.add(new SecaoDados(f.titulo(), List.copyOf(f.itens(eu.id())))));
        return new CopiaDados(eu.nome(), OffsetDateTime.now(), dadosCadastrais(eu.id()),
                consentimento.aceites(eu.id()), solicitacoes.doUsuario(eu.id()), secoes);
    }

    /** A mesma cópia em PDF, para baixar. */
    public byte[] gerarPdf(UsuarioAutenticado eu) {
        CopiaDados copia = gerarCopia(eu);
        try {
            return montarPdf(copia);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar o PDF da cópia dos dados.", e);
        }
    }

    private static byte[] montarPdf(CopiaDados copia) throws Exception {
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(0x1d, 0x24, 0x33));
        Font secao = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0x2f, 0x4b, 0xcc));
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font negrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font discreto = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(0x66, 0x70, 0x85));

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(doc, saida);
        doc.addTitle("Cópia dos dados pessoais");
        doc.open();
        doc.add(new Paragraph("Cópia dos seus dados pessoais", titulo));
        doc.add(new Paragraph("Sistema de Gestão de Competências · gerada em " + copia.geradaEmTexto(), discreto));
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("Dados cadastrais", secao));
        PdfPTable tabela = new PdfPTable(new float[]{1, 2});
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(6);
        for (ItemDado d : copia.dadosCadastrais()) {
            tabela.addCell(celula(d.rotulo(), negrito));
            tabela.addCell(celula(d.valor(), normal));
        }
        doc.add(tabela);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Termo de consentimento", secao));
        if (copia.aceites().isEmpty()) {
            doc.add(new Paragraph("Nenhum aceite registrado.", normal));
        }
        for (AceiteResumo a : copia.aceites()) {
            doc.add(new Paragraph("Versão " + a.versao() + " aceita em " + a.dataHora(), normal));
        }

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Solicitações sobre os seus dados", secao));
        if (copia.solicitacoes().isEmpty()) {
            doc.add(new Paragraph("Nenhuma solicitação.", normal));
        }
        for (SolicitacaoResumo s : copia.solicitacoes()) {
            doc.add(new Paragraph(s.protocolo() + " · " + s.tipo().getRotulo() + " · " + s.situacao().getRotulo()
                    + " · " + s.criadaEmTexto(), normal));
        }

        for (SecaoDados sec : copia.outrasSecoes()) {
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(sec.titulo(), secao));
            if (sec.itens().isEmpty()) {
                doc.add(new Paragraph("Nada registrado.", normal));
            }
            for (String item : sec.itens()) {
                doc.add(new Paragraph("• " + item, normal));
            }
        }

        doc.add(new Paragraph(" "));
        Paragraph nota = new Paragraph(NOTA_ANONIMATO, discreto);
        nota.setAlignment(Element.ALIGN_JUSTIFIED);
        doc.add(nota);
        doc.add(new Paragraph("Dúvidas ou pedidos: " + SolicitacaoService.CANAL_ENCARREGADO, discreto));
        doc.close();
        return saida.toByteArray();
    }

    private static PdfPCell celula(String texto, Font fonte) {
        PdfPCell c = new PdfPCell(new Phrase(texto == null ? "—" : texto, fonte));
        c.setPadding(5);
        c.setBorderColor(new Color(0xe4, 0xe7, 0xec));
        return c;
    }
}
