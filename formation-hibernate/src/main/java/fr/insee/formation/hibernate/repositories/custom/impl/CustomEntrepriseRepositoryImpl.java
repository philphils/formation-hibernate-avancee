package fr.insee.formation.hibernate.repositories.custom.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.repositories.custom.CustomEntrepriseRepository;

public class CustomEntrepriseRepositoryImpl implements CustomEntrepriseRepository {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Entreprise> findAllRevisionsOfEntreprise(Integer idEntreprise) {

		/**
		 * TP5 , exo4 : Cette méthode doit permettre de récupérer toutes les versions
		 * d'une entreprise à partir de son identifiant
		 */

		return null;
	}

	@Override
	public List<Entreprise> findAllEntreprisesOfRevision(Number number) {

		/**
		 * TP5, exo4 : Cette méthode doit permettre de récupérer toutes les entreprises
		 * modifiées lors d'une révision donnée
		 */

		return null;
	}

	@Override
	public List<Object[]> findAllRevisionsModifyingDenomination() {

		/**
		 * TP5, exo4 : Cette méthode doit permettre de retrouver toutes les révisions
		 * qui ont modifiées le champ dénomination d'une entreprise. Il faudra exclure
		 * les insertions.
		 */

		return null;
	}

}
