package fr.insee.formation.hibernate.batch.helloWorld;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HelloWorldBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DeclarationRepository declarationRepository;

	@Bean
	public Job helloWorldJob() {
		return jobBuilderFactory.get("helloWorldJob").start(helloWorldTaskletStep()).build();
	}

	@Bean
	public TaskletStep helloWorldTaskletStep() {
		return stepBuilderFactory.get("helloWorldTaskletStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				log.info("Nombre de déclarations : {}", declarationRepository.count());
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

}
