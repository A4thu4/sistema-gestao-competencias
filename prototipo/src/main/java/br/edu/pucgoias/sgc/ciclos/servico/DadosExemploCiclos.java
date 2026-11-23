package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.ciclos.dominio.Ciclo;
import br.edu.pucgoias.sgc.ciclos.dominio.Designacao;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoCiclo;
import br.edu.pucgoias.sgc.ciclos.dominio.SituacaoDesignacao;
import br.edu.pucgoias.sgc.ciclos.repositorio.CicloRepository;
import br.edu.pucgoias.sgc.ciclos.repositorio.DesignacaoRepository;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ciclos de exemplo para a demonstração. Roda depois das cargas do identidade e do competencias e só com a tabela
 * de ciclos vazia:
 * <ol>
 *   <li>"Ciclo 2026.1", encerrado (maio de 2026), com todas as avaliações enviadas (as respostas vêm do
 *       pacote avaliacoes);</li>
 *   <li>"Ciclo 2026.2", aberto (abriu há 10 dias, encerra em 12), com um avaliador externo para a Carla e
 *       situações variadas. Ficam não iniciadas as avaliações em que Carla, Bruno e Diego avaliam, e a do externo;</li>
 *   <li>"Ciclo 2027.1", em rascunho, para mostrar a edição.</li>
 * </ol>
 * Os ciclos no passado são gravados direto pelo repositório, sem a validação "abertura não anterior a hoje".
 */
@Component
@Order(30)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploCiclos implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosExemploCiclos.class);
    private static final Set<TipoAvaliador> QUATRO_TIPOS =
            EnumSet.of(TipoAvaliador.CHEFE, TipoAvaliador.AUTO, TipoAvaliador.PAR, TipoAvaliador.SUBORDINADO);
    private static final List<String> AVALIADOS = List.of("gestor", "colaborador", "diego", "elisa", "fabio",
            "gustavo", "igor", "julia", "katia");

    private final CicloRepository ciclos;
    private final DesignacaoRepository designacoes;
    private final MontagemCicloService montagem;
    private final ConviteService convites;
    private final UsuarioService usuarios;

    public DadosExemploCiclos(CicloRepository ciclos, DesignacaoRepository designacoes, MontagemCicloService montagem,
                              ConviteService convites, UsuarioService usuarios) {
        this.ciclos = ciclos;
        this.designacoes = designacoes;
        this.montagem = montagem;
        this.convites = convites;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (ciclos.count() > 0) {
            return;
        }
        if (usuarios.listarAtivos().isEmpty()) {
            log.warn("Dados de exemplo de ciclos não carregados: não há pessoas.");
            return;
        }
        List<Long> avaliados = AVALIADOS.stream().map(this::pessoa).map(PessoaResumo::id).toList();
        LocalDate hoje = Datas.hoje();

        // 1. Ciclo encerrado.
        Ciclo encerrado = ciclos.save(new Ciclo("Ciclo 2026.1", QUATRO_TIPOS,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31)));
        montagem.salvarAvaliados(encerrado.getId(), avaliados, null, null, Map.of());
        OffsetDateTime abertura1 = LocalDate.of(2026, 5, 1).atTime(8, 0).atZone(Datas.ZONA).toOffsetDateTime();
        encerrado.forcarSituacao(SituacaoCiclo.ENCERRADO, abertura1,
                LocalDate.of(2026, 6, 1).atTime(0, 5).atZone(Datas.ZONA).toOffsetDateTime());
        for (Designacao d : designacoes.findByCicloIdOrderById(encerrado.getId())) {
            d.definirSituacao(SituacaoDesignacao.ENVIADA);
            d.registrarConvite(abertura1);
        }

        // 2. Ciclo aberto, com externo e situações variadas.
        Ciclo aberto = ciclos.save(new Ciclo("Ciclo 2026.2", QUATRO_TIPOS, hoje.minusDays(10), hoje.plusDays(12)));
        montagem.salvarAvaliados(aberto.getId(), avaliados, null, null, Map.of());
        Long carla = pessoa("colaborador").id();
        montagem.incluirExterno(aberto.getId(), carla, "Marcos Tavares", "marcos.cliente@exemplo.com");
        aberto.forcarSituacao(SituacaoCiclo.ABERTO,
                hoje.minusDays(10).atTime(8, 0).atZone(Datas.ZONA).toOffsetDateTime(), null);
        Set<Long> ficamParaDemonstracao = Set.of(carla, pessoa("gestor").id(), pessoa("diego").id());
        int i = 0;
        for (Designacao d : designacoes.findByCicloIdOrderById(aberto.getId())) {
            if (d.isExterna() || ficamParaDemonstracao.contains(d.getAvaliadorId())) {
                continue;
            }
            if (i % 4 == 0) {
                d.definirSituacao(SituacaoDesignacao.ENVIADA);
            } else if (i % 4 == 1) {
                d.definirSituacao(SituacaoDesignacao.EM_PREENCHIMENTO);
            }
            i++;
        }
        ConviteService.ResultadoEnvio envio = convites.enviarConvitesPendentes(aberto);

        // 3. Rascunho para a edição.
        LocalDate inicio = LocalDate.of(2027, 1, 4);
        if (!inicio.isAfter(hoje)) {
            inicio = hoje.plusDays(30);
        }
        Ciclo rascunho = ciclos.save(new Ciclo("Ciclo 2027.1", EnumSet.of(TipoAvaliador.CHEFE, TipoAvaliador.AUTO),
                inicio, inicio.plusDays(25)));
        montagem.salvarAvaliados(rascunho.getId(), List.of(), List.of(pessoa("colaborador").cargoId()), null,
                Map.of());

        log.info("Dados de exemplo: 3 ciclos (encerrado, aberto e rascunho); {} convites do ciclo aberto na caixa "
                + "de e-mails", envio.enviados());
        designacoes.findByCicloIdOrderById(aberto.getId()).stream().filter(Designacao::isExterna).findFirst()
                .ifPresent(d -> log.info("Link do avaliador externo (Marcos Tavares, avalia a Carla): {}",
                        convites.linkExterno(d.getToken())));
    }

    private PessoaResumo pessoa(String usuario) {
        return usuarios.buscarPorEmail(usuario + "@exemplo.com");
    }
}
