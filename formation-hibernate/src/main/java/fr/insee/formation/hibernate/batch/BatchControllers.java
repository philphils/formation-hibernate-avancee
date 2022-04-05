package fr.insee.formation.hibernate.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchControllers {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job helloWorldJob;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	Job redressementMontantDeclarationJob;

	@RequestMapping("/helloWorldJob")
	public String helloWorldJob() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(helloWorldJob, jobParameters);

		return "Hello World Batch is working !!!";
	}

	@RequestMapping("/CreationJeuDonneesJob")
	public String creationJeuDonneesJob() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(creationJeuDonneesJob, jobParameters);

		return "Données correctement créées";
	}

	@RequestMapping("/RedressementMontantDeclarationJob")
	public String redressementMontantDeclarationJob() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(redressementMontantDeclarationJob, jobParameters);

		return "Données correctement créées";
	}

}
