package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Section extends AbstractNiveau {

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
	private Set<Division> divisions = new HashSet<Division>();

	public Division addDivision(Division division) {
		divisions.add(division);
		division.setSection(this);
		return division;
	}

	public Division removeDivision(Division division) {
		divisions.remove(division);
		division.setSection(null);
		return division;
	}

	public Set<Division> getDivisions() {
		return Collections.unmodifiableSet(divisions);
	}

}
