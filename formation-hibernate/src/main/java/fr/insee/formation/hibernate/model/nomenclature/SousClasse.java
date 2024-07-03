package fr.insee.formation.hibernate.model.nomenclature;

import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Cacheable
@org.hibernate.annotations.Cache(region = "nomenclature", usage = CacheConcurrencyStrategy.READ_ONLY)
public class SousClasse extends AbstractNiveauNomenclature {

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "nomenclature_association")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Classe classe;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "sousClasse", cascade = CascadeType.ALL)
	private Set<Entreprise> entreprises = new HashSet<Entreprise>();

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "sousClasse", cascade = CascadeType.ALL)
	private Set<IndiceAnnuel> indicesAnnuels = new HashSet<>();

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "sousClasse", cascade = CascadeType.ALL)
	private Set<IndiceMensuel> indicesMensuels = new HashSet<>();

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

	public IndiceAnnuel addIndiceAnnuel(IndiceAnnuel indiceAnnuel) {
		indicesAnnuels.add(indiceAnnuel);
		indiceAnnuel.setSousClasse(this);
		return indiceAnnuel;
	}

	public IndiceAnnuel removeIndiceAnnuel(IndiceAnnuel indiceAnnuel) {
		indicesAnnuels.remove(indiceAnnuel);
		indiceAnnuel.setSousClasse(null);
		return indiceAnnuel;
	}

	public Set<IndiceAnnuel> getIndicesAnnuels() {
		return Collections.unmodifiableSet(indicesAnnuels);
	}

	public IndiceMensuel addIndiceMensuel(IndiceMensuel indiceMensuel) {
		indicesMensuels.add(indiceMensuel);
		indiceMensuel.setSousClasse(this);
		return indiceMensuel;
	}

	public IndiceMensuel removeIndiceMensuel(IndiceMensuel indiceMensuel) {
		indicesMensuels.remove(indiceMensuel);
		indiceMensuel.setSousClasse(null);
		return indiceMensuel;
	}

	public Set<IndiceMensuel> getIndicesMensuels() {
		return Collections.unmodifiableSet(indicesMensuels);
	}

	public Map<Year, IndiceAnnuel> getMapIndicesAnnuels() {

		Map<Year, IndiceAnnuel> indiceAnnuels = new HashMap<Year, IndiceAnnuel>();

		for (Indice indice : indicesAnnuels) {
			indiceAnnuels.put(((IndiceAnnuel) indice).getYear(), (IndiceAnnuel) indice);
		}

		return Collections.unmodifiableMap(indiceAnnuels);
	}

	public Map<YearMonth, IndiceMensuel> getMapIndicesMensuels() {

		Map<YearMonth, IndiceMensuel> indiceMensuels = new HashMap<YearMonth, IndiceMensuel>();

		for (Indice indice : indicesMensuels) {
			indiceMensuels.put(((IndiceMensuel) indice).getMonth(), (IndiceMensuel) indice);
		}

		return Collections.unmodifiableMap(indiceMensuels);

	}

}
