package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Secteur;
import fr.insee.formation.hibernate.model.TypeVoie;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreationSecteurProcessor implements ItemProcessor<String[], Secteur> {

	@Value("${batch.dataScale}")
	private Integer dataScale;

	@Value("${batch.nbMoisHistorique}")
	private Integer nbMoisHistorique;

	@Value("${batch.affichageDeclarationsCreees}")
	private Integer compteurDeclarationsCrees;

	private Integer compteurDeclarations = 0;

	@Override
	public Secteur process(String[] item) throws Exception {

		Secteur secteur = new Secteur();

		secteur.setCodeNaf(item[0]);
		secteur.setLibelleNomenclature(item[1]);

		Random random = new Random();

		Integer nbEntreprises = random.nextInt(dataScale);

		Faker faker = new Faker(new Locale("fr"));

		Date dateDebutCreation = java.sql.Date.valueOf(LocalDate.of(1970, 01, 01));
		Date dateFinCreation = java.sql.Date.valueOf(LocalDate.of(2022, 01, 01));

		/*
		 * Création des entreprises
		 */
		Entreprise entreprise = null;
		Address fakerAddress = null;
		for (int i = 0; i < nbEntreprises; i++) {

			entreprise = new Entreprise();

			fakerAddress = faker.address();

			entreprise.getAdresse().setNomVoie(fakerAddress.streetName());
			entreprise.getAdresse().setNumero(fakerAddress.streetAddressNumber());

			String typeVoieString = null;
			List<String> listTypeVoie = Arrays.stream(TypeVoie.values()).map(t -> t.name().toUpperCase())
					.collect(Collectors.toList());
			while (typeVoieString == null || !listTypeVoie.contains(typeVoieString)) {
				typeVoieString = fakerAddress.streetPrefix().toUpperCase();
			}

			entreprise.getAdresse().setTypeVoie(TypeVoie.valueOf(typeVoieString));

			entreprise.getAdresse().setVille(fakerAddress.city());
			entreprise.getAdresse().setPays("France");

			entreprise.setDateCreation(faker.date().between(dateDebutCreation, dateFinCreation));

			entreprise.setSiren(faker.number().digits(9));

			entreprise.setDenomination(faker.company().name());

			entreprise.setUrl(faker.company().url());

			entreprise.setTelephone(faker.phoneNumber().phoneNumber());

			secteur.addEntreprise(entreprise);

			/*
			 * Création des déclarations
			 */
			Declaration declaration = null;
			Date dateDeclaration = null;
			Double montant = null;
			Integer montantMoyen = random.nextInt(100000);
			for (int nbMois = 0; nbMois < nbMoisHistorique; nbMois++) {

				dateDeclaration = java.sql.Date.valueOf(LocalDate.of(2022, 01, 01).minusMonths(nbMois));

				montant = Math.floor(montantMoyen * random.nextGaussian());

				declaration = new Declaration();

				declaration.setDate(dateDeclaration);
				declaration.setMontant(montant);

				entreprise.addDeclaration(declaration);

				if ((compteurDeclarations++) % compteurDeclarationsCrees == 0)
					log.info("Il y a {} déclarations qui ont été créées", compteurDeclarations);

			}

		}

		return secteur;
	}

}
