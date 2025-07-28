package fr.insee.formation.hibernate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.insee.formation.hibernate.config.AbstractTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = {
		"spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create",
		"spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=create.sql" })
@ActiveProfiles("test")
public class GenerationScriptCreateTest extends AbstractTest {

       @Test
       public void testGenerationScriptCreate() throws Exception {

               /*
                * Lancer ce test et rafraîchissez au niveau du projet. Un fichier create.sql
                * doit apparaître à la racine à côté du pom.xml
                */
               Path createScript = Paths.get("create.sql");
               org.junit.Assert.assertTrue(Files.exists(createScript));
               // Nettoyage du projet : suppression du script généré
               Files.deleteIfExists(createScript);

       }

}
