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

import fr.insee.formation.hibernate.model.Entreprise;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SousClasse extends AbstractNiveauNomenclature {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Classe classe;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "sousClasse", cascade = CascadeType.ALL)
	private Set<Entreprise> entreprises = new HashSet<Entreprise>();

	public Entreprise addEntreprise(Entreprise entreprise) {
		entreprises.add(entreprise);
		entreprise.setSousClasse(this);
		return entreprise;
	}

	public Entreprise removeEntreprise(Entreprise entreprise) {
		entreprises.remove(entreprise);
		entreprise.setSousClasse(null);
		return entreprise;
	}

	public Set<Entreprise> getEntreprises() {
		return Collections.unmodifiableSet(entreprises);
	}

}
