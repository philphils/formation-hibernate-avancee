package fr.insee.formation.hibernate.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Audited(withModifiedFlag = true)
public class Declaration {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	@SequenceGenerator(name = "hib_seq", sequenceName = "hib_seq", allocationSize = 100)
	private int id;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Entreprise entreprise;

	private Date date;

	private Double montant;

}