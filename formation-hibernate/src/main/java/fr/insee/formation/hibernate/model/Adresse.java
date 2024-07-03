package fr.insee.formation.hibernate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
