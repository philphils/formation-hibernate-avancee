package fr.insee.formation.hibernate.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BatchControllers {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job helloWorldJob;

	@Autowired
	Job creationJeuDonneesJob;

	@RequestMapping("/helloWorldJob")
	public String helloWorldJob() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(helloWorldJob, jobParameters);

		return returnMessageIfNotFailed("Hello World Batch is working !!!", jobExecution);
	}

	@RequestMapping("/CreationJeuDonneesJob")
	public String creationJeuDonneesJob() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(creationJeuDonneesJob, jobParameters);

		return returnMessageIfNotFailed("Données correctement créées", jobExecution);
	}

	private String returnMessageIfNotFailed(String message, JobExecution jobExecution) {
		if (jobExecution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED.getExitCode()))
			return message;
		else
			return "Le batch ne s'est pas terminé correctement. Consulter la log pour plus d'info. ExitStatus : "
					+ jobExecution.getExitStatus();
	}

}
