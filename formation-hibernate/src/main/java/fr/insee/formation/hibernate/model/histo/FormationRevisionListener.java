package fr.insee.formation.hibernate.model.histo;

import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Value;

public class FormationRevisionListener implements RevisionListener {

	@Value("${revision.entity.contexte}")
	private String contexte;

	@Override
	public void newRevision(Object revisionEntity) {
		((FormationRevisionEntity) revisionEntity).setContexte(contexte);
	}

}
