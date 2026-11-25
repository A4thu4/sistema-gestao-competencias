package br.edu.pucgoias.sgc.privacidade.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.NaoEncontradoException;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegraDeNegocioException;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioAutenticado;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import br.edu.pucgoias.sgc.privacidade.dominio.Aceite;
import br.edu.pucgoias.sgc.privacidade.dominio.TermoConsentimento;
import br.edu.pucgoias.sgc.privacidade.repositorio.AceiteRepository;
import br.edu.pucgoias.sgc.privacidade.repositorio.TermoConsentimentoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Termo de consentimento vigente, aceite e publicação de nova versão pelo RH.
 *
 * <p>O id do termo vigente fica guardado em memória para a barreira do termo não ir ao banco a cada requisição;
 * a publicação de uma nova versão apaga essa memória ao terminar a transação (com sucesso ou não).
 */
@Service
@Transactional(readOnly = true)
public class ConsentimentoService {

    private final TermoConsentimentoRepository termos;
    private final AceiteRepository aceites;
    private final UsuarioService usuarios;
    private final EmailService email;
    private final ApplicationEventPublisher eventos;

    private final AtomicReference<Optional<Long>> vigenteEmMemoria = new AtomicReference<>();

    public ConsentimentoService(TermoConsentimentoRepository termos, AceiteRepository aceites,
                                UsuarioService usuarios, EmailService email, ApplicationEventPublisher eventos) {
        this.termos = termos;
        this.aceites = aceites;
        this.usuarios = usuarios;
        this.email = email;
        this.eventos = eventos;
    }

    public Optional<TermoResumo> termoVigente() {
        return termos.findFirstByOrderByVigenteDesdeDescIdDesc().map(TermoResumo::de);
    }

    /** Id da versão vigente, sem ir ao banco depois da primeira consulta. Vazio quando o RH ainda não publicou. */
    public Optional<Long> idTermoVigente() {
        Optional<Long> id = vigenteEmMemoria.get();
        if (id == null) {
            id = termos.findFirstByOrderByVigenteDesdeDescIdDesc().map(TermoConsentimento::getId);
            vigenteEmMemoria.set(id);
        }
        return id;
    }

    public boolean aceitou(Long usuarioId, Long termoId) {
        return aceites.existsByUsuarioIdAndTermo_Id(usuarioId, termoId);
    }

    /** Verdadeiro quando a pessoa já aceitou a versão vigente (ou quando ainda não há termo publicado). */
    public boolean aceitouVigente(Long usuarioId) {
        return idTermoVigente().map(id -> aceitou(usuarioId, id)).orElse(true);
    }

    public Optional<AceiteResumo> ultimoAceite(Long usuarioId) {
        return aceites.findFirstByUsuarioIdOrderByAceitoEmDescIdDesc(usuarioId).map(AceiteResumo::de);
    }

    /** Histórico de aceites da pessoa, do mais recente ao mais antigo. */
    public List<AceiteResumo> aceites(Long usuarioId) {
        return aceites.findByUsuarioIdOrderByAceitoEmDescIdDesc(usuarioId).stream().map(AceiteResumo::de).toList();
    }

    /** Registra o aceite com versão, data e hora e libera o sistema. */
    @Transactional
    public void aceitar(UsuarioAutenticado eu, Long termoId) {
        TermoConsentimento vigente = termos.findFirstByOrderByVigenteDesdeDescIdDesc()
                .orElseThrow(() -> new NaoEncontradoException("Ainda não há termo de consentimento publicado."));
        if (!vigente.getId().equals(termoId)) {
            throw new RegraDeNegocioException("O termo foi atualizado enquanto você lia. Leia a versão nova e aceite de novo.");
        }
        if (aceites.existsByUsuarioIdAndTermo_Id(eu.id(), termoId)) {
            return;
        }
        aceites.save(new Aceite(eu.id(), vigente, OffsetDateTime.now()));
        eventos.publishEvent(EventoAuditoria.de("ACEITE_TERMO",
                "Aceitou o termo de consentimento versão " + vigente.getVersao(), "RF-29"));
    }

    /** A recusa não libera nada; fica só o registro no log. */
    @Transactional
    public void registrarRecusa(UsuarioAutenticado eu) {
        String versao = termoVigente().map(TermoResumo::versao).orElse("—");
        eventos.publishEvent(EventoAuditoria.de("RECUSA_TERMO",
                "Não aceitou o termo de consentimento versão " + versao, "RF-29: acesso continua bloqueado"));
    }

    public List<TermoResumo> versoes() {
        return termos.findAllByOrderByVigenteDesdeDescIdDesc().stream().map(TermoResumo::de).toList();
    }

    /** Quantas pessoas ativas já aceitaram a versão vigente. */
    public long aceitesDoVigente() {
        Optional<Long> id = idTermoVigente();
        if (id.isEmpty()) {
            return 0;
        }
        Set<Long> aceitaram = new HashSet<>();
        aceites.findByTermo_Id(id.get()).forEach(a -> aceitaram.add(a.getUsuarioId()));
        return usuarios.listarAtivos().stream().filter(p -> aceitaram.contains(p.id())).count();
    }

    public long pessoasAtivas() {
        return usuarios.listarAtivos().size();
    }

    /** No próximo acesso, todos precisam aceitar a nova versão, vendo o resumo do que mudou. */
    @Transactional
    public TermoResumo publicar(UsuarioAutenticado eu, String versao, String texto, String resumoMudancas) {
        if (!eu.ehRh()) {
            throw new AccessDeniedException("Só o RH publica o termo.");
        }
        String v = versao == null ? "" : versao.strip();
        if (v.isEmpty() || texto == null || texto.isBlank()) {
            throw new RegraDeNegocioException("Informe a versão e o texto do termo.");
        }
        if (termos.existsByVersaoIgnoreCase(v)) {
            throw new RegraDeNegocioException("Já existe um termo com a versão " + v + ". Use outro número de versão.");
        }
        boolean primeira = termos.count() == 0;
        String resumo = resumoMudancas == null || resumoMudancas.isBlank() ? null : resumoMudancas.strip();
        if (!primeira && resumo == null) {
            throw new RegraDeNegocioException("Conte, em poucas linhas, o que mudou em relação à versão anterior.");
        }
        TermoConsentimento termo = termos.save(new TermoConsentimento(v, texto.strip(), resumo, OffsetDateTime.now()));
        esquecerVigente();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    esquecerVigente();
                }
            });
        }
        eventos.publishEvent(EventoAuditoria.de("PUBLICACAO_TERMO",
                "Publicou o termo de consentimento versão " + v, primeira ? "Primeira versão" : resumo));
        for (PessoaResumo p : usuarios.listarAtivos()) {
            email.enviar(p.email(), "Nova versão do termo de consentimento",
                    "Olá, " + p.nome() + ".\n\nO termo de consentimento do Sistema de Gestão de Competências mudou "
                            + "(versão " + v + "). No seu próximo acesso, o sistema vai mostrar o que mudou e pedir "
                            + "o seu aceite.\n\nDúvidas: encarregado@exemplo.com.", "TERMO");
        }
        return TermoResumo.de(termo);
    }

    /** Apaga a memória do termo vigente; a próxima consulta vai ao banco. */
    void esquecerVigente() {
        vigenteEmMemoria.set(null);
    }
}
