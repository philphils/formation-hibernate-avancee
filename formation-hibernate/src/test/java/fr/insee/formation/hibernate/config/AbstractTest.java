package fr.insee.formation.hibernate.config;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Classe abstraite dont doivent hériter toutes les classes de test. Elle
 * n'utilise pas de base de données embarquées Postgres donc plus rapide
 * d'exécution
 * 
 * @author SYV27O
 *
 */

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
public abstract class AbstractTest {
}