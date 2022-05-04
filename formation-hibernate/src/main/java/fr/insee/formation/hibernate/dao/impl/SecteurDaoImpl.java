package fr.insee.formation.hibernate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import fr.insee.formation.hibernate.dao.SecteurDAO;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Entreprise_;
import fr.insee.formation.hibernate.model.Secteur_;
import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveau;

@Repository
public class SecteurDaoImpl implements SecteurDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public AbstractNiveau find(int id) {

		return entityManager.find(AbstractNiveau.class, id);

	}

	@Override
	public AbstractNiveau findByCodeNaf(String codeNaf) {
		return entityManager.createQuery("SELECT sect FROM Secteur sect WHERE sect.codeNaf = :codeNaf", AbstractNiveau.class)
				.setParameter("codeNaf", codeNaf).getSingleResult();
	}

	@Override
	public AbstractNiveau findByCodeNafWithEntreprisesAndDeclarationAndIndicesJPQL(String codeNaf) {

		String requete = "SELECT secteur FROM Secteur secteur " + " JOIN FETCH secteur.indices indice "
				+ " JOIN FETCH secteur.entreprises entreprise " + " JOIN FETCH entreprise.declarations declaration "
				+ " WHERE secteur.codeNaf = :codeNaf ";

		TypedQuery<AbstractNiveau> query = entityManager.createQuery(requete, AbstractNiveau.class);

		query.setParameter("codeNaf", codeNaf);

		return query.getSingleResult();
	}

	@Override
	public AbstractNiveau findByCodeNafWithEntreprisesAndDeclarationAndIndicesCriteria(String codeNaf) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<AbstractNiveau> criteria = builder.createQuery(AbstractNiveau.class);

		Root<AbstractNiveau> root = criteria.from(AbstractNiveau.class);

		criteria.select(root);

		criteria.where(builder.equal(root.get(Secteur_.codeNaf), codeNaf));

		/*
		 * On récupérer les indices, entreprises, et déclarations
		 */
		root.fetch(Secteur_.indices, JoinType.INNER);
		
		Fetch<AbstractNiveau, Entreprise> fetchEntreprise = root.fetch(Secteur_.entreprises, JoinType.INNER);
		
		Fetch<Entreprise, Declaration> fetchDeclarations = fetchEntreprise.fetch(Entreprise_.declarations, JoinType.INNER);
		
		return entityManager.createQuery(criteria).getSingleResult();
	}

}
