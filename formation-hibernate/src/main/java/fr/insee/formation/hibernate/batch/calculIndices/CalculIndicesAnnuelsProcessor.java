package fr.insee.formation.hibernate.batch.calculIndices;

import java.util.Set;

import org.springframework.batch.item.ItemProcessor;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

public class CalculIndicesAnnuelsProcessor implements ItemProcessor<IndiceAnnuel, IndiceAnnuel> {

	@Override
	public IndiceAnnuel process(IndiceAnnuel indiceAnnuel) throws Exception {

		SousClasse sousClasse = indiceAnnuel.getSousClasse();

		for (Entreprise entreprise : sousClasse.getEntreprises()) {

			/*
			 * Calcul du coeff (et parcours de la hiérarchie de la nomenclature)
			 */
			Double coeffMoy = entreprise.calculCoeffMoyen();

			/*
			 * Récupération des Declaration
			 */
			Set<Declaration> declarations = entreprise.getDeclarationsByYear(indiceAnnuel.getYear());

			/*
			 * Ajout de la valeur en cours
			 */
			if (!declarations.isEmpty()) {

				for (Declaration declaration : declarations) {
					indiceAnnuel.setValeur(indiceAnnuel.getValeur() + coeffMoy * declaration.getMontant());
				}

			}

		}

		return indiceAnnuel;
	}

}
