package fr.insee.formation.hibernate.utils;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVLineReader implements ItemReader<String[]>, StepExecutionListener {

	private FileUtils fu;

	private String filePath;

	public CSVLineReader(String filePath) {
		super();
		this.filePath = filePath;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		fu = new FileUtils(this.filePath);
		log.debug("Line Reader initialized.");
	}

	@Override
	public String[] read() throws Exception {
		String[] line = fu.readLine();
		log.debug("Read line: {}", line);
		return line;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		fu.closeReader();
		log.debug("Line Reader ended.");
		return ExitStatus.COMPLETED;
	}
}