package fr.insee.formation.hibernate.batch.utils;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JPACollectionPersistWriter<X> implements ItemWriter<Collection<X>> {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void write(Chunk<? extends Collection<X>> collections) throws Exception {

		for (Collection<X> collection : collections) {
			for (X item : collection)
				entityManager.persist(item);
		}

		entityManager.flush();
		entityManager.clear();

	}

}
