package fr.insee.formation.hibernate.model.nomenclature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import fr.insee.formation.hibernate.model.Entreprise;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SousClasse extends AbstractNiveau {

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "secteur", cascade = CascadeType.ALL)
	private Set<Entreprise> entreprises = new HashSet<Entreprise>();

	public Entreprise addEntreprise(Entreprise entreprise) {
		entreprises.add(entreprise);
		entreprise.setSecteur(this);
		return entreprise;
	}

	public Entreprise removeEntreprise(Entreprise entreprise) {
		entreprises.remove(entreprise);
		entreprise.setSecteur(null);
		return entreprise;
	}

	public Set<Entreprise> getEntreprises() {
		return Collections.unmodifiableSet(entreprises);
	}

}
