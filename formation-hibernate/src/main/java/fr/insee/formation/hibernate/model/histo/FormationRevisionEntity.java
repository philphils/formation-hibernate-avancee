package fr.insee.formation.hibernate.model.histo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@RevisionEntity(FormationRevisionListener.class)
@Getter
@Setter
public class FormationRevisionEntity {

	@Id
	@GeneratedValue(generator = "rev_info_sequence_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "rev_info_sequence_gen", sequenceName = "rev_info_sequence", initialValue = 10, allocationSize = 1)
	@RevisionNumber
	private int id;

	@RevisionTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date revisionTimestamp;

	@ElementCollection
	@ModifiedEntityNames
	private Set<String> modifiedEntityNames = new HashSet<String>();

	private String contexte;

}
