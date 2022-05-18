package fr.insee.formation.hibernate.services;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;

public interface SecteurServices {
	
	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNaf(String codeNaf);
	
	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNafRequeteJPQL(String codeNaf);

	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNafRequeteCriteria(String string);

}
