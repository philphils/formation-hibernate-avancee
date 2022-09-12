package fr.insee.formation.hibernate.batch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.config.AbstractTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedressementMontantDeclarationBatchTest extends AbstractTest {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	Job redressementMontantDeclarationJob;

	@Test
	public void redressementMontantDeclarationBatchTest() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(creationJeuDonneesJob, jobParameters);

		assertEquals(jobExecution.getAllFailureExceptions().size(), 0);

		JobParameters jobParameters2 = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution2 = jobLauncher.run(redressementMontantDeclarationJob, jobParameters2);

		assertEquals(jobExecution2.getAllFailureExceptions().size(), 0);
	}

}
