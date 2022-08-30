package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Division extends AbstractNiveauNomenclature {

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Section section;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
	private Set<Groupe> groupes = new HashSet<Groupe>();

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
