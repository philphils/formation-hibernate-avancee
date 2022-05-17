package fr.insee.formation.hibernate.model;

import java.time.YearMonth;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IndiceMensuel extends Indice {

	private YearMonth month;

}
