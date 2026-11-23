package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.privacidade.dominio.Aceite;
import br.edu.pucgoias.sgc.privacidade.dominio.TermoConsentimento;
import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.repositorio.AceiteRepository;
import br.edu.pucgoias.sgc.privacidade.repositorio.TermoConsentimentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Dados de exemplo do UC-05: termo versão 1.0 vigente, aceite de todas as pessoas menos a Kátia (ela demonstra o
 * primeiro acesso) e um pedido de correção da Carla em análise, para o RH atender na demonstração.
 */
@Component
@Order(15)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploPrivacidade implements ApplicationRunner {

    static final String SEM_ACEITE = "katia@exemplo.com";
    private static final Logger log = LoggerFactory.getLogger(DadosExemploPrivacidade.class);

    private final TermoConsentimentoRepository termos;
    private final AceiteRepository aceites;
    private final UsuarioService usuarios;
    private final SolicitacaoService solicitacoes;
    private final ConsentimentoService consentimento;

    public DadosExemploPrivacidade(TermoConsentimentoRepository termos, AceiteRepository aceites,
                                   UsuarioService usuarios, SolicitacaoService solicitacoes,
                                   ConsentimentoService consentimento) {
        this.termos = termos;
        this.aceites = aceites;
        this.usuarios = usuarios;
        this.solicitacoes = solicitacoes;
        this.consentimento = consentimento;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (termos.count() > 0) {
            return;
        }
        OffsetDateTime inicio = OffsetDateTime.now().minusDays(40).withHour(9).withMinute(0).withSecond(0).withNano(0);
        TermoConsentimento termo = termos.save(new TermoConsentimento("1.0", TextoTermo.VERSAO_1, null, inicio));
        consentimento.esquecerVigente();

        int i = 0;
        for (PessoaResumo p : usuarios.listarAtivos()) {
            if (p.email().equalsIgnoreCase(SEM_ACEITE)) {
                continue;
            }
            aceites.save(new Aceite(p.id(), termo, inicio.plusDays(1 + i % 5).plusMinutes(17L * i)));
            i++;
        }

        PessoaResumo carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        solicitacoes.registrarPara(carla.id(), new NovaSolicitacao(TipoSolicitacao.CORRECAO,
                "Meu nome está sem o sobrenome de casada. O certo é Carla Dias Moreira.",
                "Nome", "Carla Dias Moreira", false));
        log.info("Dados de exemplo (privacidade): termo 1.0, {} aceites, 1 solicitação em análise", i);
    }
}
