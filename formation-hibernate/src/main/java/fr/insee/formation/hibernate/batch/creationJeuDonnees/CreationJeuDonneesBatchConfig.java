package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import fr.insee.formation.hibernate.batch.listener.ChunkTimingListener;
import fr.insee.formation.hibernate.batch.utils.CSVLineReader;
import fr.insee.formation.hibernate.batch.utils.JPAPersistWriter;
import fr.insee.formation.hibernate.model.Secteur;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource(value = "classpath:batch.properties")
public class CreationJeuDonneesBatchConfig {

	@Value("${batch.nomFichierCreationJeuDonnees}")
	private String nomFichierCreationJeuDonnees;

	@Value("${batch.chunkSize}")
	private Integer chunkSize;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	JPAPersistWriter<Secteur> jpaPersistWriter;

	@Bean
	public ItemReader<String[]> itemReader() {
		return new CSVLineReader(nomFichierCreationJeuDonnees);
	}

	@Bean
	public ItemProcessor<String[], Secteur> itemProcessor() {
		return new CreationSecteurProcessor();
	}

	@Bean
	protected Step processLines(ItemReader<String[]> reader, ItemProcessor<String[], Secteur> processor,
			ItemWriter<Secteur> writer) {
		return
		//// @formatter:off
				steps
					.get("processLines")
					.<String[], Secteur>chunk(chunkSize)
					.reader(reader)
					.processor(processor)
					.writer(writer)
					.listener(new ChunkTimingListener(chunkSize))
				.build();
		// @formatter:on

	}

	@Bean
	public Job creationJeuDonneesJob() {
		return
		//// @formatter:off
			jobs
				.get("chunksJob")
				.start(processLines(itemReader(), itemProcessor(), jpaPersistWriter))
			.build();
		// @formatter:on

	}

}
