package fr.insee.formation.hibernate.config;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
@ActiveProfiles("test")
public abstract class AbstractTest {
}