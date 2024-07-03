package fr.insee.formation.hibernate.model;

import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyTemporal;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Audited(withModifiedFlag = true)
public class Entreprise {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	@SequenceGenerator(name = "hib_seq", sequenceName = "hib_seq", allocationSize = 100)
	private int id;

	@Version
	private Long version;

	private String denomination;

	@Column(length = 9, nullable = false)
	private String siren;

	@Embedded
	private Adresse adresse = new Adresse();

	@Column(length = 20)
	private String telephone;

	@Column(length = 50)
	private String url;

	@Temporal(TemporalType.DATE)
	private Date dateCreation;

	@Column(length = 50)
	private String nomFondateur;

	@Column(length = 50)
	private String prenomFondateur;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	@NotAudited
	private SousClasse sousClasse;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
	@MapKey(name = "date")
	@MapKeyTemporal(TemporalType.DATE)
	private Map<Date, Declaration> declarations = new HashMap<Date, Declaration>();

	private Double coeffRedressementEntreprise;

	private Double coeffCalculIndice;

	public Map<Date, Declaration> getDeclarations() {
		return Collections.unmodifiableMap(declarations);
	}

	public Declaration addDeclaration(Declaration declaration) {
		declarations.put(declaration.getDate(), declaration);
		declaration.setEntreprise(this);
		return declaration;
	}

	public Declaration removeDeclaration(Declaration declaration) {
		declarations.remove(declaration.getDate(), declaration);
		declaration.setEntreprise(null);
		return declaration;
	}

	public Declaration getDeclarationsByYearMonth(YearMonth yearMonth) {

		Declaration declaration = null;

		for (Declaration declaration2 : declarations.values()) {

			if (declaration2.getDate().toInstant().atZone(ZoneId.of("Europe/Paris")).getMonth()
					.equals(yearMonth.getMonth())
					&& declaration2.getDate().toInstant().atZone(ZoneId.of("Europe/Paris")).getYear() == yearMonth
							.getYear()) {

				if (declaration != null) {
					throw new RuntimeException(
							"2 declarations pour l'entreprise " + id + " pour le même mois " + yearMonth);
				}

				declaration = declaration2;

			}

		}

		return declaration;
	}

	public Set<Declaration> getDeclarationsByYear(Year year) {

		Set<Declaration> declarationsSet = new HashSet<>();

		for (Declaration declaration : declarations.values()) {
			if (declaration.getDate().toInstant().atZone(ZoneId.of("Europe/Paris")).getYear() == year.getValue()) {
				declarationsSet.add(declaration);
			}
		}

		return Collections.unmodifiableSet(declarationsSet);
	}

	/**
	 * Permet de calculer le coefficient moyen de la hiérarchie dans la nomenclature
	 * utilisé pour la calcul des {@link Indice}
	 * 
	 * @return
	 */
	public Double calculCoeffMoyen() {

		Double coeffMoyen = (coeffCalculIndice + sousClasse.getCoeffCalculIndice()
				+ sousClasse.getClasse().getCoeffCalculIndice()
				+ sousClasse.getClasse().getGroupe().getCoeffCalculIndice()
				+ sousClasse.getClasse().getGroupe().getDivision().getCoeffCalculIndice()
				+ sousClasse.getClasse().getGroupe().getDivision().getSection().getCoeffCalculIndice()) / 6;

		return coeffMoyen;
	}

}
