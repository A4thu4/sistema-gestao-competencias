package br.edu.pucgoias.sgc.avaliacoes.servico;

import br.edu.pucgoias.sgc.avaliacoes.dominio.Observacao;
import br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao;
import br.edu.pucgoias.sgc.avaliacoes.repositorio.ObservacaoRepository;
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
import java.time.LocalTime;
import java.time.OffsetDateTime;

import static br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao.MELHORIA;
import static br.edu.pucgoias.sgc.avaliacoes.dominio.TipoObservacao.POSITIVO;

/**
 * Dados de exemplo do pacote avaliacoes. Roda depois de identidade, privacidade, competências e ciclos e só com
 * {@code sgc.demo.carregar=true}. Cada parte é idempotente: só carrega quando a sua tabela está vazia.
 *
 * <ul>
 *   <li>UC-08: observações dos últimos cinco meses, com datas relativas a hoje. Bruno tem observações sobre toda a
 *       equipe, mas a do Fábio é antiga (mais de 60 dias), para o aviso da página inicial; Gustavo tem algumas sobre
 *       Igor e Júlia e nenhuma sobre a Kátia;</li>
 *   <li>UC-03 (respostas dos ciclos de exemplo): entra em {@link #carregarRespostas()} quando o pacote ciclos
 *       estiver pronto.</li>
 * </ul>
 */
@Component
@Order(40)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploAvaliacoes implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosExemploAvaliacoes.class);

    private final ObservacaoRepository observacoes;
    private final UsuarioService usuarios;

    public DadosExemploAvaliacoes(ObservacaoRepository observacoes, UsuarioService usuarios) {
        this.observacoes = observacoes;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        carregarObservacoes();
        carregarRespostas();
    }

    private void carregarObservacoes() {
        if (observacoes.count() > 0) {
            return;
        }
        String bruno = "gestor@exemplo.com";
        observar(bruno, "colaborador@exemplo.com", POSITIVO, 12, "Resolveu sozinha a queda do sistema de pedidos da "
                + "filial de Anápolis em menos de uma hora e deixou o passo a passo registrado na base de conhecimento.");
        observar(bruno, "colaborador@exemplo.com", MELHORIA, 48, "Encerrou três chamados sem confirmar com o cliente se "
                + "o problema tinha sido resolvido; dois foram reabertos no dia seguinte. Combinamos de sempre "
                + "confirmar antes de fechar.");
        observar(bruno, "colaborador@exemplo.com", POSITIVO, 103, "Conduziu o treinamento da nova versão do sistema de "
                + "ponto para 25 pessoas do financeiro, com boa avaliação dos participantes.");

        observar(bruno, "diego@exemplo.com", MELHORIA, 6, "Deixou um chamado urgente da diretoria sem resposta até o "
                + "dia seguinte porque não conferiu a fila antes de sair.");
        observar(bruno, "diego@exemplo.com", POSITIVO, 37, "Criou uma lista de conferência para a troca de "
                + "computadores que acabou com as visitas repetidas ao mesmo setor.");
        observar(bruno, "diego@exemplo.com", POSITIVO, 120, "Acompanhou o colega recém-chegado nas primeiras semanas e "
                + "explicou com paciência as rotinas de atendimento.");

        observar(bruno, "elisa@exemplo.com", POSITIVO, 21, "Recebeu elogio por escrito da coordenação do almoxarifado "
                + "pela clareza ao explicar a configuração das novas impressoras.");
        observar(bruno, "elisa@exemplo.com", MELHORIA, 66, "Na reunião de alinhamento, interrompeu os colegas várias "
                + "vezes. Conversamos depois e ela se comprometeu a esperar a vez de falar.");
        observar(bruno, "elisa@exemplo.com", POSITIVO, 94, "Assumiu o plantão de um colega doente no fim de semana "
                + "do fechamento contábil, sem nenhum chamado em atraso.");
        observar(bruno, "elisa@exemplo.com", MELHORIA, 140, "Ficou uma semana sem registrar no sistema os atendimentos "
                + "feitos por telefone, o que atrapalhou o relatório mensal da equipe.");

        observar(bruno, "fabio@exemplo.com", POSITIVO, 74, "Fez o inventário dos notebooks da área comercial antes do "
                + "prazo e encontrou quatro equipamentos sem número de patrimônio.");
        observar(bruno, "fabio@exemplo.com", MELHORIA, 131, "Chegou atrasado a dois plantões de sábado sem avisar a "
                + "equipe.");

        String gustavo = "gustavo@exemplo.com";
        observar(gustavo, "igor@exemplo.com", POSITIVO, 15, "Achou a causa da lentidão no relatório de vendas e "
                + "corrigiu a consulta: o tempo caiu de 40 para 3 segundos.");
        observar(gustavo, "igor@exemplo.com", MELHORIA, 55, "Publicou uma alteração sem a revisão de código de um "
                + "colega, o que causou um erro na tela de cadastro de clientes.");
        observar(gustavo, "julia@exemplo.com", POSITIVO, 28, "Escreveu a documentação da integração com o banco de "
                + "forma tão clara que o suporte passou a resolver as dúvidas sem chamar o desenvolvimento.");
        log.info("Dados de exemplo (avaliacoes): {} observações de gestores", observacoes.count());
    }

    /**
     * Respostas de exemplo dos ciclos (UC-03): avaliações enviadas do ciclo encerrado e rascunhos do ciclo aberto.
     * Entra quando o pacote ciclos estiver pronto; deve conferir a própria tabela ({@code avaliacao_enviada}) vazia.
     */
    private void carregarRespostas() {
        // UC-03: a preencher depois dos ciclos.
    }

    private void observar(String gestorEmail, String colaboradorEmail, TipoObservacao tipo, int diasAtras,
                          String descricao) {
        Long gestorId = usuarios.buscarPorEmail(gestorEmail).id();
        Long colaboradorId = usuarios.buscarPorEmail(colaboradorEmail).id();
        LocalDate dataFato = Calendario.hoje().minusDays(diasAtras);
        // Registrada no mesmo dia ou até dois dias depois do fato, em horário de expediente, nunca no futuro.
        OffsetDateTime criadaEm = dataFato.plusDays(diasAtras % 3)
                .atTime(LocalTime.of(9 + diasAtras % 8, (diasAtras * 7) % 60))
                .atZone(Calendario.ZONA).toOffsetDateTime();
        if (criadaEm.isAfter(Calendario.agora())) {
            criadaEm = Calendario.agora().minusMinutes(30);
        }
        observacoes.save(new Observacao(gestorId, colaboradorId, tipo, descricao, dataFato, criadaEm));
    }
}
