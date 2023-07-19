package fr.insee.formation.hibernate.repositories.histo.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditDisjunction;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import fr.insee.formation.hibernate.repositories.histo.JPAHistoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JPAHistoRepositoryImpl<T, I extends Serializable> extends SimpleJpaRepository<T, I>
		implements JPAHistoRepository<T, I> {

	private Class<T> type;

	private EntityManager entityManager;

	public JPAHistoRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.type = domainClass;
		this.entityManager = em;
	}

	public JPAHistoRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.type = entityInformation.getJavaType();
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getEntityRevisions(I id, Date startRevisionTime, Date endRevisionTime) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		@SuppressWarnings("unchecked")
		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, true, true)
				.add(AuditEntity.id().eq(id));

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		return auditQuery.getResultList();
	}

	@Override
	public List<Object[]> getRevisionsWithInfo(I id) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		@SuppressWarnings("unchecked")
		List<Object[]> results = auditReader.createQuery().forRevisionsOfEntity(type, false, true)
				.add(AuditEntity.id().eq(id)).getResultList();

		return results;

	}

	@Override
	public List<T> getEntityRevisionsByProperties(I id, Date startRevisionTime, Date endRevisionTime,
			String... propertyNames) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, true, true)
				.add(AuditEntity.id().eq(id));

		addPropertyChangedConditions(auditQuery, propertyNames);

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		return auditQuery.getResultList();
	}

	@Override
	public T getFirstEntityRevision(I id) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, true, true)
				.add(AuditEntity.id().eq(id));

		auditQuery.add(AuditEntity.revisionType().eq(RevisionType.ADD));

		return (T) auditQuery.getSingleResult();
	}

	@Override
	public List<Object[]> getEntityRevisionsByPropertiesWithInfo(I id, Date startRevisionTime, Date endRevisionTime,
			String... propertyNames) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, false, false)
				.add(AuditEntity.id().eq(id));

		addPropertyChangedConditions(auditQuery, propertyNames);

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		return auditQuery.getResultList();
	}

	@Override
	public T getEntityLastRevisionByProperties(I id, Date startRevisionTime, Date endRevisionTime,
			String... propertyNames) {
		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, true, true)
				.add(AuditEntity.id().eq(id));

		addPropertyChangedConditions(auditQuery, propertyNames);

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		auditQuery.addProjection(AuditEntity.revisionNumber().max());

		return (T) auditQuery.getSingleResult();
	}

	@Override
	public List<T> getEntitiesRevisionsByProperties(List<I> ids, Date startRevisionTime, Date endRevisionTime,
			String... propertyNames) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, true, true)
				.add(AuditEntity.id().in(ids));

		addPropertyChangedConditions(auditQuery, propertyNames);

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		return auditQuery.getResultList();
	}

	@Override
	public List<Object[]> getAllEntitiesRevisionsByPropertiesWithInfo(Date startRevisionTime, Date endRevisionTime,
			String... propertyNames) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, false, false);

		addPropertyChangedConditions(auditQuery, propertyNames);

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		return auditQuery.getResultList();
	}

	@Override
	public List<T> getAllEntitiesRevisionsByContexteAndProperties(String contexte, Date startRevisionTime,
			Date endRevisionTime, String... propertyNames) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(type, true, false);

		auditQuery.add(AuditEntity.revisionProperty("contexte").eq(contexte));

		addPropertyChangedConditions(auditQuery, propertyNames);

		addRevisionDateConditions(auditQuery, startRevisionTime, endRevisionTime);

		return auditQuery.getResultList();
	}

	private void addPropertyChangedConditions(AuditQuery auditQuery, String... propertyNames) {

		AuditDisjunction auditDisjunction = AuditEntity.disjunction();

		for (String propertyName : propertyNames) {
			auditDisjunction.add(AuditEntity.property(propertyName).hasChanged());
		}

		auditQuery.add(auditDisjunction);

	}

	private void addRevisionDateConditions(AuditQuery auditQuery, Date startRevisionTime, Date endRevisionTime) {
		if (startRevisionTime != null) {
			auditQuery.add(AuditEntity.revisionProperty("revisionTimestamp").ge(startRevisionTime));
		}

		if (endRevisionTime != null) {
			auditQuery.add(AuditEntity.revisionProperty("revisionTimestamp").le(endRevisionTime));
		}
	}

}
