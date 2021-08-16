package fr.insee.formation.hibernate.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import fr.insee.formation.hibernate.dao.EntrepriseDAO;
import fr.insee.formation.hibernate.model.Entreprise;

@Repository
@Transactional
public class EntrepriseDAOJPAImpl implements EntrepriseDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Entreprise> findAllOrderByDateCreation() {
		return entityManager.createQuery("SELECT ent FROM Entreprise ent ORDER BY dateCreation", Entreprise.class).getResultList();
	}

	@Override
	public void persist(Entreprise entreprise) {
		entityManager.persist(entreprise);
	}

	@Override
	public void upperDenomination() {
		for (Entreprise entreprise : findAllOrderByDateCreation()) {
			entreprise.setDenomination(entreprise.getDenomination().toUpperCase());
		}
	}

	@Override
	public void removeEntrepriseById(int identifiant) {

		Entreprise entreprise = entityManager.find(Entreprise.class, identifiant);

		entityManager.remove(entreprise);
	}

}
