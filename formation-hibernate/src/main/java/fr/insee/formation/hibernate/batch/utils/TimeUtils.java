package fr.insee.formation.hibernate.batch.utils;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

	/**
	 * Renvoie un chaîne de caractère représentant la différence entre les 2 dates
	 * de format "XX heures, XX minutes et XX secondes"
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String differenceBetween(LocalTime startTime, LocalTime endTime) {

		Long hours = ChronoUnit.HOURS.between(startTime, endTime);
		Long minutes = ChronoUnit.MINUTES.between(startTime, endTime.minusHours(hours));
		Long secondes = ChronoUnit.SECONDS.between(startTime, endTime.minusHours(hours).minusMinutes(minutes));

		return hours + " heures " + minutes + " minutes et " + secondes + " seondes";

	}

}
