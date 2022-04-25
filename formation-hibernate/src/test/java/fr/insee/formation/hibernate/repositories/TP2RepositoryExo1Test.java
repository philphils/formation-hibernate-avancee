package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TP2RepositoryExo1Test extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private SecteurRepository secteurRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	TransactionTemplate transactionTemplate;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuxMappingAssociation();
			databaseIsInitialized = true;
		}
	}

	@Test
	public void testExercice1() {

		String siren = "123456789";

		String denomination = "Le bar à Momo";

		Optional<Entreprise> optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres (siren, null)

		assertFalse(optionalEntreprise.isPresent());

		optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres (null, denomination)

		assertFalse(optionalEntreprise.isPresent());

		optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres (siren, denomination)

		assertTrue(optionalEntreprise.isPresent());

		Entreprise entreprise = optionalEntreprise.get();

		assertEquals("Adresse(numero=12, typeVoie=RUE, nomVoie=Jean Jaurès, ville=PARIS, pays=FRANCE)",
				entreprise.getAdresse().toString());

		optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres ("faux_siren",
		// "N'importe quoi")

		assertFalse(optionalEntreprise.isPresent());

	}

}