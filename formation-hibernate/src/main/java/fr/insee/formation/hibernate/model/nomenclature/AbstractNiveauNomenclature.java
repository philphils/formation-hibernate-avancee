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
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractNiveauNomenclature {

	@Id
	@GeneratedValue
	private int id;

	private String codeNaf;

	private String libelleNomenclature;

	/*
	 * Limitation d'Hibernate : mappedBy ne peut référencer un attribut héritée avec
	 * les héritage @Inheritance (cf
	 * http://chriswongdevblog.blogspot.fr/2009/10/polymorphic-one-to-many-
	 * relationships.html) On peu s'en sortir avec @JoinColumn et @Where.
	 * Avec @MappedSuperclass c'est possible, mais on perd la possibilité de faire
	 * du polymorhpisme... On stocke donc les indices indiféremment qu'ils soient
	 * annuels ou mensuels
	 */
	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "niveauNomenclature", cascade = CascadeType.ALL)
	private Set<Indice> indices = new HashSet<Indice>();

	private Double coeffRedressementNiveau;

	private Double coeffCalculIndice;

	public Indice addIndice(Indice indice) {
		indices.add(indice);
		indice.setNiveauNomenclature(this);
		return indice;
	}

	public Indice removeIndice(Indice indice) {
		indices.remove(indice);
		indice.setNiveauNomenclature(null);
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