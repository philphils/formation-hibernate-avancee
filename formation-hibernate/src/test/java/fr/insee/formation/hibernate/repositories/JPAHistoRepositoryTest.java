package fr.insee.formation.hibernate.repositories;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JPAHistoRepositoryTest extends AbstractTest {

	@Autowired
	private DeclarationRepository declarationRepository;

	@Autowired
	private DeclarationHistoRepository declarationHistoRepository;

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	TransactionTemplate transactionTemplate;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuxMappingAssociation();
			databaseIsInitialized = true;
		}

	}

	@Test
	public void testMethodeGeneric() {

		log.info("Récupération de l'identifiant d'une déclaration");

		Integer idDeclaration = declarationRepository.findAll().get(0).getId();

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Modification du montant de la déclaration d'id " + idDeclaration);

				Declaration declaration = declarationRepository.getById(idDeclaration);

				declaration.setMontant(declaration.getMontant() * 2);

			}
		});

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Modification du montant de la déclaration d'id " + idDeclaration);

				List<Declaration> revisions = declarationHistoRepository.getEntityRevisionsByProperties(idDeclaration,
						null, null, "montant");

				/*
				 * On doit récupérer 2 instances, une pour la création l'autre pour la mise à
				 * jour du montant
				 */
				assertEquals(2, revisions.size());

			}

		});

	}

}
