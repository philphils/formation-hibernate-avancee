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

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.histo.FormationRevisionEntity;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormationRevisionEntityTest extends AbstractTest {

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
	public void testFormationEntityRevision() {

		log.info("Récupération de l'identifiant d'une déclaration");

		Integer idDeclaration = declarationRepository.findAll().get(0).getId();

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info("Modification du montant de la déclaration d'id " + idDeclaration);

				Declaration declaration = declarationRepository.getById(idDeclaration);

				declaration.setMontant(declaration.getMontant() * 2);

				declaration.getEntreprise()
						.setDenomination(declaration.getEntreprise().getDenomination().toUpperCase());

			}
		});

		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {

			@Override
			public void accept(TransactionStatus t) {

				log.info(
						"Interrogation des données historiques pour vérifier qu'on a bien 2 lignes déclarations avc l'id "
								+ idDeclaration + ". (axe vertical) ");

				AuditReader auditReader = AuditReaderFactory.get(entityManager);

				AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(Declaration.class, false, true)
						.add(AuditEntity.id().eq(idDeclaration));

				List<Object[]> resultDeclarations = auditQuery.getResultList();

				assertEquals(2, resultDeclarations.size());

				FormationRevisionEntity revisionEntity = (FormationRevisionEntity) resultDeclarations.get(1)[1];

				assertEquals(true, revisionEntity.getModifiedEntityNames()
						.contains("fr.insee.formation.hibernate.model.Entreprise"));

				assertEquals(true, revisionEntity.getModifiedEntityNames()
						.contains("fr.insee.formation.hibernate.model.Declaration"));

				assertEquals("Test", revisionEntity.getContexte());

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

				Entreprise entreprise = (Entreprise) AuditReaderFactory.get(entityManager).createQuery()
						.forEntitiesModifiedAtRevision(Entreprise.class, revisions.get(1)).getSingleResult();

			}
		});

	}

}
