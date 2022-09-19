package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

		ouvrirNouvelleTransaction();

		QueryCountHolder.clear();

	}

	@Test
	@Transactional
	public void testCount() {

		Long total = declarationRepository.count();

		log.info("Nombre de déclarations en base : {}", total);

		log.info("Nombre de requête SELECT exécutées : {}", QueryCountHolder.getGrandTotal().getSelect());

		log.info("Hello World Test is working !!!");

	}

	@Test
	@Transactional
	public void testFindByEntreprise() {

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
	@Transactional
	public void testFindDeclarationWithEntreprise() {

		Declaration declaration = declarationRepository.findAll().get(0);

		Integer id = declaration.getId();

		entityManager.clear();

		QueryCountHolder.clear();

		Declaration declaration2 = declarationRepository.findByIdWithEntreprise(id).get();

		String denom = declaration2.getEntreprise().getDenomination();

		assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
	}

	@Test
	@Transactional
	public void testFindAllPage() {

		Page<Declaration> page = declarationRepository.findAll(PageRequest.of(0, 2, Sort.by("date")));

		while (page.hasNext()) {

			log.info("Il y a {} declaration dans la page n°{}", page.getNumberOfElements(), page.getNumber());

			log.info("Déclarations d'identifiant : {}",
					page.get().map(d -> Integer.toString(d.getId())).collect(Collectors.joining(",")));

			page = declarationRepository.findAll(page.nextPageable());

		}

	}

	@Test
	@Transactional
	public void testFindDeclarationByEntreprisePage() {

		Declaration declaration = declarationRepository.findAll().get(0);

		Page<Declaration> pageDeclarations = declarationRepository.findByEntreprise(declaration.getEntreprise(),
				PageRequest.of(0, 3, Sort.by("date")));

		while (pageDeclarations.hasNext()) {

			log.info("Il y a {} declaration dans la page n°{}", pageDeclarations.getNumberOfElements(),
					pageDeclarations.getNumber());

			log.info("Déclarations d'identifiant : {}",
					pageDeclarations.get().map(d -> Integer.toString(d.getId())).collect(Collectors.joining(",")));

			pageDeclarations = declarationRepository.findByEntreprise(declaration.getEntreprise(),
					pageDeclarations.nextPageable());

		}

	}

	@Test
	@Transactional
	public void testLazy() {

		Declaration declaration = declarationRepository.findAll().get(0);

		log.info("{} {}", declaration.getEntreprise().getDenomination(),
				declaration.getEntreprise().getSousClasse().getLibelleNomenclature());

		/*
		 * On vérifie que la déclaration a bien été récupérée avec 3 requêtes Pourquoi
		 * n'a-t-on pas une seule requête ? Proposer des amléiorations pour n'avoir
		 * qu'une requête
		 */
		assertEquals(3, QueryCountHolder.getGrandTotal().getSelect());

	}

}
