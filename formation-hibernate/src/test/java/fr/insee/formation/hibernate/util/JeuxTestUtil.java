package fr.insee.formation.hibernate.util;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import fr.insee.formation.hibernate.model.Adresse;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.TypeVoie;
import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import fr.insee.formation.hibernate.model.nomenclature.Classe;
import fr.insee.formation.hibernate.model.nomenclature.Division;
import fr.insee.formation.hibernate.model.nomenclature.Groupe;
import fr.insee.formation.hibernate.model.nomenclature.Section;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

@Component
public class JeuxTestUtil {

	@PersistenceContext
	private EntityManager entityManager;

	private Faker faker = new Faker(new Locale("fr"));

	@Transactional
	public void insererJeuxVolumeReduit() {

		for (Entreprise entreprise : jeuxEntreprisesReduit()) {
			entityManager.persist(entreprise);
		}

	}

	@Transactional
	public IndiceAnnuel creerIndiceAnnuel() {
		IndiceAnnuel indiceAnnuel = new IndiceAnnuel();

		indiceAnnuel.setValeur(10d);

		indiceAnnuel.setYear(Year.of(2017));

		entityManager.persist(indiceAnnuel);

		return indiceAnnuel;
	}

	@Transactional
	public IndiceMensuel creerIndiceMensuel() {

		IndiceMensuel indiceMensuel = new IndiceMensuel();

		indiceMensuel.setMonth(YearMonth.of(2017, Month.DECEMBER));

		indiceMensuel.setValeur(10.4d);

		indiceMensuel.setDerniereMaj(Instant.now());

		entityManager.persist(indiceMensuel);

		return indiceMensuel;

	}

	@Transactional
	public void creerJeuxMappingAssociation() {

		AbstractNiveauNomenclature secteur = jeuxMappingAssociation();

		entityManager.persist(secteur);
		entityManager.flush();

	}

	@Transactional
	public void creerJeux3Secteurs() {

		AbstractNiveauNomenclature secteur1 = jeuxMappingAssociation();

		AbstractNiveauNomenclature secteur2 = jeuxMappingAssociation();

		AbstractNiveauNomenclature secteur3 = jeuxMappingAssociation();

		entityManager.persist(secteur1);
		entityManager.persist(secteur2);
		entityManager.persist(secteur3);

	}

	@Transactional
	public void creerJeuNouvelleNomenclature() {

		for (Section section : jeuNouvelleNomenclature()) {
			entityManager.persist(section);
		}

	}

	@Transactional
	public void creerJeuNouvelleNomenclatureAvecIndice() {

		Set<Section> sections = jeuNouvelleNomenclature();

		ajouterIndiceRandomSousClasse(sections);

		for (Section section : sections) {
			entityManager.persist(section);
		}
	}

	private void ajouterIndiceRandomSousClasse(Set<Section> sections) {

		for (Section section : sections) {

			for (Division division : section.getDivisions()) {

				for (Groupe groupe : division.getGroupes()) {

					for (Classe classe : groupe.getClasses()) {

						for (SousClasse sousClasse : classe.getSousClasses()) {

							ajouterIndiceRandomSousClasse(sousClasse);

						}

					}

				}

			}

		}

	}

	private void ajouterIndiceRandomSousClasse(SousClasse sousClasse) {

		for (Month month : Month.values()) {

			IndiceMensuel indiceMensuel = new IndiceMensuel();

			indiceMensuel.setMonth(YearMonth.of(2016, month));

			indiceMensuel.setValeur(faker.random().nextDouble());

			sousClasse.addIndiceMensuel(indiceMensuel);

		}

		IndiceAnnuel indiceAnnuel = new IndiceAnnuel();

		indiceAnnuel.setYear(Year.parse("2016"));

		indiceAnnuel.setValeur(faker.random().nextDouble());

		sousClasse.addIndiceAnnuel(indiceAnnuel);

	}

	public static List<Entreprise> jeuxEntreprisesReduit() {

		List<Entreprise> listEntreprises = new ArrayList<Entreprise>();

		Entreprise entreprise1 = new Entreprise();

		Instant instant1 = LocalDateTime.parse("2014-03-01T10:15:30").atZone(ZoneId.of("Europe/Paris")).toInstant();

		entreprise1.setDateCreation(Date.from(instant1));

		entreprise1.setDenomination("Le bar à Momo");

		entreprise1.setSiren("123456789");

		entreprise1.setTelephone("0123456789");

		Adresse adresse1 = new Adresse();

		adresse1.setNumero("12");

		adresse1.setNomVoie("Jean Jaurès");

		adresse1.setTypeVoie(TypeVoie.RUE);

		adresse1.setVille("PARIS");

		adresse1.setPays("FRANCE");

		entreprise1.setAdresse(adresse1);

		listEntreprises.add(entreprise1);

		Entreprise entreprise2 = new Entreprise();

		Instant instant2 = LocalDateTime.parse("2015-05-26T10:15:30").atZone(ZoneId.of("Europe/Paris")).toInstant();

		entreprise2.setDateCreation(Date.from(instant2));

		entreprise2.setDenomination("Confiserie");

		entreprise2.setSiren("234567891");

		entreprise2.setTelephone("0234567890");

		Adresse adresse2 = new Adresse();

		adresse2.setNumero("56");

		adresse2.setNomVoie("Charles de Gaulle");

		adresse2.setTypeVoie(TypeVoie.BOULEVARD);

		adresse2.setVille("RENNES");

		adresse2.setPays("FRANCE");

		entreprise2.setAdresse(adresse2);

		listEntreprises.add(entreprise2);

		Entreprise entreprise3 = new Entreprise();

		Instant instant3 = LocalDateTime.parse("2012-03-24T10:15:30").atZone(ZoneId.of("Europe/Paris")).toInstant();

		entreprise3.setDateCreation(Date.from(instant3));

		entreprise3.setDenomination("Le p'tit nantais");

		entreprise3.setSiren("345678912");

		entreprise3.setTelephone("0245678912");

		Adresse adresse3 = new Adresse();

		adresse3.setNumero("23");

		adresse3.setNomVoie("Alfred Alfred");

		adresse3.setTypeVoie(TypeVoie.IMPASSE);

		adresse3.setVille("NANTES");

		adresse3.setPays("FRANCE");

		entreprise3.setAdresse(adresse3);

		listEntreprises.add(entreprise3);

		return listEntreprises;

	}

	public SousClasse jeuxMappingAssociation() {

		SousClasse secteur = new SousClasse();

		secteur.setCodeNaf("1104Z");

		secteur.setLibelleNomenclature("Production d'autres boissons fermentées non distillées");

		List<Entreprise> entreprises = jeuxEntreprisesReduit();

		int compteur = 1;

		for (Entreprise entreprise : entreprises) {

			for (Month month : Month.values()) {

				Declaration declaration = new Declaration();

				declaration.setDate(
						Date.from(YearMonth.of(2016, month).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

				declaration.setMontant(new Double(month.getValue() * compteur));

				entreprise.addDeclaration(declaration);

			}

			compteur++;

			secteur.addEntreprise(entreprise);

		}

		for (Month month : Month.values()) {

			IndiceMensuel indiceMensuel = new IndiceMensuel();

			indiceMensuel.setMonth(YearMonth.of(2016, month));

			indiceMensuel.setValeur(0d);

			secteur.addIndiceMensuel(indiceMensuel);

		}

		IndiceAnnuel indiceAnnuel = new IndiceAnnuel();

		indiceAnnuel.setYear(Year.parse("2016"));

		indiceAnnuel.setValeur(0d);

		secteur.addIndiceAnnuel(indiceAnnuel);

		return secteur;

	}

	public Set<Section> jeuNouvelleNomenclature() {

		Set<Section> sections = new HashSet<Section>();

		Section section = new Section();

		section.setCodeNaf("section1");
		section.setLibelleNomenclature("Libelle section");

		Division division = new Division();

		division.setCodeNaf("division2");
		division.setLibelleNomenclature("Libelle division");

		section.addDivision(division);

		Groupe groupe = new Groupe();

		groupe.setCodeNaf("groupe3");
		groupe.setLibelleNomenclature("Libelle groupe");

		division.addGroupe(groupe);

		Classe classe = new Classe();

		classe.setCodeNaf("classe4");
		classe.setLibelleNomenclature("Libelle classe");

		groupe.addClasse(classe);

		SousClasse sousClasse = new SousClasse();

		sousClasse.setCodeNaf("sousclasse5");
		sousClasse.setLibelleNomenclature("Libelle Sous Classe");

		classe.addSousClasse(sousClasse);

		sections.add(section);

		return sections;

	}

	public void insererObjets(Set<Object> objectsACreer) {

		for (Object object : objectsACreer) {
			entityManager.persist(object);
		}

	}

}
