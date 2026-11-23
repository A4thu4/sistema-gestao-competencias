package br.edu.pucgoias.sgc.privacidade.web;

import br.edu.pucgoias.sgc.privacidade.servico.ConsentimentoService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra a barreira do termo em todas as rotas, menos as que precisam funcionar antes do aceite. */
@Configuration
public class PrivacidadeWebConfig implements WebMvcConfigurer {

    private final ConsentimentoService consentimento;

    public PrivacidadeWebConfig(ConsentimentoService consentimento) {
        this.consentimento = consentimento;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BarreiraDoTermo(consentimento))
                .addPathPatterns("/**")
                .excludePathPatterns("/privacidade/termo", "/sair", "/login", "/externo/**", "/error",
                        "/css/**", "/js/**", "/img/**", "/favicon.ico");
    }
}
