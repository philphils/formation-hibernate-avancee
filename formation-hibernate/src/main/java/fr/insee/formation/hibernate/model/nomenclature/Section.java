package fr.insee.formation.hibernate.model.nomenclature;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Section extends AbstractNiveau {

	private Set<Division> divisions = new HashSet<Division>();

}
