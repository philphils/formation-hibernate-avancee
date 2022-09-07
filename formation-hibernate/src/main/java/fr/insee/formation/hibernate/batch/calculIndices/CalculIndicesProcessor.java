package fr.insee.formation.hibernate.batch.calculIndices;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Map.Entry;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

@Component
public class CalculIndicesProcessor implements ItemProcessor<Declaration, Entry<IndiceMensuel, IndiceAnnuel>> {

	@Override
	public Entry<IndiceMensuel, IndiceAnnuel> process(Declaration declaration) throws Exception {

		/*
		 * Calcul des coeff
		 */

		Entreprise entreprise = declaration.getEntreprise();

		SousClasse sousClasse = entreprise.getSousClasse();

		Double coeffMoy = (entreprise.getCoeffRedressementEntreprise() + sousClasse.getCoeffCalculIndice()
				+ sousClasse.getClasse().getCoeffCalculIndice()
				+ sousClasse.getClasse().getGroupe().getCoeffCalculIndice()
				+ sousClasse.getClasse().getGroupe().getDivision().getCoeffCalculIndice()
				+ sousClasse.getClasse().getGroupe().getDivision().getSection().getCoeffCalculIndice()) / 6;

		/*
		 * Récupération des Indices
		 */
		LocalDate date = declaration.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		IndiceMensuel indiceMensuel = sousClasse.getMapIndicesMensuels().get(YearMonth.from(date));

		IndiceAnnuel indiceAnnuel = sousClasse.getMapIndicesAnnuels().get(Year.from(date));

		/*
		 * Ajout de la valeur en cours
		 */
		indiceAnnuel.setValeur(indiceAnnuel.getValeur() + coeffMoy * declaration.getMontant());

		indiceMensuel.setValeur(indiceMensuel.getValeur() + coeffMoy * declaration.getMontant());

		return new AbstractMap.SimpleEntry<IndiceMensuel, IndiceAnnuel>(indiceMensuel, indiceAnnuel);
	}

}
