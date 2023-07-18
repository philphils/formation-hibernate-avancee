package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReaderFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntrepriseAuditRepositoryTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	private static boolean databaseIsInitialized = false;

	@Autowired
	EntrepriseRepository entrepriseRepository;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	EntityManager entityManager;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuxMappingAssociation();
			databaseIsInitialized = true;
		}

		log.info("Fin de la création du jeu d'essai");
	}

	@Test
	public void testCustomRepository() {

		log.info("Récupération de l'identifiant d'une entreprise");

		Integer idEntreprise = entrepriseRepository.findAll().get(0).getId();

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Modification de la dénomination de l'entreprise d'id " + idEntreprise);

				Optional<Entreprise> optionEntreprise = entrepriseRepository.findById(idEntreprise);

				assertTrue(optionEntreprise.isPresent());

				Entreprise entreprise = optionEntreprise.get();

				log.info("Changement de la dénomination : " + entreprise.getDenomination() + ", en : "
						+ entreprise.getDenomination().toUpperCase());

				entreprise.setDenomination(entreprise.getDenomination().toUpperCase());

			}
		});

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Récupération des révisions de l'entreprise d'id : " + idEntreprise + ". (axe vertical) ");

				List<Entreprise> entreprises = entrepriseRepository.findAllRevisionsOfEntreprise(idEntreprise);

				assertEquals(2, entreprises.size());

				entreprises.stream().forEach(ent -> assertEquals((int) idEntreprise, ent.getId()));

			}
		});

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Récupération des entreprise modifiées par une révision ");

				List<Number> revisions = AuditReaderFactory.get(entityManager).getRevisions(Entreprise.class,
						idEntreprise);

				List<Entreprise> entreprises = entrepriseRepository.findAllEntreprisesOfRevision(revisions.get(1));

				assertEquals(1, entreprises.size());

				assertEquals((int) idEntreprise, entreprises.get(0).getId());

			}
		});

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Récupération des révisions modifiant la dénomination d'une entreprise (pas les insertions) ");

				List<Object[]> entreprises = entrepriseRepository.findAllRevisionsModifyingDenomination();

				assertEquals(1, entreprises.size());

				assertEquals((int) idEntreprise, ((Entreprise) entreprises.get(0)[0]).getId());

			}
		});

	}

}
