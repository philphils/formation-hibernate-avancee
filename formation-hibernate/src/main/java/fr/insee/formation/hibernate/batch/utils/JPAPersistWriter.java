package fr.insee.formation.hibernate.batch.utils;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JPAPersistWriter<X> implements ItemWriter<X> {

	@Autowired
	EntityManager entityManager;

	@Override
	public void write(List<? extends X> items) throws Exception {

		for (X item : items) {
			entityManager.persist(item);
		}

		log.trace("{} items de type {} sont persistés", items.size(), items.get(0).getClass().getName());
		entityManager.flush();
		entityManager.clear();

	}

}
