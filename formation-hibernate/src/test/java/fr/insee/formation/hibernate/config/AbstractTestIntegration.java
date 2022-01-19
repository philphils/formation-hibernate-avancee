package fr.insee.formation.hibernate.config;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.insee.formation.hibernate.Application;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;

/**
 * Classe abstraite dont doivent hériter toutes les classes de test
 * d'intégration. Cette configuration inclu l'utilisation d'une base de donnée
 * embarquée de type Postgres
 * 
 * @author SYV27O
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
@ActiveProfiles("test-integration")
@Rollback
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY)
@PropertySource(value = "/batch.properties")
public abstract class AbstractTestIntegration {
}