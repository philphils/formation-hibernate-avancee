package fr.insee.formation.hibernate.model;

import java.time.YearMonth;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class IndiceMensuel extends Indice {

	private YearMonth month;

}
