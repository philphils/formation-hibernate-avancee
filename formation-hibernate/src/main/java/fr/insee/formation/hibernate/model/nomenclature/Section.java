package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Cacheable
@org.hibernate.annotations.Cache(region = "nomenclature", usage = CacheConcurrencyStrategy.READ_ONLY)
public class Section extends AbstractNiveauNomenclature {

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
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
