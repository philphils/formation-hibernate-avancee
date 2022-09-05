package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import fr.insee.formation.hibernate.model.nomenclature.Section;
import fr.insee.formation.hibernate.repositories.IndiceAnnuelRepository;
import fr.insee.formation.hibernate.repositories.SectionRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class NomenclatureTest extends AbstractTestIntegration {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private SectionRepository sectionRepository;

	@Autowired
	private IndiceAnnuelRepository indiceAnnuelRepository;

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
	public void testNouvelleNomenclature() {

		Optional<Section> optional = sectionRepository.findByCodeNaf("section1");

		assertTrue(optional.isPresent());

		Section section = optional.get();

		assertEquals(1, section.getDivisions().size());

		assertEquals(1, section.getDivisions().iterator().next().getGroupes().iterator().next().getClasses().iterator()
				.next().getSousClasses().size());

	}

	@Test
	@Transactional
	public void testNouvelleNomenclatureRecupIndice() {

		IndiceAnnuel indiceAnnuel = indiceAnnuelRepository.findAll().get(0);

		log.info(indiceAnnuel.getSousClasse().getLibelleNomenclature());

	}

}
