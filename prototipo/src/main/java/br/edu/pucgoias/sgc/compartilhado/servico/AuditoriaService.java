package br.edu.pucgoias.sgc.compartilhado.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import br.edu.pucgoias.sgc.compartilhado.repositorio.RegistroAuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Observador dos eventos de auditoria (DA-08). Grava na mesma transação de quem publicou: se a operação
 * falhar, o registro também é desfeito, e uma operação concluída nunca fica sem registro.
 */
@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final RegistroAuditoriaRepository repositorio;

    public AuditoriaService(RegistroAuditoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @EventListener
    @Transactional
    public void registrar(EventoAuditoria evento) {
        repositorio.save(new RegistroAuditoria(evento));
        log.info("auditoria: {} {} {}", evento.usuario(), evento.acao(), evento.detalhe());
    }

    @Transactional(readOnly = true)
    public Page<RegistroAuditoria> listar(int pagina) {
        return repositorio.findAllByOrderByOcorridoEmDescIdDesc(PageRequest.of(Math.max(pagina, 0), 50));
    }
}
