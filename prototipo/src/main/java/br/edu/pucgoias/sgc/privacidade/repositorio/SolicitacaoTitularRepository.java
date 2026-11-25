package br.edu.pucgoias.sgc.privacidade.repositorio;

import br.edu.pucgoias.sgc.privacidade.dominio.SituacaoSolicitacao;
import br.edu.pucgoias.sgc.privacidade.dominio.SolicitacaoTitular;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoTitularRepository extends JpaRepository<SolicitacaoTitular, Long> {

    List<SolicitacaoTitular> findByUsuarioIdOrderByCriadaEmDescIdDesc(Long usuarioId);

    List<SolicitacaoTitular> findAllByOrderByCriadaEmDescIdDesc();

    List<SolicitacaoTitular> findBySituacaoOrderByCriadaEmAscIdAsc(SituacaoSolicitacao situacao);

    long countBySituacao(SituacaoSolicitacao situacao);
}
