package fr.insee.formation.hibernate.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Declaration {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn
	private Entreprise entreprise;

	private Date date;

	private Double montant;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Entreprise getEntreprise() {
		return entreprise;
	}

	public void setEntreprise(Entreprise entreprise) {
		this.entreprise = entreprise;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getMontant() {
		return montant;
	}

	public void setMontant(Double montant) {
		this.montant = montant;
	}
	
	

}
