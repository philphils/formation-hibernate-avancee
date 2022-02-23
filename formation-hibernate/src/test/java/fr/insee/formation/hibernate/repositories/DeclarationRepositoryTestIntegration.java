package fr.insee.formation.hibernate.repositories;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeclarationRepositoryTestIntegration extends AbstractTestIntegration {

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
	@Transactional
	public void testSelectAllDeclarations() {

		List<Declaration> declarations = declarationRepository.findAll();

		for (Declaration declaration : declarations) {
			declaration.setMontant(declaration.getMontant() * 110 / 100);
		}

	}

}
