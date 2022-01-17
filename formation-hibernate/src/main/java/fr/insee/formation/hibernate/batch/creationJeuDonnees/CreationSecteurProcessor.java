package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.springframework.batch.item.ItemProcessor;

import com.github.javafaker.Faker;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Secteur;

public class CreationSecteurProcessor implements ItemProcessor<String[], Secteur> {

	private Integer dataScale;

	public CreationSecteurProcessor(Integer dataScale) {
		super();
		this.dataScale = dataScale;
	}

	@Override
	public Secteur process(String[] item) throws Exception {

		Secteur secteur = new Secteur();

		secteur.setCodeNaf(item[0]);
		secteur.setLibelleNomenclature(item[1]);

		Random random = new Random();

		Integer nbEntreprises = random.nextInt(dataScale);

		Faker faker = new Faker(new Locale("fr"));

		for (int i = 0; i < nbEntreprises; i++) {

			Entreprise entreprise = new Entreprise();

			entreprise.getAdresse().setNomVoie(faker.address().streetName());
			entreprise.getAdresse().setNumero(faker.address().streetAddressNumber());
//			entreprise.getAdresse().setTypeVoie(faker.address().streetPrefix());
			entreprise.getAdresse().setVille(faker.address().city());
			entreprise.getAdresse().setPays(faker.address().country());

			entreprise.setDateCreation(faker.date().between(new Date(1970, 1, 1), new Date(2022, 1, 1)));

			entreprise.setSiren(faker.number().digits(8));

			secteur.addEntreprise(entreprise);
		}

		return secteur;
	}

}
