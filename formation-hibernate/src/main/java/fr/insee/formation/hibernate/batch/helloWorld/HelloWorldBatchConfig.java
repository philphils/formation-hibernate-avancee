package fr.insee.formation.hibernate.batch.helloWorld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HelloWorldBatchConfig {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private DeclarationRepository declarationRepository;

	@Bean
	public Job helloWorldJob() {
		return new JobBuilder("helloWorldJob", jobRepository).start(helloWorldTaskletStep()).build();
	}

	@Bean
	public TaskletStep helloWorldTaskletStep() {
		return new StepBuilder("helloWorldTaskletStep", jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				log.info("Nombre de déclarations : {}", declarationRepository.count());
				return RepeatStatus.FINISHED;
			}
		}, platformTransactionManager).build();
	}

}
