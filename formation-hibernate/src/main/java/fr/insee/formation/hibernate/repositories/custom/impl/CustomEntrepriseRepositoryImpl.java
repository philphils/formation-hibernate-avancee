package fr.insee.formation.hibernate.repositories.custom.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditConjunction;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.repositories.custom.CustomEntrepriseRepository;

public class CustomEntrepriseRepositoryImpl implements CustomEntrepriseRepository {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Entreprise> findAllRevisionsOfEntreprise(Integer idEntreprise) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(Entreprise.class, true, true)
				.add(AuditEntity.id().eq(idEntreprise));

		return auditQuery.getResultList();
	}

	@Override
	public List<Entreprise> findAllEntreprisesOfRevision(Number number) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forEntitiesModifiedAtRevision(Entreprise.class, number);

		return auditQuery.getResultList();
	}

	@Override
	public List<Object[]> findAllRevisionsModifyingDenomination() {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntityWithChanges(Entreprise.class, false);

		AuditConjunction auditDisjunction = AuditEntity.conjunction();

		auditDisjunction.add(AuditEntity.property("denomination").hasChanged());

		auditDisjunction.add(AuditEntity.revisionType().ne(RevisionType.ADD));

		auditQuery.add(auditDisjunction);

		return auditQuery.getResultList();
	}

}
