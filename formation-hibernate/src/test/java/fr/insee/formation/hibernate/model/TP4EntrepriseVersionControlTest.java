package fr.insee.formation.hibernate.model;

// ...existing code...

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
// ...existing code...
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
		Integer idEntreprise = entrepriseRepository.findAll().get(0).getId();
		// WHEN
		final ExecutorService executor = Executors.newFixedThreadPool(2);
		List<Future<Object>> futures = executor
				.invokeAll(Arrays.asList(() -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						ajouterDeclarationEntreprise(idEntreprise, 170d);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
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
			Assert.fail("On aurait du avoir une exception d'optimistic lock levée, le contrôle de version n'a pas fonctionné");
		} catch (ExecutionException executionException) {
			assertOptimisticLockExceptionInCauses(executionException);
		}
	}

	@Test
	public void testOptimisticForceIncrementPhantomRead() throws InterruptedException {
		// GIVEN
		Integer idEntreprise = entrepriseRepository.findAll().get(0).getId();
		// WHEN
		final ExecutorService executor = Executors.newFixedThreadPool(2);
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
			Assert.fail("On aurait du avoir une exception d'optimistic lock levée, le contrôle de version n'a pas fonctionné");
		} catch (Exception exception) {
			assertOptimisticLockExceptionInCauses(exception);
		}
	}
	/**
	 * Vérifie qu'une exception d'optimistic lock (ObjectOptimisticLockingFailureException ou OptimisticLockException)
	 * est présente dans la chaîne des causes de l'exception passée en paramètre.
	 */
	private void assertOptimisticLockExceptionInCauses(Throwable throwable) {
		Throwable cause = throwable;
		boolean found = false;
		while (cause != null) {
			if (cause instanceof org.springframework.orm.ObjectOptimisticLockingFailureException
					|| (cause.getClass().getName().equals("jakarta.persistence.OptimisticLockException"))) {
				found = true;
				break;
			}
			cause = cause.getCause();
		}
		if (!found) {
			throwable.printStackTrace();
		}
		Assert.assertTrue("Une exception d'optimistic lock doit être présente dans la chaîne des causes", found);
	}

}
