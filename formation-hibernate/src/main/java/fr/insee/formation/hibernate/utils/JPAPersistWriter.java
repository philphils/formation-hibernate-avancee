package fr.insee.formation.hibernate.utils;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class JPAPersistWriter<X> implements ItemWriter<X> {

	@Autowired
	EntityManager entityManager;

	@Override
	public void write(List<? extends X> items) throws Exception {

		for (X item : items) {
			entityManager.persist(item);
		}

	}

}
