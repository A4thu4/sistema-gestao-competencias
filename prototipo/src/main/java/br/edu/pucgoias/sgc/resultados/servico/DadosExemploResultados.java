package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
import br.edu.pucgoias.sgc.compartilhado.servico.RegistrosDaCarga;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.resultados.dominio.MetaAtualizacao;
import br.edu.pucgoias.sgc.resultados.dominio.MetaDesenvolvimento;
import br.edu.pucgoias.sgc.resultados.dominio.SituacaoMeta;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaAtualizacaoRepository;
import br.edu.pucgoias.sgc.resultados.repositorio.MetaDesenvolvimentoRepository;
import br.edu.pucgoias.sgc.resultados.repositorio.ResultadoConsolidadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Dados de exemplo do pacote resultados. Roda depois das respostas do avaliacoes ({@code @Order(40)}) e só com
 * {@code sgc.demo.carregar=true}. Cada parte é idempotente:
 * <ul>
 *   <li>com a tabela {@code resultado_consolidado} vazia: consolida os ciclos 2025.2 e 2026.1 (encerrados) a
 *       partir das respostas e libera o resultado, com os avisos por e-mail datados de dezembro de 2025 e de junho
 *       de 2026; assim o painel mostra a evolução entre ciclos. O 2026.2 (aberto) não é tocado: a demonstração o
 *       encerra ao vivo e a consolidação acontece pelo evento;</li>
 *   <li>com a tabela {@code meta_desenvolvimento} vazia: plano da Carla (resolução de problemas técnicos, em
 *       andamento com duas atualizações, e conhecimento do produto, não iniciada) e do Diego (uma meta). Datas
 *       recentes, para o lembrete trimestral não disparar logo na subida.</li>
 * </ul>
 */
@Component
@Order(50)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploResultados implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosExemploResultados.class);
    private static final String CICLO = "Ciclo 2026.1";
    private static final String CICLO_ANTERIOR = "Ciclo 2025.2";

    private final ResultadoConsolidadoRepository consolidado;
    private final MetaDesenvolvimentoRepository metas;
    private final MetaAtualizacaoRepository atualizacoes;
    private final ConsolidacaoService consolidacao;
    private final ResultadoService resultados;
    private final CicloService ciclos;
    private final UsuarioService usuarios;
    private final RegistrosDaCarga registros;

    public DadosExemploResultados(ResultadoConsolidadoRepository consolidado, MetaDesenvolvimentoRepository metas,
                                  MetaAtualizacaoRepository atualizacoes, ConsolidacaoService consolidacao,
                                  ResultadoService resultados, CicloService ciclos, UsuarioService usuarios,
                                  RegistrosDaCarga registros) {
        this.consolidado = consolidado;
        this.metas = metas;
        this.atualizacoes = atualizacoes;
        this.consolidacao = consolidacao;
        this.resultados = resultados;
        this.ciclos = ciclos;
        this.usuarios = usuarios;
        this.registros = registros;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        CicloResumo ciclo = encerrado(CICLO);
        if (ciclo == null) {
            log.warn("Dados de exemplo de resultados não carregados: não há o {} encerrado.", CICLO);
            return;
        }
        if (consolidado.count() == 0) {
            CicloResumo anterior = encerrado(CICLO_ANTERIOR);
            if (anterior != null) {
                consolidarELiberar(anterior, LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 4));
            }
            consolidarELiberar(ciclo, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3));
        }
        if (metas.count() == 0) {
            criarPlanos(ciclo);
        }
    }

    private CicloResumo encerrado(String nome) {
        return ciclos.encerrados().stream().filter(c -> nome.equals(c.nome())).findFirst().orElse(null);
    }

    /** Consolida e libera, com o log e os e-mails datados de quando isso teria acontecido. */
    private void consolidarELiberar(CicloResumo ciclo, LocalDate consolidadoEm, LocalDate liberadoEm) {
        RegistrosDaCarga.Marca marca = registros.marcar();
        int linhas = consolidacao.consolidarResultados(ciclo.id());
        registros.datar(marca, consolidadoEm.atTime(0, 5).atZone(Formatos.ZONA).toOffsetDateTime());
        marca = registros.marcar();
        int avisos = ciclo.resultadoLiberado() ? 0 : resultados.liberarCiclo(ciclo.id());
        registros.datar(marca, liberadoEm.atTime(10, 20).atZone(Formatos.ZONA).toOffsetDateTime());
        log.info("Dados de exemplo: {} consolidado ({} médias) e liberado ({} avisos por e-mail)", ciclo.nome(),
                linhas, avisos);
    }

    private void criarPlanos(CicloResumo ciclo) {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        PessoaResumo diego = usuarios.buscarPorEmail("diego@exemplo.com");
        Long tecnica = competencia(ciclo, carla, "Resolução de problemas técnicos");
        Long produto = competencia(ciclo, carla, "Conhecimento do produto");
        Long atendimento = competencia(ciclo, diego, "Atendimento ao cliente");
        if (tecnica == null || produto == null || atendimento == null) {
            log.warn("Dados de exemplo: planos não criados (resultado insuficiente para Carla ou Diego).");
            return;
        }

        MetaDesenvolvimento emAndamento = metas.save(new MetaDesenvolvimento(carla.id(), carla.gestorId(), ciclo.id(),
                tecnica,
                "Usar dados para explicar a causa dos chamados que se repetem",
                "Curso on-line de análise de dados para iniciantes (20 h); apresentar à equipe, uma vez por mês, "
                        + "a análise dos chamados repetidos",
                Formatos.hoje().plusMonths(4), SituacaoMeta.EM_ANDAMENTO, em(45, 10, 12)));
        atualizacoes.save(new MetaAtualizacao(emAndamento.getId(), carla.id(), SituacaoMeta.EM_ANDAMENTO,
                "Comecei o curso de análise de dados; terminei os dois primeiros módulos.", em(30, 17, 48)));
        atualizacoes.save(new MetaAtualizacao(emAndamento.getId(), carla.gestorId(), SituacaoMeta.EM_ANDAMENTO,
                "Conversamos na reunião individual: a primeira análise mensal fica para a próxima reunião da equipe.",
                em(12, 14, 35)));
        metas.save(new MetaDesenvolvimento(carla.id(), carla.gestorId(), ciclo.id(), produto,
                "Dominar o módulo fiscal do produto",
                "Curso interno do módulo fiscal (12 h); dois plantões com o time de produto para acompanhar as "
                        + "dúvidas dos clientes",
                Formatos.hoje().plusMonths(6), SituacaoMeta.NAO_INICIADA, em(45, 10, 26)));
        metas.save(new MetaDesenvolvimento(diego.id(), diego.gestorId(), ciclo.id(), atendimento,
                "Melhorar o primeiro atendimento ao cliente",
                "Treinamento interno de atendimento (8 h); acompanhar dois atendimentos de um colega experiente",
                Formatos.hoje().plusMonths(3), SituacaoMeta.NAO_INICIADA, em(40, 15, 4)));
        log.info("Dados de exemplo: planos de desenvolvimento da Carla (2 metas) e do Diego (1 meta)");
    }

    private Long competencia(CicloResumo ciclo, PessoaResumo p, String nome) {
        return resultados.montagem(ciclo.id(), p.id()).linhas().stream()
                .filter(l -> l.notaFinal() != null && nome.equals(l.competencia()))
                .map(LinhaCompetencia::competenciaId).findFirst().orElse(null);
    }

    private static OffsetDateTime em(int diasAtras, int hora, int minuto) {
        return Formatos.hoje().minusDays(diasAtras).atTime(hora, minuto).atZone(Formatos.ZONA).toOffsetDateTime();
    }
}
