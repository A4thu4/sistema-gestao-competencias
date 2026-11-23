package br.edu.pucgoias.sgc.identidade.servico;

import java.util.List;

/**
 * Ponto de extensão da página inicial. Cada pacote de negócio oferece os seus cartões implementando esta
 * interface; o identidade só os junta. Assim o identidade não depende dos outros pacotes (inversão de dependência).
 */
public interface ContribuicaoPainel {

    List<CartaoPainel> cartoes(UsuarioAutenticado usuario);
}
