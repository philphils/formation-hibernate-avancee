package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Division extends AbstractNiveau {

	@ManyToOne(fetch = FetchType.LAZY)
	private Section section;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
	private Set<Groupe> groupes;

	public Groupe addGroupe(Groupe groupe) {
		groupes.add(groupe);
		groupe.setDivision(this);
		return groupe;
	}

	public Groupe removeGroupe(Groupe groupe) {
		groupes.remove(groupe);
		groupe.setDivision(null);
		return groupe;
	}

	public Set<Groupe> getGroupes() {
		return Collections.unmodifiableSet(groupes);
	}

}
