package fr.insee.formation.hibernate.batch.calculIndices;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Sort.Direction;

import fr.insee.formation.hibernate.batch.utils.ChunkingStreamTasklet;
import fr.insee.formation.hibernate.batch.utils.JPACollectionPersistWriter;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import fr.insee.formation.hibernate.repositories.IndiceAnnuelRepository;
import fr.insee.formation.hibernate.repositories.IndiceMensuelRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource(value = "classpath:batch.properties")
public class CalculIndicesBatchConfig {

	@Value("${batch.chunkSizeCreationIndices}")
	private Integer chunkSizeCreation;

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
	JPACollectionPersistWriter jpaCollectionPersistWriter;

	@Autowired
	IndiceValeurUpdateWriter indiceValeurUpdateWriter;

	@Autowired
	DeclarationRepository declarationRepository;

	@Bean
	public Tasklet suppressionIndicesTasklet() {

		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				indiceAnnuelRepository.deleteAllInOneQuery();
				indiceMensuelRepository.deleteAllInOneQuery();

				return null;
			}
		};

	}

	@Bean
	public ItemReader<SousClasse> itemReaderCreationIndicesSousClasse() {
		RepositoryItemReader<SousClasse> itemReaderSousClasse = new RepositoryItemReader<SousClasse>();

		itemReaderSousClasse.setRepository(sousClasseRepository);
		itemReaderSousClasse.setMethodName("findAll");
		itemReaderSousClasse.setSort(Map.of("codeNaf", Direction.ASC));

		return itemReaderSousClasse;
	}

	@Bean
	public ItemProcessor<SousClasse, Set<Indice>> itemCreationIndicesProcessor() {
		return new CreationIndicesProcessor();
	}

	@Bean
	public ItemProcessor<Declaration, Entry<IndiceMensuel, IndiceAnnuel>> itemCalculIndicesProcessor() {
		return new CalculIndicesProcessor();
	}

	@Bean
	public Tasklet calculIndicesTasklet() {

		ChunkingStreamTasklet taskletCalculIndices = new ChunkingStreamTasklet<Declaration, Entry<IndiceMensuel, IndiceAnnuel>>(
				declarationRepository::streamAllDeclarationWithEntrepriseAndSousClasseAndIndices,
				itemCalculIndicesProcessor(), indiceValeurUpdateWriter, chunkSizeCalcul, true);

		taskletCalculIndices.setAffichageLogCompteur(affichageCalculIndices);

		return taskletCalculIndices;

	}

	@Bean
	public Step suppressionIndicesStep(Tasklet tasklet) {

		return steps.get("suppressionIndicesStep").tasklet(tasklet).build();

	}

	@Bean
	public Step creationIndicesStep(ItemReader<SousClasse> reader, ItemProcessor<SousClasse, Set<Indice>> processor,
			ItemWriter<Set<Indice>> writer) {
		return
		//// @formatter:off
				steps
					.get("creationIndicesStep")
					.<SousClasse, Set<Indice>>chunk(chunkSizeCreation)
					.reader(reader)
					.processor(processor)
					.writer(writer)
				.build();
		// @formatter:on

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
						.start(suppressionIndicesStep(suppressionIndicesTasklet()))
						.next(creationIndicesStep(itemReaderCreationIndicesSousClasse(), itemCreationIndicesProcessor(), jpaCollectionPersistWriter))
						.next(calculIndicesStep())
					.build();
				// @formatter:on

	}

}
