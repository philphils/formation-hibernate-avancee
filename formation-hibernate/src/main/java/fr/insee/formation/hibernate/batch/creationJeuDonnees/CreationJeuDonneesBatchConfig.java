package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Sort.Direction;

import fr.insee.formation.hibernate.batch.listener.ChunkTimingListener;
import fr.insee.formation.hibernate.batch.utils.CSVLineReader;
import fr.insee.formation.hibernate.batch.utils.JPACollectionPersistWriter;
import fr.insee.formation.hibernate.batch.utils.JPAPersistWriter;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource(value = "classpath:batch.properties")
public class CreationJeuDonneesBatchConfig {

	@Value("${batch.nomFichierCreationJeuDonnees}")
	private String nomFichierCreationJeuDonnees;

	@Value("${batch.chunkSize}")
	private Integer chunkSize;

	@Value("${batch.chunkSizeCreationIndices}")
	private Integer chunkSizeCreation;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	SousClasseRepository sousClasseRepository;

	@Autowired
	JPAPersistWriter jpaPersistWriter;

	@Autowired
	JPACollectionPersistWriter jpaCollectionPersistWriter;

	@Bean
	public ItemReader<String[]> itemReader() {
		return new CSVLineReader(nomFichierCreationJeuDonnees);
	}

	@Bean
	public ItemReader<SousClasse> itemReaderSousClasse() {
		RepositoryItemReader<SousClasse> itemReaderSousClasse = new RepositoryItemReader<SousClasse>();

		itemReaderSousClasse.setRepository(sousClasseRepository);
		itemReaderSousClasse.setMethodName("findAllWithEntreprises");
		itemReaderSousClasse.setSort(Map.of("codeNaf", Direction.ASC));

		return itemReaderSousClasse;
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
	public ItemProcessor<SousClasse, SousClasse> itemCreationIndicesProcessor() {
		return new CreationIndicesProcessor();
	}

	@Bean
	public ItemProcessor<String[], AbstractNiveauNomenclature> itemProcessor() {
		return new CreationSecteurProcessor();
	}

	@Bean
	public ItemProcessor<SousClasse, Set<Entreprise>> itemProcessorEntreprise() {
		return new CreationEntrepriseDeclarationProcessor();
	}

	@Bean
	protected Step creationNomenclatureStep(ItemReader<String[]> reader,
			ItemProcessor<String[], AbstractNiveauNomenclature> processor,
			ItemWriter<AbstractNiveauNomenclature> writer) {
		return
		//// @formatter:off
				steps
					.get("creationNomenclatureStep")
					.<String[], AbstractNiveauNomenclature>chunk(chunkSize)
					.reader(reader)
					.processor(processor)
					.writer(writer)
				.build();
		// @formatter:on

	}

	@Bean
	protected Step creationEntrepriseAndDeclarationStep(ItemReader<SousClasse> reader,
			ItemProcessor<SousClasse, Set<Entreprise>> processor, ItemWriter<Set<Entreprise>> writer) {
		return
		//// @formatter:off
				steps
					.get("creationEntrepriseAndDeclarationStep")
					.<SousClasse, Set<Entreprise>>chunk(chunkSize)
					.reader(reader)
					.processor(processor)
					.writer(writer)
					.listener(new ChunkTimingListener(chunkSize))
				.build();
		// @formatter:on

	}

	@Bean
	public Step creationIndicesStep(ItemReader<SousClasse> reader, ItemProcessor<SousClasse, SousClasse> processor,
			ItemWriter<SousClasse> writer) {
		return
		//// @formatter:off
				steps
					.get("creationIndicesStep")
					.<SousClasse, SousClasse>chunk(chunkSizeCreation)
					.reader(reader)
					.processor(processor)
					.writer(writer)
				.build();
		// @formatter:on

	}

	@Bean
	public Job creationJeuDonneesJob() {
		return
		//// @formatter:off
			jobs
				.get("chunksJob")
				.start(creationNomenclatureStep(itemReader(), itemProcessor(), jpaPersistWriter))
				.next(creationEntrepriseAndDeclarationStep(itemReaderSousClasse(), itemProcessorEntreprise(), jpaCollectionPersistWriter))
				.next(creationIndicesStep(itemReaderCreationIndicesSousClasse(), itemCreationIndicesProcessor(), jpaPersistWriter))
			.build();
		// @formatter:on

	}

}
