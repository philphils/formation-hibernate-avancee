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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Classe extends AbstractNiveau {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Groupe groupe;

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
