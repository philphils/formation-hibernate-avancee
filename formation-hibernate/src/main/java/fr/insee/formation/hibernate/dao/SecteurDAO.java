package fr.insee.formation.hibernate.dao;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;

public interface SecteurDAO {

	public AbstractNiveauNomenclature find(int id);
	
	public AbstractNiveauNomenclature findByCodeNaf(String codeNaf);

	public AbstractNiveauNomenclature findByCodeNafWithEntreprisesAndDeclarationAndIndicesJPQL(String codeNaf);

	public AbstractNiveauNomenclature findByCodeNafWithEntreprisesAndDeclarationAndIndicesCriteria(String codeNaf);

}
