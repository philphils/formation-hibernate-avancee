package fr.insee.formation.hibernate.dao;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveau;

public interface SecteurDAO {

	public AbstractNiveau find(int id);
	
	public AbstractNiveau findByCodeNaf(String codeNaf);

	public AbstractNiveau findByCodeNafWithEntreprisesAndDeclarationAndIndicesJPQL(String codeNaf);

	public AbstractNiveau findByCodeNafWithEntreprisesAndDeclarationAndIndicesCriteria(String codeNaf);

}
