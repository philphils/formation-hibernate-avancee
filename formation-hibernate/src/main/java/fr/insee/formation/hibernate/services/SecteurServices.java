package fr.insee.formation.hibernate.services;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveau;

public interface SecteurServices {
	
	public AbstractNiveau calculerIndicesSecteurByCodeNaf(String codeNaf);
	
	public AbstractNiveau calculerIndicesSecteurByCodeNafRequeteJPQL(String codeNaf);

	public AbstractNiveau calculerIndicesSecteurByCodeNafRequeteCriteria(String string);

}
