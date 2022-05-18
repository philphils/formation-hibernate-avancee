package fr.insee.formation.hibernate.services;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;

public interface SecteurServices {

	public AbstractNiveauNomenclature calculerIndicesSousClasseByCodeNaf(String codeNaf);

}
