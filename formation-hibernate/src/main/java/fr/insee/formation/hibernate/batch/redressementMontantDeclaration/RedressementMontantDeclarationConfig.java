package fr.insee.formation.hibernate.batch.redressementMontantDeclaration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.insee.formation.hibernate.batch.listener.ChunkTimingListener;
import fr.insee.formation.hibernate.batch.utils.ChunkingStreamTasklet;
import fr.insee.formation.hibernate.batch.utils.JPAUpdateWriter;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedressementMontantDeclarationConfig {

	@Value("${batch.chunkSizeRedressement}")
	private Integer chunkSize;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	DeclarationRepository declarationRepository;

	@Bean
	public ItemReader<Declaration> redressementItemReader() {
		return new RedressementItemReader();
	}

	@Bean
	public ItemProcessor<Declaration, Declaration> redressementItemProcessor() {
		return new RedressementProcessor();
	}

	@Bean
	public ItemWriter<Declaration> redressementItemWriter() {
		return new JPAUpdateWriter<Declaration>();
	}

	@Bean
	protected Step redressementProcessLines(ItemReader<Declaration> reader,
			ItemProcessor<Declaration, Declaration> processor, ItemWriter<Declaration> writer) {
		return
		//// @formatter:off
				steps
					.get("redressementProcessLines")
					.<Declaration, Declaration>chunk(chunkSize)
					.reader(reader)
					.processor(processor)
					.writer(writer)
					.listener(new ChunkTimingListener(chunkSize))
				.build();
		// @formatter:on

	}

	@Bean
	public Job redressementMontantDeclarationJob() {
		return
		//// @formatter:off
			jobs
				.get("chunksJob")
				.start(redressementProcessLines(redressementItemReader(), redressementItemProcessor(), redressementItemWriter()))
			.build();
		// @formatter:on

	}

	@Bean
	public Tasklet redressementTasklet() {
		return new ChunkingStreamTasklet<Declaration, Declaration>(
				declarationRepository::streamAllDeclarationWithEntrepriseWithSecteur, redressementItemProcessor(),
				redressementItemWriter(), chunkSize, false);
	}

	@Bean
	protected Step redressementStreamProcessLines() {
		return steps.get("processLines").tasklet(redressementTasklet()).build();
	}

	@Bean
	public Job redressementMontantDeclarationStreamJob() {
		return
		//// @formatter:off
			jobs
				.get("chunksJob")
				.start(redressementStreamProcessLines())
			.build();
		// @formatter:on

	}

}
