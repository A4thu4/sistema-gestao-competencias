package br.edu.pucgoias.sgc.compartilhado.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import br.edu.pucgoias.sgc.compartilhado.repositorio.RegistroAuditoriaRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Observador dos eventos de auditoria (DA-08). Grava na mesma transação de quem publicou: se a operação
 * falhar, o registro também é desfeito, e uma operação concluída nunca fica sem registro.
 */
@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private static final int POR_PAGINA = 50;
    private static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");

    private final RegistroAuditoriaRepository repositorio;

    public AuditoriaService(RegistroAuditoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @EventListener
    @Transactional
    public void registrar(EventoAuditoria evento) {
        if (evento.usuario() == null) {
            evento = evento.comUsuario(usuarioAtual());
        }
        repositorio.save(new RegistroAuditoria(evento));
        log.info("auditoria: {} {} {}", evento.usuario(), evento.acao(), evento.detalhe());
    }

    private static String usuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return "sistema";
        }
        return auth.getName();
    }

    @Transactional(readOnly = true)
    public Page<RegistroAuditoria> listar(int pagina) {
        return repositorio.findAllByOrderByOcorridoEmDescIdDesc(PageRequest.of(Math.max(pagina, 0), POR_PAGINA));
    }

    @Transactional(readOnly = true)
    public Page<RegistroAuditoria> listar(FiltroAuditoria filtro, int pagina) {
        Specification<RegistroAuditoria> spec = (raiz, consulta, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();
            if (!FiltroAuditoria.isBlank(filtro.usuario())) {
                condicoes.add(cb.like(cb.lower(raiz.get("usuario")),
                        "%" + filtro.usuario().trim().toLowerCase(Locale.ROOT) + "%"));
            }
            if (!FiltroAuditoria.isBlank(filtro.acao())) {
                condicoes.add(cb.equal(raiz.get("acao"), filtro.acao()));
            }
            if (filtro.de() != null) {
                condicoes.add(cb.greaterThanOrEqualTo(raiz.get("ocorridoEm"), inicioDoDia(filtro.de())));
            }
            if (filtro.ate() != null) {
                condicoes.add(cb.lessThan(raiz.get("ocorridoEm"), inicioDoDia(filtro.ate().plusDays(1))));
            }
            return cb.and(condicoes.toArray(new Predicate[0]));
        };
        return repositorio.findAll(spec, PageRequest.of(Math.max(pagina, 0), POR_PAGINA,
                Sort.by(Sort.Direction.DESC, "ocorridoEm", "id")));
    }

    @Transactional(readOnly = true)
    public List<String> acoes() {
        return repositorio.acoes();
    }

    private static OffsetDateTime inicioDoDia(LocalDate dia) {
        return dia.atStartOfDay(BRASILIA).toOffsetDateTime();
    }
}
