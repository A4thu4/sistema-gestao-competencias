package br.edu.pucgoias.sgc.compartilhado.servico;

import br.edu.pucgoias.sgc.compartilhado.dominio.EmailEnviado;
import br.edu.pucgoias.sgc.compartilhado.repositorio.EmailEnviadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de e-mail do protótipo. O envio real está fora do escopo (README do protótipo); cada mensagem é gravada
 * numa caixa de saída consultada pelo RH e escrita no log. Trocar por um envio SMTP só muda esta classe.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailEnviadoRepository repositorio;

    public EmailService(EmailEnviadoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public void enviar(String para, String assunto, String corpo, String tipo) {
        repositorio.save(new EmailEnviado(para, assunto, corpo, tipo));
        log.info("e-mail [{}] para {}: {}", tipo, para, assunto);
    }

    @Transactional(readOnly = true)
    public Page<EmailEnviado> listar(int pagina) {
        return repositorio.findAllByOrderByCriadoEmDescIdDesc(PageRequest.of(Math.max(pagina, 0), 50));
    }

    @Transactional(readOnly = true)
    public List<EmailEnviado> enviadosPara(String email) {
        return repositorio.findByParaOrderByIdDesc(email);
    }
}
