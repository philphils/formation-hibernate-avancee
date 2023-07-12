package fr.insee.formation.hibernate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = {
		"spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create",
		"spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=create.sql" })
@ActiveProfiles("test")
public class GenerationScriptCreateTest {

	@Test
	public void testGenerationScriptCreate() {

		/**
		 * Lancer ce test et rafraîchissez au niveau du projet. Un fichier create.sql
		 * doit apparaître à la racine à côté du pom.xml
		 */

	}

}
