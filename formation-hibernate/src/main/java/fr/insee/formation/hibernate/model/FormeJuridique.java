package fr.insee.formation.hibernate.model;

public enum FormeJuridique {

	INDIV("Individuelle"), EURL("Entreprise Unipersonnelle à Responsabilité Limitée"), SASU("Société par Action Simplifiée Unipersonnelle"), SNC(
	        "Société en Nom Collectif"), SARL(
	                "Société A Responsabilité Limitée"), SA("Société Anonyme"), SAS("Société par Action Simplifiée"), SCA("Société en Commandite par Action");

	private String libelle;

	private FormeJuridique(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}

}
