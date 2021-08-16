package fr.insee.formation.hibernate.model;

public enum TypeVoie {

	RUE("Rue"), AVENUE("Avenue"), BOULEVARD("Boulevard"), IMPASSE("Impasse");

	private String libelle;

	private TypeVoie(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}

}
