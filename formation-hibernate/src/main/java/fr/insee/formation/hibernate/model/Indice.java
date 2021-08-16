package fr.insee.formation.hibernate.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Indice {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn
	private Secteur secteur;

	private Double valeur;
	
	private Instant derniereMaj;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Secteur getSecteur() {
		return secteur;
	}

	public void setSecteur(Secteur secteur) {
		this.secteur = secteur;
	}

	public Double getValeur() {
		return valeur;
	}

	public void setValeur(Double valeur) {
		this.valeur = valeur;
	}

	public Instant getDerniereMaj() {
		return derniereMaj;
	}

	public void setDerniereMaj(Instant derniereMaj) {
		this.derniereMaj = derniereMaj;
	}

}
