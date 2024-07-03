package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;

@Slf4j
public class TP2RepositoryExo4Test extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	DeclarationRepository declarationRepository;

	@Autowired
	EntityManager entityManager;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeux3Secteurs();
			databaseIsInitialized = true;
		}

		ouvrirNouvelleTransaction();

		QueryCountHolder.clear();

	}

	@Test
	@Transactional
	public void testExercice4() {

		/*
		 * Récupération des déclarations
		 */
		Stream<Declaration> declarationsStream = declarationRepository
				.streamAllDeclarationWithEntrepriseWithSousClasse();

		Set<Declaration> declarations = declarationsStream.collect(Collectors.toSet());

		assertEquals(108, declarations.size());

		for (Declaration declaration : declarations) {

			String denomination = declaration.getEntreprise().getDenomination();

			String libelle = declaration.getEntreprise().getSousClasse().getLibelleNomenclature();

			log.debug("Entreprise {} du Secteur {}", denomination, libelle);

		}

		// TODO Comprendre pourquoi on a pas les requetes select pour les entreprises et
		// les secteur

		/*
		 * On vérifie qu'il n'y a bien eu qu'une seule requête effectuée
		 */
		assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

	}

}
