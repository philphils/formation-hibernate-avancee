package fr.insee.formation.hibernate.model;

import java.time.Instant;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;

import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Indice {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	@SequenceGenerator(name = "hib_seq", sequenceName = "hib_seq", allocationSize = 100)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private SousClasse sousClasse;

	private Double valeur;

	private Instant derniereMaj;

}
