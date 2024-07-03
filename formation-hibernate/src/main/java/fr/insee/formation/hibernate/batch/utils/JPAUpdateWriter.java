package fr.insee.formation.hibernate.batch.utils;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JPAUpdateWriter<X> implements ItemWriter<X> {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void write(Chunk<? extends X> items) throws Exception {

		String className = null;

		for (X item : items) {
			entityManager.merge(item);
			if (className == null)
				className = item.getClass().getName();
		}

		log.debug("{} items de type {} sont persistés", items.size(), className);
		entityManager.flush();
		entityManager.clear();

	}

}
