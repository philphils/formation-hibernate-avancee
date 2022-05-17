package fr.insee.formation.hibernate.model.nomenclature;

import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "niveau", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractNiveau {

	@Id
	@GeneratedValue
	private int id;

	private String codeNaf;

	private String libelleNomenclature;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "secteur", cascade = CascadeType.ALL)
	private Set<Indice> indices = new HashSet<Indice>();

	private Double coeffRedressementNiveau;

	/*
	 * Limitation d'Hibernate : mappedBy ne peut référencer un attribut héritée avec
	 * les héritage @Inheritance (cf
	 * http://chriswongdevblog.blogspot.fr/2009/10/polymorphic-one-to-many-
	 * relationships.html) On peu s'en sortir avec @JoinColumn et @Where.
	 * Avec @MappedSuperclass c'est possible, mais on perd la possibilité de faire
	 * du polymorhpisme...
	 */
	// @OneToMany(mappedBy = "secteur")
	// private Set<IndiceAnnuel> indiceAnnuels = new HashSet<IndiceAnnuel>();
	//
	// @OneToMany(mappedBy = "secteur")
	// private Set<IndiceMensuel> indiceMensuels = new HashSet<IndiceMensuel>();

	public Indice addIndice(Indice indice) {
		indices.add(indice);
		indice.setSecteur(this);
		return indice;
	}

	public Indice removeIndice(Indice indice) {
		indices.remove(indice);
		indice.setSecteur(null);
		return indice;
	}

	public Set<Indice> getIndices() {
		return Collections.unmodifiableSet(indices);
	}

	public Map<Year, IndiceAnnuel> getIndicesAnnuels() {

		Map<Year, IndiceAnnuel> indiceAnnuels = new HashMap<Year, IndiceAnnuel>();

		for (Indice indice : indices) {
			if (indice instanceof IndiceAnnuel) {
				indiceAnnuels.put(((IndiceAnnuel) indice).getYear(), (IndiceAnnuel) indice);
			}
		}

		return indiceAnnuels;
	}

	public Map<YearMonth, IndiceMensuel> getIndicesMensuels() {

		Map<YearMonth, IndiceMensuel> indiceMensuels = new HashMap<YearMonth, IndiceMensuel>();

		for (Indice indice : indices) {

			if (indice instanceof IndiceMensuel) {
				indiceMensuels.put(((IndiceMensuel) indice).getMonth(), (IndiceMensuel) indice);
			}

		}

		return indiceMensuels;

	}

}