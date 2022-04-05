package fr.insee.formation.hibernate.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JPAUpdateWriter<X> implements ItemWriter<X> {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void write(List<? extends X> items) throws Exception {

		for (X item : items) {
			entityManager.merge(item);
		}

		log.debug("{} items de type {} sont persistés", items.size(), items.get(0).getClass().getName());
		entityManager.flush();
		entityManager.clear();

	}

}
