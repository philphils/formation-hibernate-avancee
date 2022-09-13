package fr.insee.formation.hibernate.batch.calculIndices;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.insee.formation.hibernate.model.Indice;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

@Component
public class CreationIndicesProcessor implements ItemProcessor<SousClasse, SousClasse> {

	@Value("${batch.nbMoisHistorique}")
	private Integer nbMoisHistorique;

	@Override
	public SousClasse process(SousClasse sousClasse) throws Exception {

		Set<Indice> indices = new HashSet<Indice>();

		LocalDate dateDebut = LocalDate.of(2022, 01, 01);

		Year yearPrecedent = null;

		for (int i = 0; i < nbMoisHistorique; i++) {

			YearMonth yearMonth = YearMonth.from(dateDebut.minusMonths(i));

			IndiceMensuel indiceMensuel = new IndiceMensuel();

			indiceMensuel.setMonth(yearMonth);
			indiceMensuel.setValeur(0d);

			sousClasse.addIndiceMensuel(indiceMensuel);

			indices.add(indiceMensuel);

			Year year = Year.from(yearMonth);

			if (yearPrecedent == null || !year.equals(yearPrecedent)) {

				yearPrecedent = year;

				IndiceAnnuel indiceAnnuel = new IndiceAnnuel();

				indiceAnnuel.setYear(year);
				indiceAnnuel.setValeur(0d);

				sousClasse.addIndiceAnnuel(indiceAnnuel);

				indices.add(indiceAnnuel);

			}

		}

		return sousClasse;
	}

}
