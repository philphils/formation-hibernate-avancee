package fr.insee.formation.hibernate.model.nomenclature;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

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