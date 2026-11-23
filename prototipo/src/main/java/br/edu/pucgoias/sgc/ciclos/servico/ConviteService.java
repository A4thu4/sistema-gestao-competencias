package br.edu.pucgoias.sgc.ciclos.servico;

import br.edu.pucgoias.sgc.ciclos.dominio.AvaliadorExterno;
import br.edu.pucgoias.sgc.ciclos.dominio.Ciclo;
import br.edu.pucgoias.sgc.ciclos.dominio.Designacao;
import br.edu.pucgoias.sgc.ciclos.repositorio.AvaliadorExternoRepository;
import br.edu.pucgoias.sgc.ciclos.repositorio.DesignacaoRepository;
import br.edu.pucgoias.sgc.compartilhado.servico.EmailService;
import br.edu.pucgoias.sgc.competencias.servico.TipoAvaliador;
import br.edu.pucgoias.sgc.identidade.servico.PessoaResumo;
import br.edu.pucgoias.sgc.identidade.servico.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Convites na abertura (UC-02, passo 14) e lembretes aos pendentes (UC-07, RF-12) pelo {@link EmailService}.
 * Cada pessoa da empresa recebe um e-mail por ciclo com todas as avaliações que tem a fazer (link para
 * {@code /avaliacoes}); cada avaliador externo recebe o seu link individual ({@code /externo/{token}}).
 *
 * <p>Envio que falhar deixa {@code convite_enviado_em} vazio: a rotina tenta de novo e o RH vê os pendentes para
 * reenviar (extensão 14a).
 */
@Service
@Transactional
public class ConviteService {

    private static final Logger log = LoggerFactory.getLogger(ConviteService.class);
    private static final int CORPO_MAXIMO = 3900;

    private final DesignacaoRepository designacoes;
    private final AvaliadorExternoRepository externos;
    private final UsuarioService usuarios;
    private final EmailService email;
    private final String urlBase;

    public ConviteService(DesignacaoRepository designacoes, AvaliadorExternoRepository externos,
                          UsuarioService usuarios, EmailService email,
                          @Value("${sgc.url-base:http://localhost:8080}") String urlBase) {
        this.designacoes = designacoes;
        this.externos = externos;
        this.usuarios = usuarios;
        this.email = email;
        this.urlBase = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
    }

    /** Resultado de um envio: quantas pessoas receberam e quantas falharam. */
    public record ResultadoEnvio(int enviados, int falhas, int jaLembradosHoje) {
    }

    public String linkExterno(String token) {
        return urlBase + "/externo/" + token;
    }

    /** Envia os convites ainda não enviados do ciclo (abertura, inclusão em ciclo aberto e reenvio). */
    public ResultadoEnvio enviarConvitesPendentes(Ciclo ciclo) {
        List<Designacao> pendentes = designacoes.findByCicloIdAndConviteEnviadoEmIsNullOrderById(ciclo.getId());
        return enviar(ciclo, pendentes, true);
    }

    /** Lembrete aos avaliadores com avaliações não enviadas, sem repetir no mesmo dia (RF-12). */
    public ResultadoEnvio lembrar(Ciclo ciclo, Collection<Designacao> alvo) {
        List<Designacao> pendentes = alvo.stream().filter(Designacao::isPendente).toList();
        return enviar(ciclo, pendentes, false);
    }

    private ResultadoEnvio enviar(Ciclo ciclo, List<Designacao> lista, boolean convite) {
        if (lista.isEmpty()) {
            return new ResultadoEnvio(0, 0, 0);
        }
        Set<Long> ids = new HashSet<>();
        lista.forEach(d -> {
            ids.add(d.getAvaliadoId());
            if (d.getAvaliadorId() != null) ids.add(d.getAvaliadorId());
        });
        Map<Long, PessoaResumo> pessoas = usuarios.buscarPorIds(ids);
        Map<Long, AvaliadorExterno> porExterno = externos.findByCicloId(ciclo.getId()).stream()
                .collect(Collectors.toMap(AvaliadorExterno::getId, Function.identity()));

        // Um e-mail por pessoa (todas as avaliações dela no ciclo); um por externo (link individual).
        Map<String, List<Designacao>> grupos = new LinkedHashMap<>();
        for (Designacao d : lista) {
            String chave = d.isExterna() ? "E" + d.getId() : "U" + d.getAvaliadorId();
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(d);
        }

        int enviados = 0;
        int falhas = 0;
        int jaLembrados = 0;
        OffsetDateTime agora = OffsetDateTime.now();
        for (List<Designacao> grupo : grupos.values()) {
            if (!convite && grupo.stream().allMatch(d -> Datas.ehHoje(d.getUltimoLembreteEm()))) {
                jaLembrados++;
                continue;
            }
            Designacao primeira = grupo.get(0);
            try {
                if (primeira.isExterna()) {
                    AvaliadorExterno e = porExterno.get(primeira.getAvaliadorExternoId());
                    PessoaResumo avaliado = pessoas.get(primeira.getAvaliadoId());
                    email.enviar(e.getEmail(), assunto(ciclo, convite),
                            corpoExterno(ciclo, e, avaliado, primeira, convite), convite ? "CONVITE" : "LEMBRETE");
                } else {
                    PessoaResumo avaliador = pessoas.get(primeira.getAvaliadorId());
                    email.enviar(avaliador.email(), assunto(ciclo, convite),
                            corpoInterno(ciclo, avaliador, grupo, pessoas, convite), convite ? "CONVITE" : "LEMBRETE");
                }
                for (Designacao d : grupo) {
                    if (convite) {
                        d.registrarConvite(agora);
                    } else {
                        d.registrarLembrete(agora);
                    }
                }
                enviados++;
            } catch (RuntimeException ex) {
                falhas++;
                log.warn("Falha ao enviar {} do ciclo {} (designação {}): {}", convite ? "convite" : "lembrete",
                        ciclo.getId(), primeira.getId(), ex.getMessage());
            }
        }
        return new ResultadoEnvio(enviados, falhas, jaLembrados);
    }

    private static String assunto(Ciclo c, boolean convite) {
        return convite ? "Convite para avaliação — " + c.getNome()
                : "Lembrete: avaliação pendente — " + c.getNome();
    }

    private String corpoInterno(Ciclo c, PessoaResumo avaliador, List<Designacao> grupo,
                                Map<Long, PessoaResumo> pessoas, boolean convite) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá, ").append(primeiroNome(avaliador.nome())).append(".\n\n");
        if (convite) {
            sb.append("O ciclo de avaliação “").append(c.getNome()).append("” está aberto e você foi convidado(a) a ")
                    .append(grupo.size() == 1 ? "fazer 1 avaliação" : "fazer " + grupo.size() + " avaliações")
                    .append(":\n");
        } else {
            sb.append("Ainda falta enviar ")
                    .append(grupo.size() == 1 ? "1 avaliação" : grupo.size() + " avaliações")
                    .append(" do ciclo “").append(c.getNome()).append("”:\n");
        }
        for (Designacao d : grupo) {
            sb.append("- ");
            if (d.getRelacao() == TipoAvaliador.AUTO) {
                sb.append("Você mesmo(a) (autoavaliação)");
            } else {
                PessoaResumo avaliado = pessoas.get(d.getAvaliadoId());
                sb.append(avaliado == null ? "pessoa" : avaliado.nome()).append(" (")
                        .append(d.getRelacao().rotulo().toLowerCase()).append(")");
            }
            sb.append('\n');
        }
        sb.append("\nPrazo: até ").append(Datas.data(c.getDataEncerramento())).append(".\n");
        sb.append("Para responder, entre no sistema: ").append(urlBase).append("/avaliacoes\n\n");
        sb.append("As respostas de pares, subordinados e externos são anônimas: ninguém vê quem respondeu o quê.");
        return cortar(sb.toString());
    }

    private String corpoExterno(Ciclo c, AvaliadorExterno e, PessoaResumo avaliado, Designacao d, boolean convite) {
        String nomeAvaliado = avaliado == null ? "um profissional" : avaliado.nome();
        StringBuilder sb = new StringBuilder();
        sb.append("Olá, ").append(primeiroNome(e.getNome())).append(".\n\n");
        if (convite) {
            sb.append("Você foi convidado(a) a avaliar ").append(nomeAvaliado)
                    .append(" no ciclo de avaliação “").append(c.getNome()).append("”. ")
                    .append("Não é preciso criar conta: use o seu link individual abaixo.\n\n");
        } else {
            sb.append("Lembramos que a sua avaliação de ").append(nomeAvaliado)
                    .append(" ainda não foi enviada.\n\n");
        }
        sb.append("Link: ").append(linkExterno(d.getToken())).append("\n");
        sb.append("O link vale até o envio da avaliação ou até ").append(Datas.data(c.getDataEncerramento()))
                .append(". Não o repasse a outras pessoas.\n\n");
        sb.append("Suas respostas são anônimas e usamos só o seu nome e e-mail para este convite.");
        return cortar(sb.toString());
    }

    private static String primeiroNome(String nome) {
        return nome == null ? "" : nome.trim().split("\\s+")[0];
    }

    private static String cortar(String s) {
        return s.length() <= CORPO_MAXIMO ? s : s.substring(0, CORPO_MAXIMO) + "…";
    }
}
