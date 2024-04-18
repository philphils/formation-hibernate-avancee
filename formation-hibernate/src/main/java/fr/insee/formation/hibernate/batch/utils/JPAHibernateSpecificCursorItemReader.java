package fr.insee.formation.hibernate.batch.utils;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class JPAHibernateSpecificCursorItemReader<T> implements ItemReader<T>, Closeable {

	private EntityManager entityManager;

	@Autowired
	PlatformTransactionManager platformTransactionManager;

	private final String requeteJPQL;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	private ScrollableResults scrollableResults;

	public JPAHibernateSpecificCursorItemReader(String requeteJPQL) {
		this.requeteJPQL = requeteJPQL;
	}

	@PostConstruct
	public void validationQuery() {

		/*
		 * En cas d'erreur de synthaxe on obtient une exception
		 */
		entityManagerFactory.createEntityManager().createQuery(requeteJPQL);

	}

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//		if (entityManager == null) {
//			entityManager = entityManagerFactory.createEntityManager();
		if (scrollableResults == null) {

			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("Transaction reader");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//			def.setReadOnly(true);
			TransactionStatus status = platformTransactionManager.getTransaction(def);

			entityManager = entityManagerFactory.createEntityManager();

			Query query = (Query) entityManager.createQuery(requeteJPQL);
			scrollableResults = query.unwrap(org.hibernate.query.Query.class).setFetchSize(200)
					.scroll(ScrollMode.FORWARD_ONLY);
		}
//		}

		if (scrollableResults != null && scrollableResults.next()) {
			return (T) scrollableResults.get(0);
		} else {
			close();
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		if (scrollableResults != null) {
			scrollableResults.close();
		}
	}
}
