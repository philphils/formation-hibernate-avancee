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
import fr.insee.formation.hibernate.model.nomenclature.Classe;
import fr.insee.formation.hibernate.model.nomenclature.Division;
import fr.insee.formation.hibernate.model.nomenclature.Groupe;
import fr.insee.formation.hibernate.model.nomenclature.Section;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
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

	/*
	 * On n'utilise qu'un objet Random pour des questions de performances
	 */
	private Random random = new Random();
	private Faker faker = new Faker(new Locale("fr"));

	private AbstractNiveauNomenclature precedentNiveauNomenclature = null;

	LocalTime localTimeDebutJob = null;
	LocalTime localTimeDebutBoucle = null;
	LocalTime localTimeFinBoucle = null;

	@Override
	public AbstractNiveauNomenclature process(String[] item) throws Exception {

		if (localTimeDebutJob == null)
			localTimeDebutJob = LocalTime.now(ZoneId.of("Europe/Paris"));

		String codeNaf = item[0];
		String libelleNomenclature = item[1];

		AbstractNiveauNomenclature niveauNomenclature = creerNiveauNomenclature(codeNaf, libelleNomenclature);

		/*
		 * Si c'est un niveau SousClasse on créé les Entreprises
		 */
		if (niveauNomenclature instanceof SousClasse) {

			Integer nbEntreprises = random.nextInt(dataScale);

			Date dateDebutCreation = java.sql.Date.valueOf(LocalDate.of(1970, 01, 01));
			Date dateFinCreation = java.sql.Date.valueOf(LocalDate.of(2022, 01, 01));
			/*
			 * Création des entreprises
			 */
			Entreprise entreprise = null;
			Address fakerAddress = null;
			for (int i = 0; i < nbEntreprises; i++) {

				((SousClasse) niveauNomenclature).addEntreprise(creerEntreprise(dateDebutCreation, dateFinCreation));

			}

		}

		precedentNiveauNomenclature = niveauNomenclature;

		return niveauNomenclature;
	}

	private AbstractNiveauNomenclature creerNiveauNomenclature(String codeNaf, String libelleNomenclature) {

		AbstractNiveauNomenclature niveauNomenclature = null;

		/*
		 * si precedentNiveauNomenclature est null, on est sur la premiere ligne du
		 * fichier
		 */
		if (precedentNiveauNomenclature == null) {
			/*
			 * le premier Niveau de nomenclature doit être de type "section"
			 */
			if (!codeNaf.contains("SECTION"))
				throw new RuntimeException("Le premier niveau doit être de type SECTION");
			else {
				niveauNomenclature = new Section();
			}
		} else {
			if (codeNaf.contains("SECTION"))
				niveauNomenclature = new Section();
			else if (codeNaf.length() <= 2) {
				niveauNomenclature = new Division();
				/*
				 * On calcule la Section à laquelle rattacher cette Division
				 */
				Section section = null;
				if (precedentNiveauNomenclature instanceof Section)
					section = ((Section) precedentNiveauNomenclature);
				else if (precedentNiveauNomenclature instanceof SousClasse)
					section = ((SousClasse) precedentNiveauNomenclature).getClasse().getGroupe().getDivision()
							.getSection();
				else
					throwHierarchieNomenclatureException(codeNaf);
				/*
				 * Et on rattache la nouvelle division à cette Section
				 */
				section.addDivision((Division) niveauNomenclature);

			} else if (codeNaf.length() <= 4) {
				niveauNomenclature = new Groupe();
				/*
				 * On calcule la Division à laquelle rattacher ce Groupe
				 */
				Division division = null;
				if (precedentNiveauNomenclature instanceof Division)
					division = (Division) precedentNiveauNomenclature;
				else if (precedentNiveauNomenclature instanceof SousClasse)
					division = ((SousClasse) precedentNiveauNomenclature).getClasse().getGroupe().getDivision();
				else
					throwHierarchieNomenclatureException(codeNaf);

				/*
				 * Et on rattache ce nouveau Groupe à la Division
				 */
				division.addGroupe((Groupe) niveauNomenclature);

			} else if (codeNaf.length() <= 5) {
				niveauNomenclature = new Classe();
				/*
				 * On calcule le Groupe auquel rattacher cette Classe
				 */
				Groupe groupe = null;
				if (precedentNiveauNomenclature instanceof Groupe)
					groupe = (Groupe) precedentNiveauNomenclature;
				else if (precedentNiveauNomenclature instanceof SousClasse)
					groupe = ((SousClasse) precedentNiveauNomenclature).getClasse().getGroupe();
				else
					throwHierarchieNomenclatureException(codeNaf);

				/*
				 * Et on rattache cette nouvelle Classe à ce Groupe
				 */
				groupe.addClasse((Classe) niveauNomenclature);

			} else if (codeNaf.length() <= 6) {
				niveauNomenclature = new SousClasse();
				/*
				 * On calcule la Classe à laquelle rattacher cette SousClasse
				 */
				Classe classe = null;
				if (precedentNiveauNomenclature instanceof Classe)
					classe = (Classe) precedentNiveauNomenclature;
				else if (precedentNiveauNomenclature instanceof SousClasse)
					classe = ((SousClasse) precedentNiveauNomenclature).getClasse();
				else
					throwHierarchieNomenclatureException(codeNaf);

				/*
				 * Et on rattache cette nouvelle SousClasse à cette Classe
				 */
				classe.addSousClasse((SousClasse) niveauNomenclature);
			}
		}

		niveauNomenclature.setCodeNaf(codeNaf);
		niveauNomenclature.setLibelleNomenclature(libelleNomenclature);

		niveauNomenclature.setCoeffRedressementNiveau(random.nextDouble());

		niveauNomenclature.setCoeffCalculIndice(random.nextDouble());

		return niveauNomenclature;

	}

	private void throwHierarchieNomenclatureException(String codeNaf) {
		throw new RuntimeException("Le fichier ne respecte pas la hiérarchie des nomenclatures pour le niveau codeNaf :"
				+ codeNaf + ". Le niveau précédant est de codeNaf : " + precedentNiveauNomenclature.getCodeNaf());
	}

	private Entreprise creerEntreprise(Date dateDebutCreation, Date dateFinCreation) {

		Entreprise entreprise = new Entreprise();

		Address fakerAddress = faker.address();

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
		Integer montantMoyen = random.nextInt(100000);

		for (int nbMois = 0; nbMois < nbMoisHistorique; nbMois++) {

			entreprise.addDeclaration(creerDeclaration(montantMoyen, nbMois));

			comptageDeclarationsCreees();

			compteurDeclarationsCreees++;

		}

		return entreprise;

	}

	private Declaration creerDeclaration(Integer montantMoyen, int nbMois) {
		Declaration declaration;
		Date dateDeclaration;
		Double montant;
		dateDeclaration = java.sql.Date.valueOf(LocalDate.of(2022, 01, 01).minusMonths(nbMois));

		montant = Math.floor(montantMoyen * random.nextGaussian());

		declaration = new Declaration();

		declaration.setDate(dateDeclaration);
		declaration.setMontant(montant);

		return declaration;
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
