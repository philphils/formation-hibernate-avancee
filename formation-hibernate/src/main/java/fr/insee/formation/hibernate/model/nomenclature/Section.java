package fr.insee.formation.hibernate.model.nomenclature;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Section extends AbstractNiveau {

	private Set<Division> divisions = new HashSet<Division>();

}
