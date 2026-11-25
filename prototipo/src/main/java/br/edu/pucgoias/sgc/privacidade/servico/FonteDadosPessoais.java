package br.edu.pucgoias.sgc.privacidade.servico;

import java.util.List;

/**
 * Ponto de extensão para a cópia dos dados pessoais. Cada pacote que guarda dados sobre uma pessoa
 * (por exemplo, observações do gestor, metas do plano de desenvolvimento e resultados consolidados) oferece uma
 * implementação {@code @Component}; o privacidade junta todas com {@code ObjectProvider}, sem depender desses
 * pacotes (inversão de dependência). Use {@code @Order} para definir a ordem das seções.
 *
 * <p><strong>Anonimato:</strong> as linhas nunca podem identificar quem respondeu avaliações de pares,
 * subordinados ou externos. Resultados entram só agregados; respostas que o próprio titular deu como avaliador
 * anônimo não entram.
 */
public interface FonteDadosPessoais {

    /** Título da seção na cópia, por exemplo "Observações do gestor". */
    String titulo();

    /** Uma linha por registro, em linguagem simples. Lista vazia quando não há nada sobre a pessoa. */
    List<String> itens(Long usuarioId);
}
