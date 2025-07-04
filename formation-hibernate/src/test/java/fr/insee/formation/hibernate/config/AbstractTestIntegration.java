package fr.insee.formation.hibernate.config;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;

import fr.insee.formation.hibernate.Application;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;

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
@AutoConfigureEmbeddedDatabase(type = DatabaseType.POSTGRES)
@DirtiesContext
public abstract class AbstractTestIntegration {

	/**
	 * Ferme la transaction en cours avec un commit et ouvre une nouvelle
	 * transaction pour la réalisation du test
	 */
	protected void ouvrirNouvelleTransaction() {
		/*
		 * On commit l'insertion des données et on clos la transaction
		 */
		TestTransaction.flagForCommit();
		TestTransaction.end();

		/*
		 * On démarre une nouvelle transaction pour le test
		 */
		TestTransaction.start();
	}
}