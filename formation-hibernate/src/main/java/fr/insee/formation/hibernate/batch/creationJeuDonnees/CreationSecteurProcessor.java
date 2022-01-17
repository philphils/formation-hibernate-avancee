package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import org.springframework.batch.item.ItemProcessor;

import fr.insee.formation.hibernate.model.Secteur;

public class CreationSecteurProcessor implements ItemProcessor<String[], Secteur> {

	@Override
	public Secteur process(String[] item) throws Exception {

		Secteur secteur = new Secteur();

		secteur.setCodeNaf(item[0]);
		secteur.setLibelleNomenclature(item[1]);

		return secteur;
	}

}
