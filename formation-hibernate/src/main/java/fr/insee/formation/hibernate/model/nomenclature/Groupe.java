package fr.insee.formation.hibernate.model.nomenclature;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Groupe extends AbstractNiveau {

}
