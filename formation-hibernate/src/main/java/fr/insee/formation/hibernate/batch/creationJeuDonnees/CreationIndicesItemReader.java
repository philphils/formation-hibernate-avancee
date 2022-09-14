package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.util.Iterator;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreationIndicesItemReader implements ItemReader<SousClasse>, ItemStream {

	@Autowired
	SousClasseRepository sousClasseRepository;

	private Boolean premierPassage = true;

	private Iterator<SousClasse> iterator;

	@Override
	public SousClasse read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		if (premierPassage) {
			log.info("La requête a été exécutée, le traitement des items commence");
			premierPassage = false;
		}

		if (iterator.hasNext())
			return iterator.next();
		else
			return null;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		iterator = sousClasseRepository.findAllWithIndicesMensuelsAndAnnuels().iterator();
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws ItemStreamException {
		// TODO Auto-generated method stub

	}

}
