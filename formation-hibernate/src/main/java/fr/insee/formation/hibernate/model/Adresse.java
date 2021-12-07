package fr.insee.formation.hibernate.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Data;

@Data
@Embeddable
public class Adresse {

	private String numero;

	@Enumerated(EnumType.STRING)
	private TypeVoie typeVoie;

	@Column(name = "rue")
	private String nomVoie;

	private String ville;

	private String pays;

}
