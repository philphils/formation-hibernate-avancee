package fr.insee.formation.hibernate.model;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import fr.insee.formation.hibernate.util.JeuxTestUtil;

public class DeclarationVersionControlTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private DeclarationRepository declarationRepository;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuxMappingAssociation();
			databaseIsInitialized = true;
		}
	}

	@Test
	public void testDeclarationVersionControl() throws InterruptedException, ExecutionException {

		// GIVEN
		Integer declarationId = declarationRepository.findAll().get(0).getId();

		final ExecutorService executor = Executors.newFixedThreadPool(2);

		// WHEN

		/*
		 * On lance 2 Thread qui modifient la même déclaration, avec un ayant une pause
		 * d'une seconde. On doit nécessairement récupérer une
		 * ObjectOptimisticLockingFailureException si les déclarations sont bien
		 * versionnées
		 */
		List<Future<Object>> futures = executor
				.invokeAll(Arrays.asList(() -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						Declaration declaration = declarationRepository.getById(declarationId);

						declaration.setMontant(120d);

						try {
							/*
							 * On fait un pause avec Thread.sleep(1000) pour être sûr que les 2 transactions
							 * se chevauchent
							 */
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}), () -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						Declaration declaration = declarationRepository.getById(declarationId);

						declaration.setMontant(140d);

					}

				})

				));

		// THEN
		try {
			futures.get(0).get();
			/*
			 * Si aucune Exception n'a été levée alors le versionnement n'est pas opérant
			 * --> le test échoue
			 */
			Assert.fail(
					"On aurait du avoir une ObjectOptimisticLockingFailureException levée, le contrôle de version n'a pas fonctionné");
		} catch (ExecutionException executionException) {
			Assert.assertEquals(ObjectOptimisticLockingFailureException.class,
					executionException.getCause().getClass());
		}

	}

}
