package fr.insee.formation.hibernate.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Entreprise {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_seq")
	@SequenceGenerator(name = "hib_seq", sequenceName = "hib_seq", allocationSize = 100)
	private int id;

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

}
