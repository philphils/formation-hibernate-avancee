package fr.insee.formation.hibernate.repositories;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import fr.insee.formation.hibernate.config.AbstractTestConfiguration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DeclarationRepositoryTest extends AbstractTestConfiguration {

	@Autowired
	private DeclarationRepository declarationRepository;

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private DataSource dataSource;

	@Before
	public void testMappingAssociation() {
		jeuxTestUtil.creerJeuxMappingAssociation();
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
