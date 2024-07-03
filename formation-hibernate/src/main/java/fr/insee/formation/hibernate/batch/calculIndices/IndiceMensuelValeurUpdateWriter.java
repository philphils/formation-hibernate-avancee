package fr.insee.formation.hibernate.batch.calculIndices;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IndiceMensuelValeurUpdateWriter implements ItemWriter<IndiceMensuel> {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void write(Chunk<? extends IndiceMensuel> collectionsIndice) throws Exception {

		for (Indice indice : collectionsIndice) {
			Query queryMensuel = entityManager.createQuery("UPDATE IndiceMensuel SET valeur = :valeur WHERE id = :id");
			queryMensuel.setParameter("valeur", indice.getValeur());
			queryMensuel.setParameter("id", indice.getId());
			queryMensuel.executeUpdate();
		}

		entityManager.flush();
		entityManager.clear();

	}

}
