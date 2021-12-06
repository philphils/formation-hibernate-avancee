package fr.insee.formation.hibernate.repositories;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import fr.insee.formation.hibernate.config.TestConfiguration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import net.ttddyy.dsproxy.QueryCountHolder;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DeclarationRepositoryTest extends TestConfiguration {

	@Autowired
	private DeclarationRepository declarationRepository;

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private DataSource dataSource;

	Logger logger = LoggerFactory.getLogger(DeclarationRepositoryTest.class);

	@Before
	public void testMappingAssociation() {
		jeuxTestUtil.creerJeuxMappingAssociation();
	}

	@Test
	public void testCount() {

		QueryCountHolder.clear();

		Long total = declarationRepository.count();

		logger.info("Nombre de déclarations en base : {}", total);

		logger.info("Nombre de requête SELECT exécutées : {}", QueryCountHolder.getGrandTotal().getSelect());

	}

}
