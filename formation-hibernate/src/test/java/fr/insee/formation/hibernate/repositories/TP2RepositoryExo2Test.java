package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Secteur;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class TP2RepositoryExo2Test extends AbstractTest {

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
			jeuxTestUtil.creerJeux3Secteurs();
			databaseIsInitialized = true;
		}
	}

	@Test
	@Transactional
	public void testExercice2() {

		/*
		 * Récupération d'une entreprise au hasard
		 */
		Secteur secteur = secteurRepository.findAll().get(0);

		Entreprise entreprise = secteur.getEntreprises().iterator().next();

		entityManager.clear();

		QueryCountHolder.clear();

		Secteur secteur2 = secteurRepository.findByEntrepriseWithAllEntreprises(entreprise);

		for (Entreprise entreprise2 : secteur2.getEntreprises()) {
			log.info("L'Entreprise {} est dans le secteur {}", entreprise2.getDenomination(),
					secteur2.getLibelleNomenclature());
		}

		assertEquals(3, secteur2.getEntreprises().size());

		assertTrue(secteur2.getEntreprises().stream().anyMatch(ent -> ent.getId() == entreprise.getId()));

		assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

	}

//	public Entreprise getEntrepriseFromASecteur() {
//
//		Entreprise entreprise = null;
//
//		transactionTemplate.execute(new TransactionCallback {
//			@Override
//			protected void doInTransactionWithoutResult(TransactionStatus status) {
//				Secteur secteur = secteurRepository.findAll().get(0);
//
//				Integer idSecteur = secteur.getId();
//
//				entreprise = secteur.getEntreprises().iterator().next();
//
//			}
//		});
//
//		return entreprise;
//	}

}