package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Cacheable
@org.hibernate.annotations.Cache(region = "nomenclature", usage = CacheConcurrencyStrategy.READ_ONLY)
public class Division extends AbstractNiveauNomenclature {

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Section section;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
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
