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
import fr.insee.formation.hibernate.config.AbstractTestIntegration;
import fr.insee.formation.hibernate.repositories.IndiceAnnuelRepository;
import fr.insee.formation.hibernate.repositories.IndiceMensuelRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TP1CreationJeuDonneesBatchTest extends AbstractTestIntegration {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	IndiceAnnuelRepository indiceAnnuelRepository;

	@Autowired
	IndiceMensuelRepository indiceMensuelRepository;

	@Autowired
	SousClasseRepository sousClasseRepository;

	@Test
	public void creationJeuDonneesTest() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(creationJeuDonneesJob, jobParameters);

		assertEquals(jobExecution.getAllFailureExceptions().size(), 0);

		assertEquals(5, sousClasseRepository.count());

		assertEquals(20, indiceAnnuelRepository.count());

		assertEquals(180, indiceMensuelRepository.count());
	}

}
