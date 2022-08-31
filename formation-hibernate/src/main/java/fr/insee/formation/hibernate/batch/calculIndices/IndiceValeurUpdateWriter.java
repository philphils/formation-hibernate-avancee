package fr.insee.formation.hibernate.batch.calculIndices;

import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IndiceValeurUpdateWriter implements ItemWriter<Entry<IndiceMensuel, IndiceAnnuel>> {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void write(List<? extends Entry<IndiceMensuel, IndiceAnnuel>> collectionsIndice) throws Exception {

		for (Entry<IndiceMensuel, IndiceAnnuel> entry : collectionsIndice) {
			Query queryMensuel = entityManager.createQuery("UPDATE IndiceMensuel SET valeur = :valeur WHERE id = :id");
			queryMensuel.setParameter("valeur", entry.getKey().getValeur());
			queryMensuel.setParameter("id", entry.getKey().getId());
			queryMensuel.executeUpdate();

			Query queryAnnuel = entityManager.createQuery("UPDATE IndiceAnnuel SET valeur = :valeur WHERE id = :id");
			queryAnnuel.setParameter("valeur", entry.getValue().getValeur());
			queryAnnuel.setParameter("id", entry.getValue().getId());
			queryAnnuel.executeUpdate();
		}

		entityManager.flush();
		entityManager.clear();

	}

}
