package fr.insee.formation.hibernate.batch.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import org.springframework.batch.core.ItemProcessListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimingItemProcessListener implements ItemProcessListener {

	private Integer compteur = 1;

	private Integer affichageLogCompteur;

	private Instant timer;

	private Long totalMilliseconds = 0L;

	private Boolean firstTime = true;

	public void setAffichageLogCompteur(Integer affichageLogCompteur) {
		this.affichageLogCompteur = affichageLogCompteur;
	}

	@Override
	public void beforeProcess(Object item) {
		if (timer == null)
			timer = Instant.now();
	}

	@Override
	public void afterProcess(Object item, Object result) {

		compteur = compteur + 1;

		if (affichageLogCompteur != null && compteur % affichageLogCompteur == 0) {

			/* Gestion des effets de bord */
			if (firstTime) {
				compteur = 1;
				timer = Instant.now();
				totalMilliseconds = 0L;
				firstTime = false;
			}

			else {
				Long milliSeconds = Instant.now().toEpochMilli() - timer.toEpochMilli();
				totalMilliseconds = milliSeconds + totalMilliseconds;
				timer = Instant.now();
				log.info(milliSeconds + " milli-secondes pour persister " + affichageLogCompteur + " objets. Moyenne : "
						+ (new BigDecimal(((double) compteur / (double) totalMilliseconds) * 1000)).setScale(2,
								RoundingMode.DOWN)
						+ " objets traités / second");
			}
		}
	}

	@Override
	public void onProcessError(Object item, Exception e) {
		// TODO Auto-generated method stub

	}

}
