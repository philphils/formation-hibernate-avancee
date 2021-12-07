package fr.insee.formation.hibernate.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(exclude = "declarations")
@Entity
public class Entreprise {

	@Id
	@GeneratedValue(generator = "hib_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "hib_seq", initialValue = 1, allocationSize = 1)
	private int id;

	private String denomination;

	@Column(length = 9, unique = true, nullable = false)
	private String siren;

	@Embedded
	private Adresse adresse;

	@Column(length = 10)
	private String telephone;

	@Enumerated(EnumType.STRING)
	private FormeJuridique formeJuridique;

	@Temporal(TemporalType.DATE)
	private Date dateCreation;

	@ManyToOne
	@JoinColumn
	private Secteur secteur;

	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
	@MapKey(name = "date")
	@MapKeyTemporal(TemporalType.DATE)
	private Map<Date, Declaration> declarations = new HashMap<Date, Declaration>();

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
