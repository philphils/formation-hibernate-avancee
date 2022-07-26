package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TestTransaction;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.nomenclature.Section;
import fr.insee.formation.hibernate.repositories.SectionRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class NomenclatureCacheSecondNiveauTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private SectionRepository sectionRepository;

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

		QueryCountHolder.clear();

	}

	@Test
	@Transactional
	public void testSectionCache() {

		List<Section> sections = sectionRepository.findAll();

		assertTrue(entityManagerFactory.getCache().contains(Section.class, 1));

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
		assertEquals(QueryCountHolder.getGrandTotal().getTotal(), 1);

	}

}
