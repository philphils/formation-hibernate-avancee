package fr.insee.formation.hibernate.repositories;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class DeclarationRepositoryTest extends AbstractTestIntegration {

	@Autowired
	private DeclarationRepository declarationRepository;

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuxMappingAssociation();
			databaseIsInitialized = true;
		}
	}

	@Test
	public void testCount() {

		QueryCountHolder.clear();

		Long total = declarationRepository.count();

		log.info("Nombre de déclarations en base : {}", total);

		log.info("Nombre de requête SELECT exécutées : {}", QueryCountHolder.getGrandTotal().getSelect());

		log.info("Hello World Test is working !!!");

	}

}
