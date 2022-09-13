package fr.insee.formation.hibernate.batch.calculIndices;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import fr.insee.formation.hibernate.batch.utils.ChunkingStreamTasklet;
import fr.insee.formation.hibernate.batch.utils.JPAPersistWriter;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.IndiceAnnuelRepository;
import fr.insee.formation.hibernate.repositories.IndiceMensuelRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource(value = "classpath:batch.properties")
public class CalculIndicesBatchConfig {

	@Value("${batch.chunkSizeCalculIndices}")
	private Integer chunkSizeCalcul;

	@Value("${batch.affichageCalculIndices}")
	private Integer affichageCalculIndices;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	SousClasseRepository sousClasseRepository;

	@Autowired
	IndiceAnnuelRepository indiceAnnuelRepository;

	@Autowired
	IndiceMensuelRepository indiceMensuelRepository;

	@Autowired
	JPAPersistWriter<SousClasse> jpaPersistWriter;

	@Autowired
	IndiceMensuelValeurUpdateWriter indiceMensuelValeurUpdateWriter;

	@Bean
	public Tasklet remiseAZeroIndicesTasklet() {

		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				indiceAnnuelRepository.remiseZeroInOneQuery();
				indiceMensuelRepository.remiseZeroInOneQuery();

				return null;
			}
		};

	}

	@Bean
	public ItemProcessor<IndiceMensuel, IndiceMensuel> itemCalculIndicesProcessor() {
		return new CalculIndicesProcessor();
	}

	@Bean
	public Tasklet calculIndicesTasklet() {

		ChunkingStreamTasklet taskletCalculIndices = new ChunkingStreamTasklet<IndiceMensuel, IndiceMensuel>(
				indiceMensuelRepository::findAllIndicesMensuelWithSousClasseAndEntrepriseAndDeclaration,
				itemCalculIndicesProcessor(), indiceMensuelValeurUpdateWriter, chunkSizeCalcul, true);

		taskletCalculIndices.setAffichageLogCompteur(affichageCalculIndices);

		return taskletCalculIndices;

	}

	@Bean
	public Step remiseAZeroIndicesStep(Tasklet tasklet) {

		return steps.get("remiseAZeroIndicesStep").tasklet(tasklet).build();

	}

	@Bean
	public Step calculIndicesStep() {

		return
		//// @formatter:off
						steps
							.get("calculIndicesStep")
							.tasklet(calculIndicesTasklet())
						.build();
				// @formatter:on

	}

	@Bean
	public Job calculIndicesJob() {
		return
		//// @formatter:off
					jobs
						.get("calculIndicesJob")
						.start(remiseAZeroIndicesStep(remiseAZeroIndicesTasklet()))
						.next(calculIndicesStep())
					.build();
				// @formatter:on

	}

}
