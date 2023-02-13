package fr.insee.formation.hibernate.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.batch.calculIndices.CalculIndicesMensuelsProcessor;
import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.IndiceMensuelRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TP4IndiceConcurrenceControlTest extends AbstractTestIntegration {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	Job calculIndicesJob;

	@Autowired
	IndiceMensuelRepository indiceMensuelRepository;

	@Autowired
	SousClasseRepository sousClasseRepository;

	@Autowired
	EntityManagerFactory entityManagerFactory;

	private static Object attendreRecupIndice = new Object();

	@Autowired
	CalculIndicesMensuelsProcessor calculIndicesMensuelsProcessor;

	@Autowired
	TransactionTemplate transactionTemplate;

	private void calculIndice(Integer idIndice, Date mois, Integer idSousClasse) throws Exception {
		/*
		 * TODO : Remplacer par l'appel à votre méthode qui pose un verrou en écriture
		 * sur l'indice
		 */
		IndiceMensuel indiceMensuel = indiceMensuelRepository.getIndiceMensuelWithPessimisticWriteLock(idIndice);

		/*
		 * TODO : Remplacer par l'appel à votre méthode qui pose un verrou en lecture
		 * sur la sous-classe, avec ses entreprises et les déclarations de la date
		 * concernée
		 */
		SousClasse sousClasse = sousClasseRepository.findForCalculIndiceWithPessimisticReadLock(idSousClasse, mois);

		/*
		 * On synchronise les threads pour être sûr que les verrous sont posés avant
		 * l'exécution des autres Thread
		 */
		synchronized (attendreRecupIndice) {
			attendreRecupIndice.notifyAll();
		}

		indiceMensuel = calculIndicesMensuelsProcessor.process(indiceMensuel);
	}

	private void waitRecupIndice() {
		try {
			/*
			 * On synchronise les threads pour être sûr que les verrous sont posés avant
			 * l'exécution des autres Thread
			 */
			synchronized (attendreRecupIndice) {
				attendreRecupIndice.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testCalculIndices() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(creationJeuDonneesJob, jobParameters);

		assertEquals(jobExecution.getAllFailureExceptions().size(), 0);

		IndiceMensuel indiceMensuelExample = indiceMensuelRepository.findAll().get(0);

		Integer idIndice = indiceMensuelExample.getId();

		YearMonth yearMonth = indiceMensuelExample.getMonth();

		Integer idSousClasse = indiceMensuelExample.getSousClasse().getId();

		final ExecutorService executor = Executors.newFixedThreadPool(5);

		Long startTime = System.currentTimeMillis();

		transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);

		List<Future<Object>> futures = executor.invokeAll(Arrays.asList(

				/*
				 * Thread 1 : On Calcule l'Indice en posant un verrou en écriture sur l'indice
				 * et un verrou en lecture sur la sous-classe, les entreprises et leurs
				 * declarations. On fait un Thread.sleep(10000) pour que cette opération dure 10
				 * secondes ce qui nous permet de tester les verrous avec les autres Thread.
				 */
				() -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {

						log.info("Début Thread 1");

						try {
							Thread.sleep(100);

							calculIndice(idIndice,
									Date.from(yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
									idSousClasse);

							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
							Assert.fail();
						}

						log.info("Fin Thread 1");

					}

				}),

				/*
				 * Thread 2 : On essaie de modifier la valeur de l'Indice (impossible
				 * normalement)
				 */
				() -> transactionTemplate.execute(new TransactionCallback<Long>() {

					@Override
					public Long doInTransaction(TransactionStatus status) {

						log.info("Début Thread 2");

						waitRecupIndice();

						IndiceMensuel indiceMensuel = indiceMensuelRepository.findById(idIndice).get();

						indiceMensuel.setValeur(18000d);

						status.flush();

						log.info("Affichage pour s'assurer qu'on a récupéré l'indice " + indiceMensuel.getMonth());

						log.info("Fin Thread 2");

						return System.currentTimeMillis() - startTime;
					}

				}),

				/*
				 * Thread 3 : On essaie de poser un verrou en lecture sur l'Indice (impossible
				 * normalement)
				 */
				() -> transactionTemplate.execute(new TransactionCallback<Long>() {

					@Override
					public Long doInTransaction(TransactionStatus status) {

						log.info("Début Thread 3");

						waitRecupIndice();

						EntityManager entityManager = EntityManagerFactoryUtils
								.getTransactionalEntityManager(entityManagerFactory);

						Query query = entityManager
								.createQuery("SELECT indice FROM IndiceMensuel indice WHERE indice.id = :id");

						query.setParameter("id", idIndice);

						query.setLockMode(LockModeType.PESSIMISTIC_READ);

						IndiceMensuel indiceMensuel = (IndiceMensuel) query.getSingleResult();

						log.info("Affichage pour s'assurer qu'on a récupéré l'indice " + indiceMensuel.getMonth());

						log.info("Fin Thread 3");

						return System.currentTimeMillis() - startTime;
					}

				}),

				/*
				 * Thread 4 : On essaie de modifier les Declarations des Entreprise de la
				 * SousClasse concernée (impossible normalement)
				 */
				() -> transactionTemplate.execute(new TransactionCallback<Long>() {

					@Override
					public Long doInTransaction(TransactionStatus status) {

						log.info("Début Thread 4");

						waitRecupIndice();

						SousClasse sousClasse = sousClasseRepository.getById(idSousClasse);

						Entreprise entreprise = sousClasse.getEntreprises().iterator().next();

						Declaration declaration = entreprise.getDeclarationsByYearMonth(yearMonth);

						declaration.setMontant(declaration.getMontant() + 1000d);

						status.flush();

						log.info("Fin Thread 4");

						return System.currentTimeMillis() - startTime;
					}

				}),

				/*
				 * Thread 5 : On essaie de poser un verrou en lecture sur une Declaration utile
				 * au calcul de l'Indice (possible normalement)
				 */
				() -> transactionTemplate.execute(new TransactionCallback<Long>() {

					@Override
					public Long doInTransaction(TransactionStatus status) {

						log.info("Début Thread 5");

						waitRecupIndice();

						SousClasse sousClasse = sousClasseRepository.getById(idSousClasse);

						Entreprise entreprise = sousClasse.getEntreprises().iterator().next();

						Declaration declaration = entreprise.getDeclarations().values().iterator().next();

						EntityManager entityManager = EntityManagerFactoryUtils
								.getTransactionalEntityManager(entityManagerFactory);

						Query query = entityManager.createQuery(
								"SELECT declaration FROM Declaration declaration WHERE declaration.id = :id");

						query.setParameter("id", declaration.getId());

						query.setLockMode(LockModeType.PESSIMISTIC_READ);

						status.flush();

						log.info("Fin Thread 5");

						return System.currentTimeMillis() - startTime;
					}

				})

		));

		// THEN
		try {

			futures.get(0).get();

			Long timeRecupIndiceThread2 = (Long) futures.get(1).get();

			Long timeRecupIndiceThread3 = (Long) futures.get(2).get();

			Long timeRecupIndiceThread4 = (Long) futures.get(3).get();

			Long timeRecupIndiceThread5 = (Long) futures.get(4).get();

			/*
			 * On doit avoir une TimeoutException
			 */
			log.info("Temps d'exécution du thread 2 : " + timeRecupIndiceThread2 + "ms");
			log.info("Temps d'exécution du thread 3 : " + timeRecupIndiceThread3 + "ms");
			log.info("Temps d'exécution du thread 4 : " + timeRecupIndiceThread4 + "ms");
			log.info("Temps d'exécution du thread 5 : " + timeRecupIndiceThread5 + "ms");

			assertTrue(
					"On devrait avoir un temps d'exécution d'au moins 5 secondes : l'Indice n'est pas verrouillé on peut le modifier",
					timeRecupIndiceThread2 > 5000l);

			assertTrue(
					"On devrait avoir un temps d'exécution d'au moins 5 secondes : on peut poser des verrous en lecture sur l'Indice ",
					timeRecupIndiceThread3 > 5000l);

			assertTrue(
					"On devraitdoit avoir un temps d'exécution d'au moins 5 secondes : on peut modifier les déclarations des entreprises de la sous-classe pendant le calcul de l'indice",
					timeRecupIndiceThread4 > 5000l);

			assertTrue(
					"On devrait avoir un temps d'exécution rapide (< à 1s) : on ne peut pas poser de verrous en lecture sur les données (déclarations) permettant le calcul de l'indice",
					timeRecupIndiceThread5 < 1000l);

		} catch (ExecutionException executionException) {
			log.error("Problème à l'exécution du test", executionException);
			Assert.fail();
		}

	}

}