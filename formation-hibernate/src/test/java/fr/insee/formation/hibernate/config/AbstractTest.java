package fr.insee.formation.hibernate.config;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;

import fr.insee.formation.hibernate.Application;

/**
 * Classe abstraite dont doivent hériter toutes les classes de test. Elle
 * n'utilise pas de base de données embarquées Postgres donc plus rapide
 * d'exécution
 * 
 * @author SYV27O
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
@Import(DataSourceTestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
public abstract class AbstractTest {

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