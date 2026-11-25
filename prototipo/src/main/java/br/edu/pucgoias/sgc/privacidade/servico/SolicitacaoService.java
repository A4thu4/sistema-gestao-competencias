package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.privacidade.dominio.Protocolo;
import br.edu.pucgoias.sgc.privacidade.dominio.SituacaoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.dominio.SolicitacaoTitular;
import br.edu.pucgoias.sgc.privacidade.dominio.TipoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.repositorio.SolicitacaoTitularRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Solicitações do titular (acesso, correção e exclusão) e o atendimento pelo RH. O titular só vê as próprias
 * solicitações; a conferência é feita aqui, no servidor.
 */
@Service
@Transactional(readOnly = true)
public class SolicitacaoService {

    /** Campos que o titular pode apontar como incorretos (os dados pessoais mantidos). */
    public static final List<String> DADOS_CORRIGIVEIS = List.of("Nome", "E-mail", "Cargo", "Área", "Gestor direto");
    public static final String CANAL_ENCARREGADO = "encarregado@exemplo.com";

    private final SolicitacaoTitularRepository solicitacoes;
    private final UsuarioService usuarios;
    private final EmailService email;
    private final ApplicationEventPublisher eventos;
    private final ObjectProvider<ParticipacaoEmCiclo> participacao;

    public SolicitacaoService(SolicitacaoTitularRepository solicitacoes, UsuarioService usuarios, EmailService email,
                              ApplicationEventPublisher eventos, ObjectProvider<ParticipacaoEmCiclo> participacao) {
        this.solicitacoes = solicitacoes;
        this.usuarios = usuarios;
        this.email = email;
        this.eventos = eventos;
        this.participacao = participacao;
    }

    /** A exclusão pede confirmação quando a pessoa participa de um ciclo em andamento. */
    public boolean exclusaoPrecisaDeConfirmacao(Long usuarioId) {
        return participacao.orderedStream().anyMatch(p -> p.participaDeCicloAberto(usuarioId));
    }

    /**
     * Registra a solicitação com protocolo, envia a confirmação por e-mail e encaminha ao RH. O pedido de acesso já sai
     * atendido: a cópia dos dados é gerada na hora.
     */
    @Transactional
    public SolicitacaoResumo registrar(UsuarioAutenticado eu, NovaSolicitacao pedido) {
        return registrarPara(eu.id(), pedido);
    }

    @Transactional
    SolicitacaoResumo registrarPara(Long usuarioId, NovaSolicitacao pedido) {
        if (pedido.tipo() == null) {
            throw new RegraDeNegocioException("Escolha o tipo de solicitação.");
        }
        String descricao = limpar(pedido.descricao());
        String dadoIncorreto = null;
        String valorCorreto = null;
        switch (pedido.tipo()) {
            case ACESSO -> descricao = descricao == null ? "Pedido de cópia dos meus dados pessoais." : descricao;
            case CORRECAO -> {
                dadoIncorreto = limpar(pedido.dadoIncorreto());
                valorCorreto = limpar(pedido.valorCorreto());
                if (dadoIncorreto == null || valorCorreto == null) {
                    throw new RegraDeNegocioException("Diga qual dado está errado e qual é o valor correto.");
                }
                if (descricao == null) {
                    descricao = "Correção de " + dadoIncorreto.toLowerCase() + ".";
                }
            }
            case EXCLUSAO -> {
                if (exclusaoPrecisaDeConfirmacao(usuarioId) && !pedido.confirmouAvisoCiclo()) {
                    throw new RegraDeNegocioException("Confirme que entendeu o aviso sobre o ciclo em andamento.");
                }
                if (descricao == null) {
                    descricao = "Peço a exclusão dos meus dados pessoais.";
                }
            }
        }
        if (descricao.length() > 1000 || (valorCorreto != null && valorCorreto.length() > 200)
                || (dadoIncorreto != null && dadoIncorreto.length() > 200)) {
            throw new RegraDeNegocioException("O texto ficou longo demais. Resuma o pedido.");
        }

        OffsetDateTime agora = OffsetDateTime.now();
        SolicitacaoTitular s = solicitacoes.save(new SolicitacaoTitular(usuarioId, pedido.tipo(), descricao,
                dadoIncorreto, valorCorreto, agora));
        s.definirProtocolo(Protocolo.formatar(Datas.anoAtual(), s.getId()));
        if (pedido.tipo() == TipoSolicitacao.ACESSO) {
            s.concluir(SituacaoSolicitacao.ATENDIDA,
                    "Cópia dos dados gerada na hora pelo sistema, para ver na tela ou baixar em PDF.", null, agora);
        }

        PessoaResumo titular = usuarios.buscar(usuarioId);
        eventos.publishEvent(EventoAuditoria.de("SOLICITACAO_TITULAR",
                s.getProtocolo() + ": " + pedido.tipo().getRotulo()));
        email.enviar(titular.email(), "Recebemos a sua solicitação " + s.getProtocolo(), corpoConfirmacao(titular, s),
                "LGPD");
        return SolicitacaoResumo.de(s);
    }

    private String corpoConfirmacao(PessoaResumo titular, SolicitacaoTitular s) {
        String situacao = s.getTipo() == TipoSolicitacao.ACESSO
                ? "A cópia dos seus dados já está disponível em \"Meus dados\". A solicitação foi registrada como atendida."
                : "A solicitação está em análise pelo RH e pelo encarregado de dados. Avisaremos por e-mail quando houver resposta.";
        return "Olá, " + titular.nome() + ".\n\n"
                + "Recebemos a sua solicitação de " + s.getTipo().getRotulo().toLowerCase() + ".\n"
                + "Protocolo: " + s.getProtocolo() + "\n"
                + "Data e hora: " + Datas.dataHora(s.getCriadaEm()) + "\n\n"
                + situacao + "\n\nDúvidas: " + CANAL_ENCARREGADO + ".";
    }

    /** Solicitações do próprio titular, da mais recente à mais antiga. */
    public List<SolicitacaoResumo> doTitular(UsuarioAutenticado eu) {
        return solicitacoes.findByUsuarioIdOrderByCriadaEmDescIdDesc(eu.id()).stream()
                .map(SolicitacaoResumo::de).toList();
    }

    List<SolicitacaoResumo> doUsuario(Long usuarioId) {
        return solicitacoes.findByUsuarioIdOrderByCriadaEmDescIdDesc(usuarioId).stream()
                .map(SolicitacaoResumo::de).toList();
    }

    /** Se a solicitação for de outra pessoa, o acesso é negado. */
    public SolicitacaoResumo buscarDoTitular(UsuarioAutenticado eu, Long id) {
        SolicitacaoTitular s = carregar(id);
        if (!Objects.equals(s.getUsuarioId(), eu.id())) {
            throw new AccessDeniedException("Solicitação de outra pessoa.");
        }
        return SolicitacaoResumo.de(s);
    }

    /** RH: lista as solicitações, opcionalmente só de uma situação. */
    public List<SolicitacaoResumo> listar(UsuarioAutenticado eu, SituacaoSolicitacao situacao) {
        exigirRh(eu);
        List<SolicitacaoTitular> lista = situacao == null
                ? solicitacoes.findAllByOrderByCriadaEmDescIdDesc()
                : solicitacoes.findBySituacaoOrderByCriadaEmAscIdAsc(situacao);
        return lista.stream().map(SolicitacaoResumo::de).toList();
    }

    /** RH: uma solicitação para atendimento. */
    public SolicitacaoResumo buscarParaAtendimento(UsuarioAutenticado eu, Long id) {
        exigirRh(eu);
        return SolicitacaoResumo.de(carregar(id));
    }

    /** Nome e e-mail dos titulares e de quem atendeu, para a tela do RH. */
    public Map<Long, PessoaResumo> pessoas(Collection<Long> ids) {
        return usuarios.buscarPorIds(ids);
    }

    public long emAnalise() {
        return solicitacoes.countBySituacao(SituacaoSolicitacao.EM_ANALISE);
    }

    /** Texto sugerido para a justificativa de uma exclusão: o que sai e o que fica, e por quê. */
    public static String justificativaPadraoExclusao() {
        return "Excluímos o seu nome, o seu e-mail e o seu acesso ao sistema. "
                + "Mantivemos as respostas que já fazem parte dos resultados dos ciclos, porque estão agregadas "
                + "e não identificam ninguém, e o registro do seu aceite do termo e desta solicitação, porque a lei "
                + "exige guardar a prova do consentimento e do atendimento.";
    }

    /**
     * O RH registra a decisão com justificativa. Correção de nome atendida altera o cadastro; exclusão atendida (no todo
     * ou em parte) anonimiza a pessoa e bloqueia o acesso. O titular é avisado por e-mail antes da anonimização.
     */
    @Transactional
    public SolicitacaoResumo concluir(UsuarioAutenticado eu, Long id, SituacaoSolicitacao decisao, String justificativa) {
        exigirRh(eu);
        String texto = limpar(justificativa);
        if (texto == null) {
            throw new RegraDeNegocioException("Escreva a justificativa da decisão. Ela é enviada ao titular.");
        }
        if (texto.length() > 1000) {
            throw new RegraDeNegocioException("A justificativa passou de 1000 caracteres. Resuma o texto.");
        }
        SolicitacaoTitular s = carregar(id);
        s.concluir(decisao, texto, eu.id(), OffsetDateTime.now());

        PessoaResumo titular = usuarios.buscar(s.getUsuarioId());
        email.enviar(titular.email(), "Resposta à sua solicitação " + s.getProtocolo(),
                "Olá, " + titular.nome() + ".\n\n"
                        + "A sua solicitação de " + s.getTipo().getRotulo().toLowerCase() + " (protocolo "
                        + s.getProtocolo() + ") foi concluída.\n"
                        + "Situação: " + decisao.getRotulo() + "\n"
                        + "Justificativa: " + texto + "\n\nDúvidas: " + CANAL_ENCARREGADO + ".", "LGPD");

        boolean atendida = decisao == SituacaoSolicitacao.ATENDIDA || decisao == SituacaoSolicitacao.ATENDIDA_PARCIAL;
        String efeito = "";
        if (s.getTipo() == TipoSolicitacao.CORRECAO && decisao == SituacaoSolicitacao.ATENDIDA
                && "Nome".equalsIgnoreCase(s.getDadoIncorreto())) {
            usuarios.corrigirNome(s.getUsuarioId(), s.getValorCorreto());
            efeito = "; nome corrigido no cadastro";
        } else if (s.getTipo() == TipoSolicitacao.EXCLUSAO && atendida) {
            usuarios.anonimizar(s.getUsuarioId());
            efeito = "; titular anonimizado e acesso bloqueado";
        }
        eventos.publishEvent(EventoAuditoria.de("CONCLUSAO_SOLICITACAO_TITULAR",
                s.getProtocolo() + ": " + decisao.getRotulo() + efeito, texto));
        return SolicitacaoResumo.de(s);
    }

    private SolicitacaoTitular carregar(Long id) {
        return solicitacoes.findById(id).orElseThrow(() -> new NaoEncontradoException("Solicitação não encontrada."));
    }

    private static void exigirRh(UsuarioAutenticado eu) {
        if (!eu.ehRh()) {
            throw new AccessDeniedException("Só o RH atende as solicitações dos titulares.");
        }
    }

    private static String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.strip();
    }
}
