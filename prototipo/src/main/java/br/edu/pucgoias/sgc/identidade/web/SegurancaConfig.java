package br.edu.pucgoias.sgc.identidade.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Controle de acesso no servidor. As regras por URL barram o perfil errado antes do controller; as regras por pessoa
 * e equipe (por exemplo, "o gestor só vê a própria equipe") ficam nos serviços, que conferem o usuário logado em toda
 * consulta. Senhas guardadas com BCrypt.
 */
@Configuration
@EnableMethodSecurity
public class SegurancaConfig {

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()
                // Avaliador externo: link individual, sem conta.
                .requestMatchers("/externo/**").permitAll()
                .requestMatchers("/competencias/**", "/ciclos/novo", "/auditoria", "/emails", "/pessoas",
                        "/moderacao/**", "/privacidade/solicitacoes/**").hasRole("RH")
                .requestMatchers("/observacoes/**").hasRole("GESTOR")
                .anyRequest().authenticated())
            .formLogin(f -> f
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?erro"))
            .logout(l -> l.logoutUrl("/sair").logoutSuccessUrl("/login?saiu"))
            .sessionManagement(s -> s.sessionFixation(f -> f.changeSessionId()));
        return http.build();
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
