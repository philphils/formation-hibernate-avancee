package fr.insee.formation.hibernate.batch.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

	private List<T> resultList = new ArrayList<T>();

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

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		streamSupplier.get().forEach(entry -> {
			try {
				processAndWrite(entry);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		return RepeatStatus.FINISHED;
	}

	private void processAndWrite(S entry) throws Exception {

		T result = itemProcessor.process(entry);

		write(result);

	}

	private void write(T result) throws Exception {
		if (compteur % chunkSize != 0) {
			resultList.add(result);
		} else {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						log.debug("Writing {} objects", chunkSize);
						itemWriter.write(resultList);
						resultList.removeAll(resultList);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});

		}
		compteur++;
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
