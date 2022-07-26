package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Section extends AbstractNiveauNomenclature {

	@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
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
