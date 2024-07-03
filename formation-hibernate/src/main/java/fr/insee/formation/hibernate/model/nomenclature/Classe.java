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
public class Classe extends AbstractNiveauNomenclature {

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Groupe groupe;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "classe", cascade = CascadeType.ALL)
	private Set<SousClasse> sousClasses = new HashSet<SousClasse>();

	public SousClasse addSousClasse(SousClasse sousClasse) {
		sousClasses.add(sousClasse);
		sousClasse.setClasse(this);
		return sousClasse;
	}

	public SousClasse removeSousClasse(SousClasse sousClasse) {
		sousClasses.remove(sousClasse);
		sousClasse.setClasse(null);
		return sousClasse;
	}

	public Set<SousClasse> getSousClasses() {
		return Collections.unmodifiableSet(sousClasses);
	}

}
