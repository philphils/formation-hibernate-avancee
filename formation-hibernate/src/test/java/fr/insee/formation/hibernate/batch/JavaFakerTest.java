package fr.insee.formation.hibernate.batch;

import java.util.Locale;

import org.junit.Test;

import com.github.javafaker.Faker;

import fr.insee.formation.hibernate.config.AbstractTestConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaFakerTest extends AbstractTestConfiguration {

	@Test
	public void helloWorldTest() {

		Faker feku = new Faker(new Locale("fr"));

		log.info(feku.address().fullAddress());
		log.info(feku.address().fullAddress());
		log.info(feku.address().fullAddress());

	}

}
