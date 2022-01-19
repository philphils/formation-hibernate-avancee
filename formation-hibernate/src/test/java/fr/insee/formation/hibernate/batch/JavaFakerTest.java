package fr.insee.formation.hibernate.batch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.github.javafaker.Faker;

import fr.insee.formation.hibernate.config.AbstractTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaFakerTest extends AbstractTest {

	@Test
	public void helloWorldTest() {

		Faker faker = new Faker(new Locale("fr"));

		// Obtention de 3 adresses différentes
		log.info(faker.address().fullAddress());
		log.info(faker.address().fullAddress());
		log.info(faker.address().fullAddress());

		// Obtention d'une date aléatoire entre le 01/01/1970 et le 01/01/2022
		Date dateDebutCreation = java.sql.Date.valueOf(LocalDate.of(1970, 01, 01));
		Date dateFinCreation = java.sql.Date.valueOf(LocalDate.of(2022, 01, 01));

		Date dateAleatoire = faker.date().between(dateDebutCreation, dateFinCreation);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		log.info(dateFormat.format(dateAleatoire));

		// Obtention de nom d'entreprise en français
		log.info(faker.company().name());

	}

}
