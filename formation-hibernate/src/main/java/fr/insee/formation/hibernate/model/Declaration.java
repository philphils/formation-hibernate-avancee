package fr.insee.formation.hibernate.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
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

}