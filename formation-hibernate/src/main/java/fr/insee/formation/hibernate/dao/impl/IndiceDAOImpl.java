package fr.insee.formation.hibernate.dao.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import fr.insee.formation.hibernate.dao.IndiceDAO;
import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;

@Repository
@Transactional
public class IndiceDAOImpl implements IndiceDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public IndiceAnnuel findIndiceAnnuel(int i) {
		return entityManager.find(IndiceAnnuel.class, i);
	}

	@Override
	public IndiceMensuel findIndiceMensuel(int i) {
		return entityManager.find(IndiceMensuel.class, i);
	}

	@Override
	public Set<Indice> findAll() {
		return new HashSet<Indice>(entityManager.createQuery("SELECT ind FROM Indice ind", Indice.class).getResultList());
	}

}
