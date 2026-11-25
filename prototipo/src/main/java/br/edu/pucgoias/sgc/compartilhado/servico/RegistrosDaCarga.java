package br.edu.pucgoias.sgc.compartilhado.servico;

import br.edu.pucgoias.sgc.compartilhado.repositorio.EmailEnviadoRepository;
import br.edu.pucgoias.sgc.compartilhado.repositorio.RegistroAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Usado só pelas cargas de exemplo: dá aos registros de auditoria e aos e-mails gerados na carga a data em que o
 * fato teria acontecido (abertura ou liberação do ciclo), em vez da hora em que a aplicação subiu.
 */
@Service
public class RegistrosDaCarga {

    public record Marca(long auditoria, long email) {
    }

    private final RegistroAuditoriaRepository auditoria;
    private final EmailEnviadoRepository emails;

    public RegistrosDaCarga(RegistroAuditoriaRepository auditoria, EmailEnviadoRepository emails) {
        this.auditoria = auditoria;
        this.emails = emails;
    }

    @Transactional
    public Marca marcar() {
        return new Marca(auditoria.ultimoId(), emails.ultimoId());
    }

    /** Data os registros criados depois da marca. */
    @Transactional
    public void datar(Marca desde, OffsetDateTime quando) {
        auditoria.datarDepoisDe(desde.auditoria(), quando);
        emails.datarDepoisDe(desde.email(), quando);
    }
}
