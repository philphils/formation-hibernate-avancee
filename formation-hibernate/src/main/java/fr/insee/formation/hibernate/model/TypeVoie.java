package fr.insee.formation.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TypeVoie {

	//// @formatter:off
	RUE("Rue"),
	AVENUE("Avenue"),
	BOULEVARD("Boulevard"),
	IMPASSE("Impasse");
	// @formatter:on

	@Getter
	private String libelle;

}
