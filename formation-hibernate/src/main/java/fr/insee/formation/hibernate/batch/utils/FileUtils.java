package fr.insee.formation.hibernate.batch.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	private String fileName;
	private CSVReader CSVReader;
	private CSVWriter CSVWriter;
	private FileReader fileReader;
	private FileWriter fileWriter;
	private File file;

	public FileUtils(String fileName) {
		this.fileName = fileName;
	}

	public String[] readLine() {
		try {
			if (CSVReader == null)
				initReader();
			String[] line = CSVReader.readNext();
			if (line == null)
				return null;
			return line;
		} catch (Exception e) {
			log.error("Error while reading line in file: " + this.fileName);
			return null;
		}
	}

	public void writeLine(String[] line) {
		try {
			if (CSVWriter == null)
				initWriter();
			CSVWriter.writeNext(line);
		} catch (Exception e) {
			log.error("Error while writing line in file: " + this.fileName);
		}
	}

	private void initReader() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		if (file == null)
			file = new File(classLoader.getResource(fileName).getFile());
		if (fileReader == null)
			fileReader = new FileReader(file);
		if (CSVReader == null)
			CSVReader = new CSVReaderBuilder(fileReader)
					.withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();
	}

	private void initWriter() throws Exception {
		if (file == null) {
			file = new File(fileName);
			file.createNewFile();
		}
		if (fileWriter == null)
			fileWriter = new FileWriter(file, true);
		if (CSVWriter == null)
			CSVWriter = new CSVWriter(fileWriter);
	}

	public void closeWriter() {
		try {
			CSVWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			log.error("Error while closing writer.");
		}
	}

	public void closeReader() {
		try {
			CSVReader.close();
			fileReader.close();
		} catch (IOException e) {
			log.error("Error while closing reader.");
		}
	}

}
