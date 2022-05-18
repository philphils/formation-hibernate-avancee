package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
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
public class Groupe extends AbstractNiveau {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Division division;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
	private Set<Classe> classes;

	public Classe addClasse(Classe classe) {
		classes.add(classe);
		classe.setGroupe(this);
		return classe;
	}

	public Classe removeClasse(Classe classe) {
		classes.remove(classe);
		classe.setGroupe(null);
		return classe;
	}

	public Set<Classe> getClasses() {
		return Collections.unmodifiableSet(classes);
	}

}
