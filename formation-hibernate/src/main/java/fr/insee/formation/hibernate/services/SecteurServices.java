package fr.insee.formation.hibernate.services;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;

//TODO Modifier le service pour calcul des indices avec le nouveau modèle de nomenclature
public interface SecteurServices {

	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNaf(String codeNaf);

	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNafRequeteJPQL(String codeNaf);

	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNafRequeteCriteria(String string);

}
