package fr.insee.formation.hibernate.model.histo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormationRevisionEntity {

	/**
	 * TP5, exo3 : Compléter cette classe pour avoir une Revision Entity complète
	 * (avec numéro de révision, timestamp, champ des entités modifiées, et infos
	 * contexte)
	 */

	private int id;

	private Date revisionTimestamp;

	private Set<String> modifiedEntityNames = new HashSet<String>();

	private String contexte;

}
