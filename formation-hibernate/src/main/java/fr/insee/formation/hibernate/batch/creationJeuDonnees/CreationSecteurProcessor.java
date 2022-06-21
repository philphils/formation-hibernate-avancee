package fr.insee.formation.hibernate.batch.creationJeuDonnees;

import java.util.Locale;
import java.util.Random;

import org.springframework.batch.item.ItemProcessor;

import com.github.javafaker.Faker;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import fr.insee.formation.hibernate.model.nomenclature.Classe;
import fr.insee.formation.hibernate.model.nomenclature.Division;
import fr.insee.formation.hibernate.model.nomenclature.Groupe;
import fr.insee.formation.hibernate.model.nomenclature.Section;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreationSecteurProcessor implements ItemProcessor<String[], AbstractNiveauNomenclature> {

	/*
	 * On n'utilise qu'un objet Random pour des questions de performances
	 */
	private Random random = new Random();
	private Faker faker = new Faker(new Locale("fr"));

	private AbstractNiveauNomenclature precedentNiveauNomenclature = null;

	@Override
	public AbstractNiveauNomenclature process(String[] item) throws Exception {

		String codeNaf = item[0];
		String libelleNomenclature = item[1];

		AbstractNiveauNomenclature niveauNomenclature = creerNiveauNomenclature(codeNaf, libelleNomenclature);

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

}
