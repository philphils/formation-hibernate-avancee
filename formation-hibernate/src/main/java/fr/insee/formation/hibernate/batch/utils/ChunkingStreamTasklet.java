package fr.insee.formation.hibernate.batch.utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe permettant de réaliser un {@link Chunk} avec un {@link Stream} pour
 * {@link ItemReader}. Cette classe reproduit donc la logique des {@link Chunk}
 * de Spring-Batch. Il est possible d'exécuter tout le {@link Tasklet} au sein
 * de la même transaction (peut-être utile en cas de mise à jour d'Entity gérées
 * par Hibernate pour éviter les requêtes superflues lors du réattachement de
 * l'objet au {@link PersistenceContext}), ou bien une transaction par "chunk".
 * Paramètrable avec {@link ChunkingStreamTasklet#newTransactionForEachChunk}
 * 
 * @author SYV27O
 *
 * @param <S>
 * @param <T>
 */

@Slf4j
public class ChunkingStreamTasklet<S, T> implements Tasklet, StepExecutionListener {

	private Supplier<Stream<S>> streamSupplier;

	private ItemProcessor<S, T> itemProcessor;

	private ItemWriter<T> itemWriter;

	private Integer chunkSize;

	private Boolean newTransactionForEachChunk;

	private Integer compteur = 1;

	private Integer affichageLogCompteur;

	private Instant timer;

	private Long totalMilliseconds = 0L;

	private List<S> entryList = new ArrayList<S>();

	private List<T> resultList = new ArrayList<T>();

	@Autowired
	EntityManager entityManager;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private TransactionTemplate transactionTemplate;

	public ChunkingStreamTasklet(Supplier<Stream<S>> streamSupplier, ItemProcessor<S, T> itemProcessor,
			ItemWriter<T> itemWriter, Integer chunkSize, Boolean newTransactionForEachChunk) {
		super();
		this.streamSupplier = streamSupplier;
		this.itemProcessor = itemProcessor;
		this.itemWriter = itemWriter;
		this.chunkSize = chunkSize;
		this.newTransactionForEachChunk = newTransactionForEachChunk;
	}

	public void setAffichageLogCompteur(Integer affichageLogCompteur) {
		this.affichageLogCompteur = affichageLogCompteur;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		timer = Instant.now();

		streamSupplier.get().forEach(entry -> {
			try {
				entryList.add(entry);
				/*
				 * On ajoute les entrées à la liste et on les trait lorsque le nombre à atteint
				 * la taille définie des chunks
				 */
				if (compteur % chunkSize == 0) {
					processAndWrite(entryList);
					entryList.removeAll(entryList);
				}
				compteur++;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		/*
		 * On traite les elements restants dont le nombre est inférieur à la taille des
		 * chunks
		 */
		if (!entryList.isEmpty()) {
			processAndWrite(entryList);
			entryList.removeAll(entryList);
		}

		return RepeatStatus.FINISHED;
	}

	private void processAndWrite(List<S> entryList) throws Exception {

		/*
		 * On ouvre une transaction pour le traitement de chaque chunk
		 */
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					log.debug("Writing {} objects", chunkSize);
					resultList = entryList.stream().map(e -> {
						try {
							return itemProcessor.process(e);
						} catch (Exception e1) {
							throw new RuntimeException(e1);
						}
					}).collect(Collectors.toList());
					itemWriter.write(resultList);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

		if (newTransactionForEachChunk)
			entityManager.clear();

		if (affichageLogCompteur != null && compteur % affichageLogCompteur == 0) {
			Long milliSeconds = Instant.now().toEpochMilli() - timer.toEpochMilli();
			totalMilliseconds = milliSeconds + totalMilliseconds;
			log.info(milliSeconds + " milli-secondes pour persister " + affichageLogCompteur + " objets. Moyenne : "
					+ Math.floor(((double) compteur / (double) totalMilliseconds) * 1000) + " objet traité / second");
			timer = Instant.now();
		}

	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		transactionTemplate = new TransactionTemplate(transactionManager);
		if (newTransactionForEachChunk)
			transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

}
