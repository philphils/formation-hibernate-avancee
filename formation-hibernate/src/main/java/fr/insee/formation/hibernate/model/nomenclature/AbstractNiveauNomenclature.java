package fr.insee.formation.hibernate.model.nomenclature;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractNiveauNomenclature {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	@SequenceGenerator(name = "hib_seq", sequenceName = "hib_seq", allocationSize = 100)
	private int id;

	private String codeNaf;

	private String libelleNomenclature;

	private Double coeffRedressementNiveau;

	private Double coeffCalculIndice;

}