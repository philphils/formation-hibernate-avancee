package fr.insee.formation.hibernate.model.nomenclature;

import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
	@MapKeyColumn(name = "year")
	private Map<Year, IndiceAnnuel> indicesAnnuels = new HashMap<>();

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "sousClasse", cascade = CascadeType.ALL)
	@MapKeyColumn(name = "month")
	private Map<YearMonth, IndiceMensuel> indicesMensuels = new HashMap<>();

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
		indicesAnnuels.put(indiceAnnuel.getYear(), indiceAnnuel);
		indiceAnnuel.setSousClasse(this);
		return indiceAnnuel;
	}

	public IndiceAnnuel removeIndiceAnnuel(IndiceAnnuel indiceAnnuel) {
		indicesAnnuels.remove(indiceAnnuel);
		indiceAnnuel.setSousClasse(null);
		return indiceAnnuel;
	}

	public Map<Year, IndiceAnnuel> getIndicesAnnuels() {
		return Collections.unmodifiableMap(indicesAnnuels);
	}

	public IndiceMensuel addIndiceMensuel(IndiceMensuel indiceMensuel) {
		indicesMensuels.put(indiceMensuel.getMonth(), indiceMensuel);
		indiceMensuel.setSousClasse(this);
		return indiceMensuel;
	}

	public IndiceMensuel removeIndiceMensuel(IndiceMensuel indiceMensuel) {
		indicesMensuels.remove(indiceMensuel);
		indiceMensuel.setSousClasse(null);
		return indiceMensuel;
	}

	public Map<YearMonth, IndiceMensuel> getIndicesMensuels() {
		return Collections.unmodifiableMap(indicesMensuels);
	}

}
