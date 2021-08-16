package fr.insee.formation.hibernate.dao;

import java.util.Set;

import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;

public interface IndiceDAO {
	
	public IndiceAnnuel findIndiceAnnuel(int i);

	public IndiceMensuel findIndiceMensuel(int i);

	public Set<Indice> findAll();

}
