package fr.insee.formation.hibernate.model.nomenclature;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractNiveauNomenclature {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	/*
	 * TP1 : Spécifier l'allocationSize pour économiser les allers-retours avec la
	 * BDD
	 */
	private int id;

	private String codeNaf;

	private String libelleNomenclature;

	private Double coeffRedressementNiveau;

	private Double coeffCalculIndice;

}