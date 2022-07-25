package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

	@PersistenceUnit
	EntityManagerFactory entityManagerFactory;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuNouvelleNomenclatureAvecIndice();
			databaseIsInitialized = true;
		}

		QueryCountHolder.clear();

	}

	@Test
	public void testSectionCache() {

		List<Section> sections = sectionRepository.findAll();

		assertTrue(entityManagerFactory.getCache().contains(Section.class, 1));

	}

}
