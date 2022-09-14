package fr.insee.formation.hibernate.batch.listener;

import java.time.Instant;

import org.springframework.batch.core.ItemProcessListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimingItemProcessListener implements ItemProcessListener {

	private Integer compteur = 1;

	private Integer affichageLogCompteur;

	private Instant timer;

	private Long totalMilliseconds = 0L;

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
			Long milliSeconds = Instant.now().toEpochMilli() - timer.toEpochMilli();
			totalMilliseconds = milliSeconds + totalMilliseconds;
			log.info(milliSeconds + " milli-secondes pour persister " + affichageLogCompteur + " objets. Moyenne : "
					+ Math.floor(((double) compteur / (double) totalMilliseconds) * 1000) + " objets traités / second");
			timer = Instant.now();
		}

	}

	@Override
	public void onProcessError(Object item, Exception e) {
		// TODO Auto-generated method stub

	}

}
