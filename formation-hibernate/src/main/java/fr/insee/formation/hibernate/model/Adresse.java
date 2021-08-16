package fr.insee.formation.hibernate.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Adresse {

	private String numero;

	@Enumerated(EnumType.STRING)
	private TypeVoie typeVoie;

	@Column(name = "rue")
	private String nomVoie;

	private String ville;

	private String pays;

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public TypeVoie getTypeVoie() {
		return typeVoie;
	}

	public void setTypeVoie(TypeVoie typeVoie) {
		this.typeVoie = typeVoie;
	}

	public String getNomVoie() {
		return nomVoie;
	}

	public void setNomVoie(String nomVoie) {
		this.nomVoie = nomVoie;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getPays() {
		return pays;
	}

	public void setPays(String pays) {
		this.pays = pays;
	}

}
