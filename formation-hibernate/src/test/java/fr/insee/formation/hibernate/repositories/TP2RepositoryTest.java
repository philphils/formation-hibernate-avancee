package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Secteur;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class TP2RepositoryTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private SecteurRepository secteurRepository;

	@Autowired
	private EntityManager entityManager;

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

		assertTrue(optionalEntreprise.isPresent());

		Entreprise entreprise = optionalEntreprise.get();

		assertEquals("Adresse(numero=12, typeVoie=RUE, nomVoie=Jean Jaurès, ville=PARIS, pays=FRANCE)",
				entreprise.getAdresse().toString());

		optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres (null, denomination)

		assertTrue(optionalEntreprise.isPresent());

		entreprise = optionalEntreprise.get();

		assertEquals("Adresse(numero=12, typeVoie=RUE, nomVoie=Jean Jaurès, ville=PARIS, pays=FRANCE)",
				entreprise.getAdresse().toString());

		optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres (siren, denomination)

		assertTrue(optionalEntreprise.isPresent());

		entreprise = optionalEntreprise.get();

		assertEquals("Adresse(numero=12, typeVoie=RUE, nomVoie=Jean Jaurès, ville=PARIS, pays=FRANCE)",
				entreprise.getAdresse().toString());

		optionalEntreprise = null;
		// Remplacer "null" par votre appel à la méthode que vous
		// écrirez dans EntrepriseRepository avec les paramètres ("faux_siren",
		// "N'importe quoi")

		assertFalse(optionalEntreprise.isPresent());

	}

	@Test
	@Transactional
	public void testExercice2() {

		Secteur secteur = secteurRepository.findAll().get(0);

		Integer idSecteur = secteur.getId();

		Entreprise entreprise = secteur.getEntreprises().iterator().next();

		secteur = null;

		entityManager.clear();

		QueryCountHolder.clear();

		// **********************************************************************************//
		// Remplacer ici null par l'appel à une méthode que vous définirez dans
		// SecteurRepository
		// **********************************************************************************//
		Secteur secteur2 = null;
		// **********************************************************************************//

		for (Entreprise entreprise2 : secteur2.getEntreprises()) {
			log.info("L'Entreprise {} est dans le secteur {}", entreprise2.getDenomination(),
					secteur2.getLibelleNomenclature());
		}

		assertTrue(secteur2.getEntreprises().stream().anyMatch(ent -> ent.getId() == entreprise.getId()));

		assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

	}

}