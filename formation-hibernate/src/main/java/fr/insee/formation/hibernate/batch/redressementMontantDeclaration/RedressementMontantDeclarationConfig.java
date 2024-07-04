package fr.insee.formation.hibernate.batch.redressementMontantDeclaration;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import fr.insee.formation.hibernate.batch.listener.TimingItemProcessListener;
import fr.insee.formation.hibernate.batch.utils.ChunkingStreamTasklet;
import fr.insee.formation.hibernate.batch.utils.JPAHibernateSpecificCursorItemReader;
import fr.insee.formation.hibernate.batch.utils.JPAUpdateWriter;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedressementMontantDeclarationConfig {

	@Value("${batch.chunkSizeRedressement}")
	private Integer chunkSize;

	@Value("${batch.affichageDeclarationRedressees}")
	private Integer affichageLogCompteur;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	DeclarationRepository declarationRepository;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@Bean
	public ItemProcessor<Declaration, Declaration> redressementItemProcessor() {
		return new RedressementProcessor();
	}

	@Bean
	public ItemWriter<Declaration> redressementItemWriter() {
		return new JPAUpdateWriter<Declaration>();
	}

	//// @formatter:off
	/**
	 * ---------------PARTIE CHUNK SANS CURSOR---------------------------- 
	 * Ici est défini le batch de redressement en mode Chunk "classique", avec donc un reader qui
	 * s'appuie sur une requête renvoyant une liste de {@link Declaration}
	 */
	// @formatter:on

	@Bean
	public ItemReader<Declaration> redressementItemReader() {
		return new RedressementItemReader();
	}

	@Bean
	protected Step redressementProcessLines(ItemReader<Declaration> reader,
			ItemProcessor<Declaration, Declaration> processor, ItemWriter<Declaration> writer) {

		return
		//// @formatter:off
				new StepBuilder("redressementProcessLines", jobRepository)
					.<Declaration, Declaration>chunk(chunkSize, platformTransactionManager)
					.reader(reader)
					.processor(processor)
					.listener(new TimingItemProcessListener(affichageLogCompteur))
					.writer(writer)
				.build();
		// @formatter:on

	}

	@Bean
	public Job redressementMontantDeclarationJob() {
		return
		//// @formatter:off
			new JobBuilder("redressementMontantDeclarationJob", jobRepository)
				.start(redressementProcessLines(redressementItemReader(), redressementItemProcessor(), redressementItemWriter()))
			.build();
		// @formatter:on

	}

	/**
	 * ---------------FIN PARTIE CHUNK SANS CURSOR----------------------------
	 */

	//// @formatter:off
	/**
	 * ---------------PARTIE CHUNK AVEC CURSOR---------------------------- 
	 * Ici est défini le batch de redressement en mode Chunk "classique", avec donc un reader qui
	 * s'appuie sur une requête renvoyant une liste de {@link Declaration}
	 */
	// @formatter:on

	@Bean
	public ItemReader<Declaration> redressementItemReaderWithCursor() {

		String requete = "SELECT declaration FROM Declaration declaration JOIN FETCH declaration.entreprise entreprise JOIN FETCH entreprise.sousClasse sousClasse";

//		//// @formatter:off
//		ItemReader<Declaration> itemReader = new HibernateCursorItemReaderBuilder()
//				.fetchSize(200)
//				.queryString(requete)
//				.sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
//				.name("requete_decl_cursor")
//				.build();
//		// @formatter:on

		JPAHibernateSpecificCursorItemReader<Declaration> itemReader = new JPAHibernateSpecificCursorItemReader<>(
				requete);

		return itemReader;

	}

	@Bean
	public Job redressementMontantDeclarationCursorJob() {
		return
		//// @formatter:off
			new JobBuilder("redressementMontantDeclarationCursorJob", jobRepository)
				.start(redressementProcessLines(redressementItemReaderWithCursor(), redressementItemProcessor(), redressementItemWriter()))
			.build();
		// @formatter:on

	}

	/**
	 * ---------------PARTIE CHUNK AVEC CURSOR----------------------------
	 */

	//// @formatter:off
	/**
	 * ---------------PARTIE TASKLET STREAM--------------------------------------
	 * Ici on utilise le composant {@link ChunkingStreamTasklet} qui permet de
	 * reproduire une partie des fonctionnalités du mode Chunk mais qui rend en
	 * entrée une fonction renvoyant un {@link Stream} ( un {@link Supplier} de {@link Stream})
	 */
	// @formatter:on

	@Bean
	public Tasklet redressementTasklet() {

		ChunkingStreamTasklet tasklet = new ChunkingStreamTasklet<Declaration, Declaration>(
				declarationRepository::streamAllDeclarationWithEntrepriseWithSousClasse, redressementItemProcessor(),
				redressementItemWriter(), chunkSize, true);

		tasklet.addItemProcessListener(new TimingItemProcessListener(affichageLogCompteur));

		return tasklet;
	}

	@Bean
	protected Step redressementStreamProcessLines() {
		return new StepBuilder("processLines", jobRepository).tasklet(redressementTasklet(), platformTransactionManager)
				.build();
	}

	@Bean
	public Job redressementMontantDeclarationStreamJob() {
		return
		//// @formatter:off
			new JobBuilder("redressementMontantDeclarationStreamJob", jobRepository)
				.start(redressementStreamProcessLines())
			.build();
		// @formatter:on
	}

	/**
	 * ---------------FIN PARTIE TASKLET STREAM-----------------------------------
	 */
}
