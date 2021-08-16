package fr.insee.formation.hibernate.dao;

import fr.insee.formation.hibernate.model.Secteur;

public interface SecteurDAO {

	public Secteur find(int id);
	
	public Secteur findByCodeNaf(String codeNaf);

	public Secteur findByCodeNafWithEntreprisesAndDeclarationAndIndicesJPQL(String codeNaf);

	public Secteur findByCodeNafWithEntreprisesAndDeclarationAndIndicesCriteria(String codeNaf);

}
