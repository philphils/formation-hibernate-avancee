package fr.insee.formation.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FormeJuridique {

	//// @formatter:off
	INDIV("Individuelle"), 
	EURL("Entreprise Unipersonnelle à Responsabilité Limitée"),
	SASU("Société par Action Simplifiée Unipersonnelle"), 
	SNC("Société en Nom Collectif"),
	SARL("Société A Responsabilité Limitée"), 
	SA("Société Anonyme"), 
	SAS("Société par Action Simplifiée"),
	SCA("Société en Commandite par Action");
	// @formatter:on

	@Getter
	private String libelle;

}
