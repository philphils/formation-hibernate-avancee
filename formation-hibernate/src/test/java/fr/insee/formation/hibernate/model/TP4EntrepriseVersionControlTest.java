package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertEquals;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TP4EntrepriseVersionControlTest extends AbstractTest {

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

	private Entreprise getEntrepriseWithOptimisticLockForceIncrement(Integer idEntreprise) {
		/*
		 * TODO : Remplacer null par l'appel à la méthode que vous aurez définie dans
		 * EntrepriseRepository
		 */
		return entrepriseRepository.getEntrepriseWithOptimistLockForceIncrement(idEntreprise);

	}

	private void ajouterDeclarationEntreprise(Integer idEntreprise, Double montant) {

		Entreprise entreprise = getEntrepriseWithOptimisticLockForceIncrement(idEntreprise);

		entrepriseServices.ajouterDeclarationEntreprise(entreprise, montant, new Date());

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
		 * ayant une pause d'une seconde. On veut récupérer une
		 * ObjectOptimisticLockingFailureException le verrou est correctement posé
		 * 
		 */
		List<Future<Object>> futures = executor
				.invokeAll(Arrays.asList(() -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						ajouterDeclarationEntreprise(idEntreprise, 170d);

						try {
							/*
							 * On fait un pause avec Thread.sleep(100) pour être sûr que les 2 transactions
							 * se chevauchent
							 */
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}), () -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						ajouterDeclarationEntreprise(idEntreprise, 150d);

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

	@Test
	public void testOptimisticForceIncrementPhantomRead() throws InterruptedException {

		// GIVEN
		/*
		 * On récupère un identifiant d'entreprise
		 */
		Integer idEntreprise = entrepriseRepository.findAll().get(0).getId();

		// WHEN
		final ExecutorService executor = Executors.newFixedThreadPool(2);

		/*
		 * On lance 2 Thread qui ajoutent une déclaration à la même entreprise, avec un
		 * ayant une pause d'une seconde. On veut récupérer une
		 * ObjectOptimisticLockingFailureException le verrou est correctement posé
		 * 
		 */
		List<Future<Object>> futures = executor
				.invokeAll(Arrays.asList(() -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						Entreprise entreprise = getEntrepriseWithOptimisticLockForceIncrement(idEntreprise);

						entrepriseServices.ajouterDeclarationEntreprise(entreprise, 170d, new Date());

					}

				}), () -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						Entreprise entreprise = getEntrepriseWithOptimisticLockForceIncrement(idEntreprise);

					}

				})

				));

		// THEN
		try {
			futures.get(0).get();
			futures.get(1).get();

			/*
			 * Si aucune Exception n'a été levée alors le versionnement n'est pas opérant
			 * --> le test échoue
			 */
			Assert.fail(
					"On aurait du avoir une ObjectOptimisticLockingFailureException levée, le contrôle de version n'a pas fonctionné");
		} catch (Exception exception) {
			assertEquals("On devrait avoir une ObjectOptimisticLockingFailureException",
					ObjectOptimisticLockingFailureException.class, exception.getCause().getClass());
		}

	}

}
