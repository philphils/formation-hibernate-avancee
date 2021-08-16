package fr.insee.formation.hibernate.services;

import fr.insee.formation.hibernate.model.Secteur;

public interface SecteurServices {
	
	public Secteur calculerIndicesSecteurByCodeNaf(String codeNaf);
	
	public Secteur calculerIndicesSecteurByCodeNafRequeteJPQL(String codeNaf);

	public Secteur calculerIndicesSecteurByCodeNafRequeteCriteria(String string);

}
