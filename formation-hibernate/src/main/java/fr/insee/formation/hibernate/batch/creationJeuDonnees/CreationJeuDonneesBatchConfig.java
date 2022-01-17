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

import fr.insee.formation.hibernate.model.Secteur;
import fr.insee.formation.hibernate.utils.CSVLineReader;
import fr.insee.formation.hibernate.utils.JPAPersistWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CreationJeuDonneesBatchConfig {

	@Value("${batch.data_scale}")
	private Integer dataScale;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	public ItemReader<String[]> itemReader() {
		return new CSVLineReader("code_naf.csv");
	}

	@Bean
	public ItemProcessor<String[], Secteur> itemProcessor() {
		return new CreationSecteurProcessor(dataScale);
	}

	@Bean
	public ItemWriter<Secteur> itemWriter() {
		return new JPAPersistWriter<Secteur>();
	}

	@Bean
	protected Step processLines(ItemReader<String[]> reader, ItemProcessor<String[], Secteur> processor,
			ItemWriter<Secteur> writer) {
		return steps.get("processLines").<String[], Secteur>chunk(100).reader(reader).processor(processor)
				.writer(writer).build();
	}

	@Bean
	public Job creationJeuDonneesJob() {
		return jobs.get("chunksJob").start(processLines(itemReader(), itemProcessor(), itemWriter())).build();
	}

}
