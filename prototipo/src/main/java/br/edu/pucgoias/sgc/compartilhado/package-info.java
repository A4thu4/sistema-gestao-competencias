/**
 * Itens usados por todos os pacotes: configuração, tratamento de erros e log de auditoria (RNF-05). Não depende de nenhum pacote de negócio.
 *
 * <p>Camadas dentro do pacote:
 * <ul>
 *   <li>{@code web}: controllers (rotas e telas Thymeleaf);</li>
 *   <li>{@code servico}: regras do caso de uso;</li>
 *   <li>{@code dominio}: entidades JPA e objetos de valor;</li>
 *   <li>{@code repositorio}: interfaces Spring Data.</li>
 * </ul>
 * <p>Responsável: Arthur Mamedes Borges (#60).
 */
package br.edu.pucgoias.sgc.compartilhado;
