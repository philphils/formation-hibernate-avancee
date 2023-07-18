package fr.insee.formation.hibernate.repositories.custom;

import java.util.List;

import fr.insee.formation.hibernate.model.Entreprise;

public interface CustomEntrepriseRepository {

	public List<Entreprise> findAllRevisionsOfEntreprise(Integer idEntreprise);

	List<Entreprise> findAllEntreprisesOfRevision(Number number);

	List<Object[]> findAllRevisionsModifyingDenomination();

}
