package fr.insee.formation.hibernate.model;

import java.time.Instant;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Indice {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	/*
	 * TP1 : Spécifier l'allocationSize pour économiser les allers-retours avec la
	 * BDD
	 */
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private SousClasse sousClasse;

	private Double valeur;

	private Instant derniereMaj;

}
