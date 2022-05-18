package fr.insee.formation.hibernate.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Indice {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private AbstractNiveauNomenclature niveauNomenclature;

	private Double valeur;

	private Instant derniereMaj;

}
