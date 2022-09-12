package fr.insee.formation.hibernate.batch;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedressementMontantDeclarationBatchTest extends AbstractTest {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	Job redressementMontantDeclarationJob;

	@Autowired
	SousClasseRepository sousClasseRepository;

	@Autowired
	DeclarationRepository declarationRepository;

	@Autowired
	TransactionTemplate transactionTemplate;

	@Test
	public void redressementMontantDeclarationBatchTest() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(creationJeuDonneesJob, jobParameters);

		assertEquals(jobExecution.getAllFailureExceptions().size(), 0);

		Entry<Declaration, Double> declarationEntry = transactionTemplate
				.execute(new TransactionCallback<Entry<Declaration, Double>>() {

					@Override
					public Entry<Declaration, Double> doInTransaction(TransactionStatus status) {

						Optional<SousClasse> sousClasse = sousClasseRepository.findByCodeNaf("02.10Z");

						/*
						 * On récupère une déclaration au hasard
						 */
						Declaration declaration = sousClasse.get().getEntreprises().iterator().next().getDeclarations()
								.entrySet().iterator().next().getValue();

						Double montantApresRedressement = declaration.getMontant()
								* declaration.getEntreprise().getCoeffRedressementEntreprise()
								* declaration.getEntreprise().getSousClasse().getCoeffRedressementNiveau();

						return new AbstractMap.SimpleEntry<Declaration, Double>(declaration, montantApresRedressement);

					}
				});

		JobParameters jobParameters2 = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution2 = jobLauncher.run(redressementMontantDeclarationJob, jobParameters2);

		assertEquals(jobExecution2.getAllFailureExceptions().size(), 0);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				Optional<Declaration> declaration2 = declarationRepository.findById(declarationEntry.getKey().getId());

				assertEquals(declarationEntry.getValue(), declaration2.get().getMontant());

			}
		});

	}

}
