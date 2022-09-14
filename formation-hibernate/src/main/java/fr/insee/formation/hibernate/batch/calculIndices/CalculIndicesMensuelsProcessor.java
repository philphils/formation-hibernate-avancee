package fr.insee.formation.hibernate.batch.calculIndices;

import javax.persistence.EntityManager;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

@Component
public class CalculIndicesMensuelsProcessor implements ItemProcessor<IndiceMensuel, IndiceMensuel> {

	@Autowired
	EntityManager entityManager;

	@Override
	public IndiceMensuel process(IndiceMensuel indiceMensuel) throws Exception {

		SousClasse sousClasse = indiceMensuel.getSousClasse();

		for (Entreprise entreprise : sousClasse.getEntreprises()) {

			/*
			 * Calcul du coeff (et parcours de la hiérarchie de la nomenclature)
			 */
			Double coeffMoy = entreprise.calculCoeffMoyen();

			/*
			 * Récupération des Declaration
			 */
			Declaration declaration = entreprise.getDeclarationsByYearMonth(indiceMensuel.getMonth());

			/*
			 * Ajout de la valeur en cours
			 */
			if (declaration != null) {
				indiceMensuel.setValeur(indiceMensuel.getValeur() + coeffMoy * declaration.getMontant());
			}

		}

		return indiceMensuel;
	}

}
