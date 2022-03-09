package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class DeclarationRepositoryTest extends AbstractTest {

	@Autowired
	private DeclarationRepository declarationRepository;

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

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
	public void testCount() {

		QueryCountHolder.clear();

		Long total = declarationRepository.count();

		log.info("Nombre de déclarations en base : {}", total);

		log.info("Nombre de requête SELECT exécutées : {}", QueryCountHolder.getGrandTotal().getSelect());

		log.info("Hello World Test is working !!!");

	}

	@Test
	public void testFindByEntreprise() {

		QueryCountHolder.clear();

		Declaration declaration = declarationRepository.findAll().get(0);

		Set<Declaration> declarations = declarationRepository.findByEntreprise(declaration.getEntreprise());

		log.info("Nombre de déclarations de l'entreprise {} en base {}", declaration.getEntreprise().getDenomination(),
				declarations.size());

		Set<Declaration> declarations2 = declarationRepository.findByEntrepriseAndDate(declaration.getEntreprise(),
				declaration.getDate());

		assertEquals(1, declarations2.size());

		assertEquals(declaration.getId(), declarations2.iterator().next().getId());

		log.info("Nombre de requête SELECT exécutées : {}", QueryCountHolder.getGrandTotal().getSelect());

	}

	@Test
	public void testFindDeclarationWithEntreprise() {

		Declaration declaration = declarationRepository.findAll().get(0);

		Integer id = declaration.getId();

		entityManager.clear();

		QueryCountHolder.clear();

		Declaration declaration2 = declarationRepository.findByIdWithEntreprise(id).get();

		String denom = declaration2.getEntreprise().getDenomination();

		/*
		 * On vérifie que la déclaration a bien été récupérée avec 2 requêtes Pourquoi
		 * n'a-t-on pas une seule requête ? Proposer des amléiorations pour n'avoir
		 * qu'une requête
		 */
		assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
	}

}
