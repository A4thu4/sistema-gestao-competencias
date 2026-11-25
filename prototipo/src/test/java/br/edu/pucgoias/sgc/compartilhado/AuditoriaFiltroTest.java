package br.edu.pucgoias.sgc.compartilhado;

import br.edu.pucgoias.sgc.TesteIntegracao;
import br.edu.pucgoias.sgc.compartilhado.dominio.EventoAuditoria;
import br.edu.pucgoias.sgc.compartilhado.dominio.RegistroAuditoria;
import br.edu.pucgoias.sgc.compartilhado.servico.AuditoriaService;
import br.edu.pucgoias.sgc.compartilhado.servico.FiltroAuditoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditoriaFiltroTest extends TesteIntegracao {

    @Autowired
    private AuditoriaService auditoria;

    private final LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

    @BeforeEach
    void registros() {
        auditoria.registrar(new EventoAuditoria("teste.um@exemplo.com", "TESTE_FILTRO_A", "primeiro", null));
        auditoria.registrar(new EventoAuditoria("teste.dois@exemplo.com", "TESTE_FILTRO_B", "segundo", null));
    }

    @Test
    void filtraPorAcaoUsuarioEPeriodo() {
        List<RegistroAuditoria> porAcao = auditoria.listar(
                new FiltroAuditoria(null, "TESTE_FILTRO_A", null, null), 0).getContent();
        assertThat(porAcao).isNotEmpty().allMatch(r -> r.getAcao().equals("TESTE_FILTRO_A"));

        List<RegistroAuditoria> porUsuario = auditoria.listar(
                new FiltroAuditoria("TESTE.DOIS", null, hoje, hoje), 0).getContent();
        assertThat(porUsuario).extracting(RegistroAuditoria::getDetalhe).containsExactly("segundo");

        assertThat(auditoria.listar(new FiltroAuditoria("teste.", null, hoje.plusDays(1), null), 0)).isEmpty();
    }

    @Test
    void telaMostraAcaoComNomeDeTelaEFiltra() throws Exception {
        mvc.perform(get("/auditoria").param("acao", "TESTE_FILTRO_B").with(como("rh@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Teste filtro b")))
                .andExpect(content().string(containsString("segundo")))
                .andExpect(content().string(not(containsString("primeiro"))));
    }
}
