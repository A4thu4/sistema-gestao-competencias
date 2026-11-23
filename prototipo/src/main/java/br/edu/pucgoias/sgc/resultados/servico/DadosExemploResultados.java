package br.edu.pucgoias.sgc.resultados.servico;

import br.edu.pucgoias.sgc.ciclos.servico.CicloResumo;
import br.edu.pucgoias.sgc.ciclos.servico.CicloService;
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

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Dados de exemplo do pacote resultados. Roda depois das respostas do avaliacoes ({@code @Order(40)}) e só com
 * {@code sgc.demo.carregar=true}. Cada parte é idempotente:
 * <ul>
 *   <li>com a tabela {@code resultado_consolidado} vazia: consolida o "Ciclo 2026.1" (encerrado) a partir das
 *       respostas e libera o resultado, com os avisos por e-mail. O 2026.2 (aberto) não é tocado: a demonstração
 *       o encerra ao vivo e a consolidação acontece pelo evento;</li>
 *   <li>com a tabela {@code meta_desenvolvimento} vazia: plano da Carla (duas metas nas competências de menor nota,
 *       uma em andamento com duas atualizações e uma não iniciada) e do Diego (uma meta). Datas recentes, para o
 *       lembrete trimestral não disparar logo na subida.</li>
 * </ul>
 */
@Component
@Order(50)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploResultados implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosExemploResultados.class);
    private static final String CICLO = "Ciclo 2026.1";

    private final ResultadoConsolidadoRepository consolidado;
    private final MetaDesenvolvimentoRepository metas;
    private final MetaAtualizacaoRepository atualizacoes;
    private final ConsolidacaoService consolidacao;
    private final ResultadoService resultados;
    private final CicloService ciclos;
    private final UsuarioService usuarios;

    public DadosExemploResultados(ResultadoConsolidadoRepository consolidado, MetaDesenvolvimentoRepository metas,
                                  MetaAtualizacaoRepository atualizacoes, ConsolidacaoService consolidacao,
                                  ResultadoService resultados, CicloService ciclos, UsuarioService usuarios) {
        this.consolidado = consolidado;
        this.metas = metas;
        this.atualizacoes = atualizacoes;
        this.consolidacao = consolidacao;
        this.resultados = resultados;
        this.ciclos = ciclos;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        CicloResumo ciclo = ciclos.encerrados().stream().filter(c -> CICLO.equals(c.nome())).findFirst().orElse(null);
        if (ciclo == null) {
            log.warn("Dados de exemplo de resultados não carregados: não há o {} encerrado.", CICLO);
            return;
        }
        if (consolidado.count() == 0) {
            int linhas = consolidacao.consolidarResultados(ciclo.id());
            int avisos = ciclo.resultadoLiberado() ? 0 : resultados.liberarCiclo(ciclo.id());
            log.info("Dados de exemplo: {} consolidado ({} médias) e liberado ({} avisos por e-mail)", CICLO, linhas,
                    avisos);
        }
        if (metas.count() == 0) {
            criarPlanos(ciclo);
        }
    }

    private void criarPlanos(CicloResumo ciclo) {
        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        PessoaResumo diego = usuarios.buscarPorEmail("diego@exemplo.com");
        List<LinhaCompetencia> daCarla = menores(ciclo, carla);
        List<LinhaCompetencia> doDiego = menores(ciclo, diego);
        if (daCarla.size() < 2 || doDiego.isEmpty()) {
            log.warn("Dados de exemplo: planos não criados (resultado insuficiente para Carla ou Diego).");
            return;
        }
        OffsetDateTime agora = OffsetDateTime.now();

        MetaDesenvolvimento emAndamento = metas.save(new MetaDesenvolvimento(carla.id(), carla.gestorId(), ciclo.id(),
                daCarla.get(0).competenciaId(),
                "Usar dados para explicar a causa dos chamados que se repetem",
                "Curso on-line de análise de dados para iniciantes (20 h); apresentar à equipe, uma vez por mês, "
                        + "a análise dos chamados repetidos",
                Formatos.hoje().plusMonths(4), SituacaoMeta.EM_ANDAMENTO, agora.minusDays(45)));
        atualizacoes.save(new MetaAtualizacao(emAndamento.getId(), carla.id(), SituacaoMeta.EM_ANDAMENTO,
                "Comecei o curso de análise de dados; terminei os dois primeiros módulos.", agora.minusDays(30)));
        atualizacoes.save(new MetaAtualizacao(emAndamento.getId(), carla.gestorId(), SituacaoMeta.EM_ANDAMENTO,
                "Conversamos na reunião individual: a primeira análise mensal fica para a próxima reunião da equipe.",
                agora.minusDays(12)));
        metas.save(new MetaDesenvolvimento(carla.id(), carla.gestorId(), ciclo.id(), daCarla.get(1).competenciaId(),
                "Participar mais das decisões da equipe",
                "Conduzir uma reunião semanal da equipe por mês; pedir retorno dos colegas depois de cada uma",
                Formatos.hoje().plusMonths(6), SituacaoMeta.NAO_INICIADA, agora.minusDays(45)));
        metas.save(new MetaDesenvolvimento(diego.id(), diego.gestorId(), ciclo.id(), doDiego.get(0).competenciaId(),
                "Melhorar o primeiro atendimento ao cliente",
                "Treinamento interno de atendimento (8 h); acompanhar dois atendimentos de um colega experiente",
                Formatos.hoje().plusMonths(3), SituacaoMeta.NAO_INICIADA, agora.minusDays(40)));
        log.info("Dados de exemplo: planos de desenvolvimento da Carla (2 metas) e do Diego (1 meta)");
    }

    /** Competências com nota final, da menor para a maior. */
    private List<LinhaCompetencia> menores(CicloResumo ciclo, PessoaResumo p) {
        return resultados.montagem(ciclo.id(), p.id()).linhas().stream().filter(l -> l.notaFinal() != null)
                .sorted(Comparator.comparing(LinhaCompetencia::notaFinal)).toList();
    }
}
