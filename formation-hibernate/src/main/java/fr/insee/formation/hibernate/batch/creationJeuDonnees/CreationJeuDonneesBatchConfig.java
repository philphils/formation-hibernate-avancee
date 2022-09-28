package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.util.Set;

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

import fr.insee.formation.hibernate.batch.listener.TimingItemProcessListener;
import fr.insee.formation.hibernate.batch.utils.CSVLineReader;
import fr.insee.formation.hibernate.batch.utils.JPACollectionPersistWriter;
import fr.insee.formation.hibernate.batch.utils.JPAPersistWriter;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Indice;
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

	@Value("${batch.nombreSecteur}")
	private Integer nombreSecteur;

	@Value("${batch.chunkSize}")
	private Integer chunkSize;

	@Value("${batch.chunkSizeCreationIndices}")
	private Integer chunkSizeCreationIndices;

	@Value("${batch.affichageSecteurCrees}")
	private Integer compteurAffichageSecteurCrees;

	@Value("${batch.affichageDeclarationsCrees}")
	private Integer compteurAffichageDeclarationsCrees;

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
	public ItemReader<String[]> creationSecteurItemReader() {
		if (nombreSecteur != null)
			return new CSVLineReader(nomFichierCreationJeuDonnees, nombreSecteur);
		else
			return new CSVLineReader(nomFichierCreationJeuDonnees);
	}

	@Bean
	public ItemReader<SousClasse> creationEntreprisesItemReader() {
		return new CreationEntreprisesItemReader();
	}

	@Bean
	public ItemReader<SousClasse> creationIndicesItemReader() {
		return new CreationIndicesItemReader();
	}

	@Bean
	public ItemProcessor<SousClasse, Set<Indice>> creationIndicesItemProcessor() {
		return new CreationIndicesProcessor();
	}

	@Bean
	public ItemProcessor<String[], AbstractNiveauNomenclature> creationSecteurItemProcessor() {
		return new CreationSecteurProcessor();
	}

	@Bean
	public ItemProcessor<SousClasse, Set<Entreprise>> creationEntrepriseItemProcessor() {
		return new CreationEntrepriseDeclarationProcessor();
	}

	@Bean
	protected Step creationNomenclatureStep(ItemReader<String[]> reader,
			ItemProcessor<String[], AbstractNiveauNomenclature> processor,
			ItemWriter<AbstractNiveauNomenclature> writer) {

		TimingItemProcessListener itemProcessListener = new TimingItemProcessListener();

		itemProcessListener.setAffichageLogCompteur(compteurAffichageSecteurCrees);

		return
		//// @formatter:off
				steps
					.get("creationNomenclatureStep")
					.<String[], AbstractNiveauNomenclature>chunk(chunkSize)
					.reader(reader)
					.processor(processor)
					.listener(itemProcessListener)
					.writer(writer)
				.build();
		// @formatter:on

	}

	@Bean
	protected Step creationEntrepriseAndDeclarationStep(ItemReader<SousClasse> reader,
			ItemProcessor<SousClasse, Set<Entreprise>> processor, ItemWriter<Set<Entreprise>> writer) {

		TimingItemProcessListener itemProcessListener = new TimingItemProcessListener();

		itemProcessListener.setAffichageLogCompteur(compteurAffichageDeclarationsCrees);

		return
		//// @formatter:off
				steps
					.get("creationEntrepriseAndDeclarationStep")
					.<SousClasse, Set<Entreprise>>chunk(chunkSize)
					.reader(reader)
					.processor(processor)
					.listener(itemProcessListener)
					.writer(writer)
				.build();
		// @formatter:on

	}

	@Bean
	public Step creationIndicesStep(ItemReader<SousClasse> reader, ItemProcessor<SousClasse, Set<Indice>> processor,
			ItemWriter<Set<Indice>> writer) {

		TimingItemProcessListener itemProcessListener = new TimingItemProcessListener();

		itemProcessListener.setAffichageLogCompteur(compteurAffichageSecteurCrees);

		return
		//// @formatter:off
				steps
					.get("creationIndicesStep")
					.<SousClasse, Set<Indice>>chunk(chunkSizeCreationIndices)
					.reader(reader)
					.processor(processor)
					.listener(itemProcessListener)
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
				.start(creationNomenclatureStep(creationSecteurItemReader(), creationSecteurItemProcessor(), jpaPersistWriter))
				.next(creationEntrepriseAndDeclarationStep(creationEntreprisesItemReader(), creationEntrepriseItemProcessor(), jpaCollectionPersistWriter))
				.next(creationIndicesStep(creationIndicesItemReader(), creationIndicesItemProcessor(), jpaCollectionPersistWriter))
			.build();
		// @formatter:on

	}

}
