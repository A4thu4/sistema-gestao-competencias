package br.edu.pucgoias.sgc.resultados;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.compartilhado.servico.AuditoriaService;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaResumo;
import br.edu.pucgoias.sgc.competencias.servico.CompetenciaService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.resultados.dominio.ResultadoConsolidado;
import br.edu.pucgoias.sgc.resultados.repositorio.ResultadoConsolidadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.AUTO;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.CHEFE;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.EXTERNO;
import static br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador.PAR;

/**
 * Cenário dos testes de resultados: o consolidado do "Ciclo 2026.1" (encerrado) é gravado direto na tabela, com
 * grupos de 1 e 2 respostas que precisam sumir (RNF-02). Médias "marcadas" que nunca podem aparecer:
 * pares da Carla em Comunicação = 1,37 (2 respostas) e externos do Diego = 1,93 (1 resposta).
 *
 * <p>Carla: Atendimento ao cliente 4,63 (ponto forte), Comunicação 4,00, Trabalho em equipe 3,40 (foco);
 * média geral 4,01.
 */
abstract class CenarioResultados extends TesteIntegracao {

    static final String PAR_OCULTO = "1.37";
    static final String PAR_OCULTO_TELA = "1,4";
    static final String EXTERNO_OCULTO = "1.93";

    @Autowired
    protected ResultadoConsolidadoRepository consolidado;
    @Autowired
    protected JdbcTemplate jdbc;
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    protected UsuarioService usuarios;
    @Autowired
    protected CompetenciaService competencias;
    @Autowired
    protected EmailService emails;
    @Autowired
    protected AuditoriaService auditoria;

    protected Long ciclo;
    protected PessoaResumo carla;
    protected PessoaResumo diego;
    protected PessoaResumo igor;
    protected PessoaResumo bruno;
    protected PessoaResumo gustavo;
    protected Long atendimento;
    protected Long comunicacao;
    protected Long trabalho;

    @BeforeEach
    void montarCenario() {
        ciclo = idDoCiclo("Ciclo 2026.1");
        carla = usuarios.buscarPorEmail("colaborador@exemplo.com");
        diego = usuarios.buscarPorEmail("diego@exemplo.com");
        igor = usuarios.buscarPorEmail("igor@exemplo.com");
        bruno = usuarios.buscarPorEmail("gestor@exemplo.com");
        gustavo = usuarios.buscarPorEmail("gustavo@exemplo.com");
        Map<String, Long> porNome = competencias.competenciasAtivas().stream()
                .collect(Collectors.toMap(CompetenciaResumo::nome, CompetenciaResumo::id, (a, b) -> a));
        atendimento = porNome.get("Atendimento ao cliente");
        comunicacao = porNome.get("Comunicação");
        trabalho = porNome.get("Trabalho em equipe");

        consolidado.apagarDoCiclo(ciclo);
        consolidado.apagarDoCiclo(idDoCiclo("Ciclo 2025.2")); // cenário sem histórico
        consolidado.saveAll(List.of(
                linha(carla, atendimento, AUTO, "4.00", 1), linha(carla, atendimento, CHEFE, "4.50", 1),
                linha(carla, atendimento, PAR, "4.75", 3),
                linha(carla, comunicacao, AUTO, "3.00", 1), linha(carla, comunicacao, CHEFE, "4.00", 1),
                linha(carla, comunicacao, PAR, PAR_OCULTO, 2),
                linha(carla, trabalho, AUTO, "5.00", 1), linha(carla, trabalho, CHEFE, "3.20", 1),
                linha(carla, trabalho, PAR, "3.60", 4),
                linha(diego, atendimento, CHEFE, "3.00", 1), linha(diego, atendimento, PAR, "3.33", 3),
                linha(diego, comunicacao, CHEFE, "4.00", 1), linha(diego, comunicacao, EXTERNO, EXTERNO_OCULTO, 1),
                linha(igor, comunicacao, CHEFE, "4.20", 1), linha(igor, comunicacao, PAR, "4.00", 3)));
        liberado(true);
    }

    /** Muda a liberação do ciclo 2026.1 direto no banco (a transação do teste desfaz no final). */
    protected void liberado(boolean liberado) {
        em.flush();
        jdbc.update("update ciclo set resultado_liberado = ?, liberado_em = " + (liberado ? "now()" : "null")
                + " where id = ?", liberado, ciclo);
        em.clear();
    }

    protected Long idDoCiclo(String nome) {
        return jdbc.queryForObject("select id from ciclo where nome = ?", Long.class, nome);
    }

    protected ResultadoConsolidado linha(PessoaResumo p, Long competencia, TipoAvaliador grupo, String media, int n) {
        return linha(ciclo, p, competencia, grupo, media, n, 5);
    }

    protected static ResultadoConsolidado linha(Long ciclo, PessoaResumo p, Long competencia, TipoAvaliador grupo,
                                                String media, int n, int escala) {
        return new ResultadoConsolidado(ciclo, p.id(), competencia, grupo, new BigDecimal(media), n, escala);
    }

    protected List<String> acoesAuditadas() {
        return auditoria.listar(0).getContent().stream().map(r -> r.getAcao() + " " + r.getDetalhe()).toList();
    }

    protected long emailsPara(String email, String trechoDoAssunto) {
        return emails.enviadosPara(email).stream().filter(e -> e.getAssunto().contains(trechoDoAssunto)).count();
    }
}
