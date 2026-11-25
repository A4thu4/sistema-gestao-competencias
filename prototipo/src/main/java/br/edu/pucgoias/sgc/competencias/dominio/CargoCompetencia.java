package br.edu.pucgoias.sgc.competencias.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

/** Competência do conjunto padrão de um cargo. O cargo pertence ao pacote identidade, por isso aqui fica só o id. */
@Entity
@Table(name = "cargo_competencia")
@IdClass(CargoCompetencia.Chave.class)
public class CargoCompetencia {

    @Id
    private Long cargoId;
    @Id
    private Long competenciaId;

    protected CargoCompetencia() {
    }

    public CargoCompetencia(Long cargoId, Long competenciaId) {
        this.cargoId = cargoId;
        this.competenciaId = competenciaId;
    }

    public Long getCargoId() { return cargoId; }
    public Long getCompetenciaId() { return competenciaId; }

    /** Chave composta (cargo, competência). */
    public static class Chave implements Serializable {
        private Long cargoId;
        private Long competenciaId;

        public Chave() {
        }

        public Chave(Long cargoId, Long competenciaId) {
            this.cargoId = cargoId;
            this.competenciaId = competenciaId;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Chave c && Objects.equals(cargoId, c.cargoId) && Objects.equals(competenciaId, c.competenciaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cargoId, competenciaId);
        }
    }
}
