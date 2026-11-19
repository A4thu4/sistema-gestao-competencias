/**
 * Login, perfis (RH, GESTOR, COLABORADOR), usuários, cargos e equipes.
 *
 * <p>Camadas dentro do pacote:
 * <ul>
 *   <li>{@code web}: controllers (rotas e telas Thymeleaf);</li>
 *   <li>{@code servico}: regras do caso de uso;</li>
 *   <li>{@code dominio}: entidades JPA e objetos de valor;</li>
 *   <li>{@code repositorio}: interfaces Spring Data.</li>
 * </ul>
 * <p>Migrações: V1_x em {@code src/main/resources/db/migration}.
 * <p>Responsável: Rikelmy Andrade (#55).
 */
package br.edu.pucgoias.sgc.identidade;
