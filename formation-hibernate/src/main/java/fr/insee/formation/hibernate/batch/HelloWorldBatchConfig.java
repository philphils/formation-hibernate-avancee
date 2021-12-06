package fr.insee.formation.hibernate.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.insee.formation.hibernate.repositories.DeclarationRepository;

@Configuration
public class HelloWorldBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DeclarationRepository declarationRepository;

	Logger logger = LoggerFactory.getLogger(HelloWorldBatchConfig.class);

	@Bean
	public Job helloWorldJob() {
		return jobBuilderFactory.get("helloWorldJob").start(helloWorldTaskletStep()).build();
	}

	@Bean
	public TaskletStep helloWorldTaskletStep() {
		return stepBuilderFactory.get("helloWorldTaskletStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				logger.info("Nombre de déclarations : {}", declarationRepository.count());
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

}
