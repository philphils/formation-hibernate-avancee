package fr.insee.formation.hibernate.model;

import java.time.YearMonth;

import javax.persistence.Entity;

@Entity
public class IndiceMensuel extends Indice {

	private YearMonth month;

	public YearMonth getMonth() {
		return month;
	}

	public void setMonth(YearMonth month) {
		this.month = month;
	}

}
