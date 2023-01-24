package fr.insee.formation.hibernate.model;

import java.util.Arrays;
import java.util.Date;
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
import fr.insee.formation.hibernate.repositories.EntrepriseRepository;
import fr.insee.formation.hibernate.services.IEntrepriseServices;
import fr.insee.formation.hibernate.util.JeuxTestUtil;

public class EntrepriseVersionControlTest extends AbstractTest {

	@Autowired
	private JeuxTestUtil jeuxTestUtil;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private IEntrepriseServices entrepriseServices;

	@Autowired
	private EntrepriseRepository entrepriseRepository;

	private static boolean databaseIsInitialized = false;

	@Before
	public void testMappingAssociation() {
		if (!databaseIsInitialized) {
			jeuxTestUtil.creerJeuxMappingAssociation();
			databaseIsInitialized = true;
		}
	}

	@Test
	public void testOptimisticForceIncrement() throws InterruptedException {

		// GIVEN
		/*
		 * On récupère un identifiant d'entreprise
		 */
		Integer idEntreprise = entrepriseRepository.findAll().get(0).getId();

		// WHEN
		final ExecutorService executor = Executors.newFixedThreadPool(2);

		/*
		 * On lance 2 Thread qui ajoutent une déclaration à la même entreprise, avec un
		 * ayant une pause d'une seconde. On doit nécessairement récupérer une
		 * ObjectOptimisticLockingFailureException si l'option FORCE_INCREMENT est bien
		 * activée
		 * 
		 */
		List<Future<Object>> futures = executor
				.invokeAll(Arrays.asList(() -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						Entreprise entreprise = entrepriseRepository
								.getEntrepriseWithOptimisticLockForceIncrement(idEntreprise);

						entrepriseServices.ajouterDeclarationEntreprise(entreprise, 150d, new Date());

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}), () -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						// TODO Auto-generated method stub

						Entreprise entreprise = entrepriseRepository
								.getEntrepriseWithOptimisticLockForceIncrement(idEntreprise);

						entrepriseServices.ajouterDeclarationEntreprise(entreprise, 170d, new Date());

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
			if (!(executionException.getCause() instanceof ObjectOptimisticLockingFailureException)) {
				executionException.printStackTrace();
			}
			Assert.assertEquals(ObjectOptimisticLockingFailureException.class,
					executionException.getCause().getClass());
		}

	}

}
