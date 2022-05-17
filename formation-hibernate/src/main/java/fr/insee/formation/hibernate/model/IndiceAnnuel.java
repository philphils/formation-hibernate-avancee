package fr.insee.formation.hibernate.model;

import java.time.Year;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IndiceAnnuel extends Indice {

	private Year year;

}
