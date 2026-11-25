package br.edu.pucgoias.sgc.privacidade.repositorio;

import br.edu.pucgoias.sgc.privacidade.dominio.TermoConsentimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermoConsentimentoRepository extends JpaRepository<TermoConsentimento, Long> {

    /** A versão vigente é a publicada por último. */
    Optional<TermoConsentimento> findFirstByOrderByVigenteDesdeDescIdDesc();

    List<TermoConsentimento> findAllByOrderByVigenteDesdeDescIdDesc();

    boolean existsByVersaoIgnoreCase(String versao);
}
