package fr.insee.formation.hibernate.model;

import java.time.Year;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class IndiceAnnuel extends Indice {

	private Year year;

}
