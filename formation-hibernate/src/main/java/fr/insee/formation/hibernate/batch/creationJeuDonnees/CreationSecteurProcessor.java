package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import fr.insee.formation.hibernate.model.TypeVoie;
import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreationSecteurProcessor implements ItemProcessor<String[], AbstractNiveauNomenclature> {

	@Value("${batch.dataScale}")
	private Integer dataScale;

	@Value("${batch.nbMoisHistorique}")
	private Integer nbMoisHistorique;

	/*
	 * Variable pour mesurer la vitesse de création des déclarations
	 */
	@Value("${batch.affichageDeclarationsCreees}")
	private Integer compteurAffichageDeclarationsCrees;

	private Integer compteurDeclarationsCreees = 1;

	LocalTime localTimeDebutJob = null;
	LocalTime localTimeDebutBoucle = null;
	LocalTime localTimeFinBoucle = null;

	@Override
	public AbstractNiveauNomenclature process(String[] item) throws Exception {

		if (localTimeDebutJob == null)
			localTimeDebutJob = LocalTime.now(ZoneId.of("Europe/Paris"));

		AbstractNiveauNomenclature secteur = null; // TODO gérer la création des niveau aux différents échelons

		secteur.setCodeNaf(item[0]);
		secteur.setLibelleNomenclature(item[1]);

		Random random = new Random();

		secteur.setCoeffRedressementNiveau(random.nextDouble());

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

			creerEntreprise(random, faker, dateDebutCreation, dateFinCreation);

		}

		return secteur;
	}

	private void creerEntreprise(Random random, Faker faker, Date dateDebutCreation, Date dateFinCreation) {
		Entreprise entreprise;
		Address fakerAddress;
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

		entreprise.setPrenomFondateur(faker.name().firstName());

		entreprise.setNomFondateur(faker.name().lastName());

		entreprise.setCoeffRedressementEntreprise(random.nextDouble());

		// TODO gérer l'ajout entreprise pour le niveau sous-classe
		// secteur.addEntreprise(entreprise);

		/*
		 * Création des déclarations
		 */
		Declaration declaration = null;
		Date dateDeclaration = null;
		Double montant = null;
		Integer montantMoyen = random.nextInt(100000);

		for (int nbMois = 0; nbMois < nbMoisHistorique; nbMois++) {

			creerDeclaration(random, entreprise, montantMoyen, nbMois);

			comptageDeclarationsCreees();

			compteurDeclarationsCreees++;

		}
	}

	private void creerDeclaration(Random random, Entreprise entreprise, Integer montantMoyen, int nbMois) {
		Declaration declaration;
		Date dateDeclaration;
		Double montant;
		dateDeclaration = java.sql.Date.valueOf(LocalDate.of(2022, 01, 01).minusMonths(nbMois));

		montant = Math.floor(montantMoyen * random.nextGaussian());

		declaration = new Declaration();

		declaration.setDate(dateDeclaration);
		declaration.setMontant(montant);

		entreprise.addDeclaration(declaration);
	}

	private void comptageDeclarationsCreees() {

		if (compteurDeclarationsCreees % compteurAffichageDeclarationsCrees == 0) {

			if (localTimeFinBoucle == null)
				/* Premier passage -> on prend la date de début du job */
				localTimeDebutBoucle = localTimeDebutJob;
			else
				/* Sinon on récupère la date de fin de la dernière boucle */
				localTimeDebutBoucle = localTimeFinBoucle;

			localTimeFinBoucle = LocalTime.now(ZoneId.of("Europe/Paris"));

			Long secondesPourCetteBoucle = ChronoUnit.SECONDS.between(localTimeDebutBoucle, localTimeFinBoucle);

			if (secondesPourCetteBoucle != 0)
				log.debug("{} déclarations par seconde pour les {} dernières",
						compteurAffichageDeclarationsCrees / secondesPourCetteBoucle,
						compteurAffichageDeclarationsCrees);

			Long secondesDepuisDebutJob = ChronoUnit.SECONDS.between(localTimeDebutJob, localTimeFinBoucle);

			if (secondesDepuisDebutJob != 0)
				log.debug("{} déclarations par seconde en moyenne depuis le début et {} déclarations créées au total.",
						compteurDeclarationsCreees / secondesDepuisDebutJob, compteurDeclarationsCreees);

		}
	}

}
