package fr.insee.formation.hibernate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;

@SpringBootTest
@ActiveProfiles(value = "test")
//@ContextConfiguration(locations = { "/spring-test-datasource.xml", "/spring-core.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DeclarationRepositoryTest {

	@Autowired
	private DeclarationRepository declarationRepository;
	
	@Autowired
	private JeuxTestUtil jeuxTestUtil;
	
	@Before
	public void testMappingAssociation() {
		jeuxTestUtil.creerJeuxMappingAssociation();
	}
	
	@Test
	public void testCount() {
		
		Long total = declarationRepository.count();
		
		System.out.println("I y a " + total + " déclarations");
		
	}

}
