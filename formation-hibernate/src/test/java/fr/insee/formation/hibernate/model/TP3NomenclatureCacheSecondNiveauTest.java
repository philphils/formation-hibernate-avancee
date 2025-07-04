package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TestTransaction;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.nomenclature.Classe;
import fr.insee.formation.hibernate.model.nomenclature.Division;
import fr.insee.formation.hibernate.model.nomenclature.Groupe;
import fr.insee.formation.hibernate.model.nomenclature.Section;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.SectionRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class TP3NomenclatureCacheSecondNiveauTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private SectionRepository sectionRepository;

	@Autowired
	private SousClasseRepository sousClasseRepository;

	@PersistenceContext
	EntityManager entityManager;

	@PersistenceUnit
	EntityManagerFactory entityManagerFactory;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuNouvelleNomenclatureAvecIndice();
			databaseIsInitialized = true;
		}

		ouvrirNouvelleTransaction();

		entityManager.clear();
		entityManagerFactory.getCache().evictAll();

		QueryCountHolder.clear();

	}

	@Test
	@Transactional
	public void testSectionCache() {

		List<Section> sections = sectionRepository.findAll();

		assertTrue("No Section entities found", !sections.isEmpty());
		
		Integer sectionId = sections.get(0).getId();
		assertTrue(entityManagerFactory.getCache().contains(Section.class, sectionId));

	}

	@Test
	@Transactional
	public void testAssociationSectionDivisionCache() {

		List<Section> sections = sectionRepository.findAll();

		sections.forEach(section -> section.getDivisions()
				.forEach(div -> log.info("Section : " + section.getCodeNaf() + ", div : " + div.getCodeNaf())));

		TestTransaction.flagForCommit();
		TestTransaction.end();

		sections = null;

		entityManager.clear();

		QueryCountHolder.clear();

		TestTransaction.start();

		List<Section> sections2 = sectionRepository.findAll();

		sections2.forEach(section -> section.getDivisions()
				.forEach(div -> log.info("Section : " + section.getCodeNaf() + ", div : " + div.getCodeNaf())));

		/*
		 * Les requêtes de récupération des divisions ne doivent pas avoir été faîtes
		 * car l'association doit avoir été mise en cache
		 */
		assertEquals(QueryCountHolder.getGrandTotal().getTotal(), 0);

	}

	@Test
	@Transactional
	public void testQueryCacheProgramatic() {

		Section section = (Section) entityManager
				.createQuery("SELECT section FROM Section section WHERE section.codeNaf = :codeNaf")
				.setParameter("codeNaf", "section1").setHint("org.hibernate.cacheable", true).getSingleResult();

		QueryCountHolder.clear();

		Section section2 = (Section) entityManager
				.createQuery("SELECT section FROM Section section WHERE section.codeNaf = :codeNaf")
				.setParameter("codeNaf", "section1").setHint("org.hibernate.cacheable", true).getSingleResult();

		assertEquals(QueryCountHolder.getGrandTotal().getTotal(), 0);

	}

	@Test
	@Transactional
	public void testQueryCacheAnnotation() {

		Optional<Section> section = sectionRepository.findByCodeNaf("section1");

		QueryCountHolder.clear();

		Optional<Section> section2 = sectionRepository.findByCodeNaf("section1");

		assertEquals(QueryCountHolder.getGrandTotal().getTotal(), 0);

	}

	@Test
	@Transactional
	public void testFindAllCache() {

		List<Section> sections = sectionRepository.findAll();

		QueryCountHolder.clear();

		List<Section> sections2 = sectionRepository.findAll();

		assertEquals(QueryCountHolder.getGrandTotal().getTotal(), 0);

	}

	@Test
	@Transactional
	public void testCacheAssociationSensDescendant() {

		/*
		 * Premier passage pour mise en cache
		 */

		Optional<Section> optionalSection = sectionRepository.findByCodeNaf("section1");

		Section section = optionalSection.get();

		for (Division division : section.getDivisions()) {

			log.info("Division " + division.getCodeNaf());

			for (Groupe groupe : division.getGroupes()) {

				log.info("Groupe " + groupe.getCodeNaf());

				for (Classe classe : groupe.getClasses()) {

					log.info("Classe " + classe.getCodeNaf());

					for (SousClasse sousClasse : classe.getSousClasses()) {

						log.info("Sous Classe " + sousClasse.getCodeNaf());

					}

				}

			}
		}

		entityManager.clear();

		QueryCountHolder.clear();

		/*
		 * Deuxièmement passage pour vérification requêtes générées
		 */

		Optional<Section> optionalSection2 = sectionRepository.findByCodeNaf("section1");

		Section section2 = optionalSection2.get();

		for (Division division : section2.getDivisions()) {

			log.info("Division " + division.getCodeNaf());

			for (Groupe groupe : division.getGroupes()) {

				log.info("Groupe " + groupe.getCodeNaf());

				for (Classe classe : groupe.getClasses()) {

					log.info("Classe " + classe.getCodeNaf());

					for (SousClasse sousClasse : classe.getSousClasses()) {

						log.info("Sous Classe " + sousClasse.getCodeNaf());

					}

				}

			}
		}

		assertEquals(0, QueryCountHolder.getGrandTotal().getSelect());

	}

	@Test
	@Transactional
	public void testCacheAssociationSensAscendant() {

		/*
		 * Premier passage pour mise en cache
		 */

		Optional<SousClasse> optionalSousClasse = sousClasseRepository.findByCodeNaf("sousclasse5");

		SousClasse sousClasse = optionalSousClasse.get();

		log.info("Sous Classe " + sousClasse.getCodeNaf());

		log.info("Classe " + sousClasse.getClasse().getCodeNaf());

		log.info("Groupe " + sousClasse.getClasse().getGroupe().getCodeNaf());

		log.info("Division " + sousClasse.getClasse().getGroupe().getDivision().getCodeNaf());

		log.info("Section " + sousClasse.getClasse().getGroupe().getDivision().getSection().getCodeNaf());

		entityManager.clear();

		QueryCountHolder.clear();

		/*
		 * Deuxièmement passage pour vérification requêtes générées
		 */

		Optional<SousClasse> optionalSousClasse2 = sousClasseRepository.findByCodeNaf("sousclasse5");

		SousClasse sousClasse2 = optionalSousClasse.get();

		log.info("Sous Classe " + sousClasse2.getCodeNaf());

		log.info("Classe " + sousClasse2.getClasse().getCodeNaf());

		log.info("Groupe " + sousClasse2.getClasse().getGroupe().getCodeNaf());

		log.info("Division " + sousClasse2.getClasse().getGroupe().getDivision().getCodeNaf());

		log.info("Section " + sousClasse2.getClasse().getGroupe().getDivision().getSection().getCodeNaf());

		assertEquals(0, QueryCountHolder.getGrandTotal().getSelect());

	}

}
