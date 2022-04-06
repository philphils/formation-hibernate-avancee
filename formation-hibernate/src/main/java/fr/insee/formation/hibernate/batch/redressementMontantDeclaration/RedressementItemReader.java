package fr.insee.formation.hibernate.batch.redressementMontantDeclaration;

import java.util.Iterator;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.repositories.DeclarationRepository;

public class RedressementItemReader implements ItemReader<Declaration>, ItemStream {

	@Autowired
	DeclarationRepository declarationRepository;

	private Iterator<Declaration> iterator;

	@Override
	public Declaration read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator.hasNext())
			return iterator.next();
		else
			return null;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		iterator = declarationRepository.findAllDeclarationWithEntrepriseWithSecteur().iterator();
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
