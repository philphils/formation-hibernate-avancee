package fr.insee.formation.hibernate.batch;

import static org.junit.Assert.assertEquals;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import fr.insee.formation.hibernate.config.AbstractTest;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.IndiceAnnuelRepository;
import fr.insee.formation.hibernate.repositories.IndiceMensuelRepository;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;

public class CalculIndicesBatchTest extends AbstractTest {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job creationJeuDonneesJob;

	@Autowired
	Job calculIndicesJob;

	@Autowired
	IndiceAnnuelRepository indiceAnnuelRepository;

	@Autowired
	IndiceMensuelRepository indiceMensuelRepository;

	@Autowired
	SousClasseRepository sousClasseRepository;

	@Autowired
	TransactionTemplate transactionTemplate;

	@Test
	public void testCalculIndices() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(creationJeuDonneesJob, jobParameters);

		assertEquals(jobExecution.getAllFailureExceptions().size(), 0);

		assertEquals(0, indiceAnnuelRepository.count());

		assertEquals(0, indiceMensuelRepository.count());

		JobParameters jobParameters2 = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution2 = jobLauncher.run(calculIndicesJob, jobParameters2);

		assertEquals(jobExecution2.getAllFailureExceptions().size(), 0);

		assertEquals(20, indiceAnnuelRepository.count());

		assertEquals(180, indiceMensuelRepository.count());

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				Double valeurIndiceMensuel2021_01 = 0d;

				Double valeurIndiceAnnuel_2021 = 0d;

				Optional<SousClasse> sousClasse = sousClasseRepository.findByCodeNaf("02.10Z");

				for (Entreprise entreprise : sousClasse.get().getEntreprises()) {

					for (Entry<Date, Declaration> entryDateDeclaration : entreprise.getDeclarations().entrySet()) {

						if (entryDateDeclaration.getKey().toInstant().atZone(ZoneId.of("Europe/Paris"))
								.getYear() == 2021) {

							Double coeff = (entreprise.getCoeffCalculIndice()
									+ entreprise.getSousClasse().getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getGroupe().getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getGroupe().getDivision()
											.getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getGroupe().getDivision().getSection()
											.getCoeffCalculIndice())
									/ 6;

							valeurIndiceAnnuel_2021 = valeurIndiceAnnuel_2021
									+ coeff * entryDateDeclaration.getValue().getMontant();

						}

						if (entryDateDeclaration.getKey().toInstant().atZone(ZoneId.of("Europe/Paris"))
								.getYear() == 2021
								&& entryDateDeclaration.getKey().toInstant().atZone(ZoneId.of("Europe/Paris"))
										.getMonth() == Month.JANUARY) {

							Double coeff = (entreprise.getCoeffCalculIndice()
									+ entreprise.getSousClasse().getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getGroupe().getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getGroupe().getDivision()
											.getCoeffCalculIndice()
									+ entreprise.getSousClasse().getClasse().getGroupe().getDivision().getSection()
											.getCoeffCalculIndice())
									/ 6;

							valeurIndiceMensuel2021_01 = valeurIndiceMensuel2021_01
									+ coeff * entryDateDeclaration.getValue().getMontant();

						}

					}

				}

				assertEquals(valeurIndiceAnnuel_2021.intValue(),
						sousClasse.get().getMapIndicesAnnuels().get(Year.of(2021)).getValeur().intValue());

				assertEquals(valeurIndiceMensuel2021_01.intValue(), sousClasse.get().getMapIndicesMensuels()
						.get(YearMonth.of(2021, Month.JANUARY)).getValeur().intValue());

			}
		});

	}

}
