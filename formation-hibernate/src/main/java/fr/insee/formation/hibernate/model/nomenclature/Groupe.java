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
public class Groupe extends AbstractNiveauNomenclature {

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Division division;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
	private Set<Classe> classes = new HashSet<Classe>();

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
