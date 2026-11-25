package br.edu.pucgoias.sgc.identidade.servico;

import br.edu.pucgoias.sgc.identidade.dominio.Cargo;
import br.edu.pucgoias.sgc.identidade.dominio.Perfil;
import br.edu.pucgoias.sgc.identidade.dominio.Usuario;
import br.edu.pucgoias.sgc.identidade.repositorio.CargoRepository;
import br.edu.pucgoias.sgc.identidade.repositorio.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Empresa de exemplo para a demonstração (fictícia). Roda só com o banco vazio e com {@code sgc.demo.carregar=true}.
 * Todas as contas usam a senha {@value #SENHA}.
 *
 * <pre>
 * Helena Prado (Gerente de Operações, gestora)
 * ├── Bruno Lima (Coordenador de Suporte, gestor) ── Carla, Diego, Elisa, Fábio (Analistas de Suporte)
 * └── Gustavo Reis (Coordenador de Desenvolvimento, gestor) ── Igor, Júlia, Kátia (Desenvolvedores)
 * Ana Souza (Analista de RH, perfil RH) ── gestora: Helena
 * </pre>
 */
@Component
@Order(10)
@ConditionalOnProperty(name = "sgc.demo.carregar", havingValue = "true")
public class DadosExemploIdentidade implements ApplicationRunner {

    public static final String SENHA = "senha123";
    private static final Logger log = LoggerFactory.getLogger(DadosExemploIdentidade.class);

    private final UsuarioRepository usuarios;
    private final CargoRepository cargos;
    private final PasswordEncoder codificador;

    public DadosExemploIdentidade(UsuarioRepository usuarios, CargoRepository cargos, PasswordEncoder codificador) {
        this.usuarios = usuarios;
        this.cargos = cargos;
        this.codificador = codificador;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarios.count() > 0) {
            return;
        }
        Map<String, Cargo> c = new HashMap<>();
        for (String nome : new String[]{"Gerente de Operações", "Analista de RH", "Coordenador de Suporte",
                "Analista de Suporte", "Coordenador de Desenvolvimento", "Desenvolvedor"}) {
            c.put(nome, cargos.save(new Cargo(nome)));
        }
        String senha = codificador.encode(SENHA);

        Usuario helena = salvar("Helena Prado", "helena@exemplo.com", senha, Perfil.GESTOR, c.get("Gerente de Operações"), null, "Operações");
        salvar("Ana Souza", "rh@exemplo.com", senha, Perfil.RH, c.get("Analista de RH"), helena, "Recursos Humanos");

        Usuario bruno = salvar("Bruno Lima", "gestor@exemplo.com", senha, Perfil.GESTOR, c.get("Coordenador de Suporte"), helena, "Suporte");
        salvar("Carla Dias", "colaborador@exemplo.com", senha, Perfil.COLABORADOR, c.get("Analista de Suporte"), bruno, "Suporte");
        salvar("Diego Rocha", "diego@exemplo.com", senha, Perfil.COLABORADOR, c.get("Analista de Suporte"), bruno, "Suporte");
        salvar("Elisa Martins", "elisa@exemplo.com", senha, Perfil.COLABORADOR, c.get("Analista de Suporte"), bruno, "Suporte");
        salvar("Fábio Nunes", "fabio@exemplo.com", senha, Perfil.COLABORADOR, c.get("Analista de Suporte"), bruno, "Suporte");

        Usuario gustavo = salvar("Gustavo Reis", "gustavo@exemplo.com", senha, Perfil.GESTOR, c.get("Coordenador de Desenvolvimento"), helena, "Desenvolvimento");
        salvar("Igor Matos", "igor@exemplo.com", senha, Perfil.COLABORADOR, c.get("Desenvolvedor"), gustavo, "Desenvolvimento");
        salvar("Júlia Campos", "julia@exemplo.com", senha, Perfil.COLABORADOR, c.get("Desenvolvedor"), gustavo, "Desenvolvimento");
        salvar("Kátia Freitas", "katia@exemplo.com", senha, Perfil.COLABORADOR, c.get("Desenvolvedor"), gustavo, "Desenvolvimento");
        log.info("Dados de exemplo: {} pessoas criadas (senha: {})", usuarios.count(), SENHA);
    }

    private Usuario salvar(String nome, String email, String senha, Perfil perfil, Cargo cargo, Usuario gestor, String area) {
        return usuarios.save(new Usuario(nome, email, senha, perfil, cargo, gestor, area));
    }
}
