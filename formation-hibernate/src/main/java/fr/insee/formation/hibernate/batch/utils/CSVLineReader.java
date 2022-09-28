package fr.insee.formation.hibernate.batch.utils;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVLineReader implements ItemReader<String[]>, StepExecutionListener {

	private FileUtils fu;

	private String filePath;

	private Integer limit;

	private Integer compteur;

	public CSVLineReader(String filePath) {
		super();
		this.filePath = filePath;
	}

	public CSVLineReader(String filePath, Integer limit) {
		super();
		this.filePath = filePath;
		this.limit = limit;
		this.compteur = 1;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		fu = new FileUtils(this.filePath);
		log.debug("Line Reader initialized.");
	}

	@Override
	public String[] read() throws Exception {

		if (compteur != null && compteur >= limit)
			return null;

		String[] line = fu.readLine();
		log.trace("Read line: {}", line);

		if (compteur != null)
			compteur = compteur + 1;

		return line;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		fu.closeReader();
		log.debug("Line Reader ended.");
		return ExitStatus.COMPLETED;
	}
}