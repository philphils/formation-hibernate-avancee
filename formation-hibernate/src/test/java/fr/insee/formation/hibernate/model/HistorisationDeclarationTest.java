package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HistorisationDeclarationTest extends AbstractTestIntegration {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	private static boolean databaseIsInitialized = false;

	@Autowired
	DeclarationRepository declarationRepository;

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
	public void testHistorisationDeclaration() {

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

				log.info(
						"Interrogation des données historiques pour vérifier qu'on a bien 2 lignes déclarations avc l'id "
								+ idDeclaration + ". (axe vertical) ");

				AuditReader auditReader = AuditReaderFactory.get(entityManager);

				AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(Declaration.class, true, true)
						.add(AuditEntity.id().eq(idDeclaration));

				List<Declaration> declarations = auditQuery.getResultList();

				assertEquals(2, declarations.size());

				assertEquals(2d, declarations.get(1).getMontant() / declarations.get(0).getMontant(), 0.001d);

			}
		});

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info(
						"Interrogation des données historiques pour connaître les entités modifiées pour 1 révision données. (axe horizontal) ");

				List<Number> revisions = AuditReaderFactory.get(entityManager).getRevisions(Declaration.class,
						idDeclaration);

				assertEquals(2, revisions.size());

				Declaration declaration = (Declaration) AuditReaderFactory.get(entityManager).createQuery()
						.forEntitiesAtRevision(Declaration.class, revisions.get(1))
						.add(AuditEntity.id().eq(idDeclaration)).getSingleResult();

			}
		});

	}

}
