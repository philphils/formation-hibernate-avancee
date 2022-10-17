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

import fr.insee.formation.hibernate.batch.listener.TimingItemProcessListener;
import fr.insee.formation.hibernate.batch.utils.ChunkingStreamTasklet;
import fr.insee.formation.hibernate.batch.utils.JPAPersistWriter;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
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

	@Autowired
	IndiceAnnuelValeurUpdateWriter indiceAnnuelValeurUpdateWriter;

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
	public ItemProcessor<IndiceMensuel, IndiceMensuel> itemCalculIndicesMensuelsProcessor() {
		return new CalculIndicesMensuelsProcessor();
	}

	@Bean
	public ItemProcessor<IndiceAnnuel, IndiceAnnuel> itemCalculIndicesAnnuelsProcessor() {
		return new CalculIndicesAnnuelsProcessor();
	}

	@Bean
	public Tasklet calculIndicesMensuelsTasklet() {

		ChunkingStreamTasklet taskletCalculIndicesMensuels = new ChunkingStreamTasklet<IndiceMensuel, IndiceMensuel>(
				indiceMensuelRepository::streamAllIndicesMensuelWithSousClasseAndEntrepriseAndDeclaration,
				itemCalculIndicesMensuelsProcessor(), indiceMensuelValeurUpdateWriter, chunkSizeCalcul, true);

//		taskletCalculIndicesMensuels.setAffichageLogCompteur(affichageCalculIndices);

		taskletCalculIndicesMensuels.addItemProcessListener(new TimingItemProcessListener(affichageCalculIndices));

		return taskletCalculIndicesMensuels;

	}

	@Bean
	public Tasklet calculIndicesAnnuelsTasklet() {

		ChunkingStreamTasklet taskletCalculIndicesAnnuels = new ChunkingStreamTasklet<IndiceAnnuel, IndiceAnnuel>(
				indiceAnnuelRepository::streamAllIndicesAnnuelsWithSousClasseAndEntrepriseAndDeclaration,
				itemCalculIndicesAnnuelsProcessor(), indiceAnnuelValeurUpdateWriter, chunkSizeCalcul, true);

//		taskletCalculIndicesAnnuels.setAffichageLogCompteur(affichageCalculIndices);

		taskletCalculIndicesAnnuels.addItemProcessListener(new TimingItemProcessListener(affichageCalculIndices));

		return taskletCalculIndicesAnnuels;

	}

	@Bean
	public Step remiseAZeroIndicesStep(Tasklet tasklet) {

		return steps.get("remiseAZeroIndicesStep").tasklet(tasklet).build();

	}

	@Bean
	public Step calculIndicesMensuelsStep() {

		return
		//// @formatter:off
						steps
							.get("calculIndicesMensuelsStep")
							.tasklet(calculIndicesMensuelsTasklet())
						.build();
		// @formatter:on

	}

	@Bean
	public Step calculIndicesAnnuelsStep() {

		return
		//// @formatter:off
						steps
							.get("calculIndicesAnnuelsStep")
							.tasklet(calculIndicesAnnuelsTasklet())
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
						.next(calculIndicesMensuelsStep())
						.next(calculIndicesAnnuelsStep())
					.build();
		// @formatter:on

	}

}
