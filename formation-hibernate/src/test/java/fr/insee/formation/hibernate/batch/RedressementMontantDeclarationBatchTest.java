package fr.insee.formation.hibernate.batch;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedressementMontantDeclarationBatchTest extends AbstractTestIntegration {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job redressementMontantDeclarationJob;

	@Test
	public void redressementMontantDeclarationBatchTest() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(redressementMontantDeclarationJob, jobParameters);

	}

}
