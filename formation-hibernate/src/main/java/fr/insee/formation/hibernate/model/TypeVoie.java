package fr.insee.formation.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TypeVoie {

	//// @formatter:off
	RUE("Rue"),
	PLACE("Place"),
	AVENUE("Avenue"),
	BOULEVARD("Boulevard"),
	IMPASSE("Impasse"),
	QUAI("Quai"),
	PASSAGE("Passage")
	;
	// @formatter:on

	@Getter
	private String libelle;

}
