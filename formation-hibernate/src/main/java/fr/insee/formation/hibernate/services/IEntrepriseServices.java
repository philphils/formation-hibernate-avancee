package fr.insee.formation.hibernate.services;

import java.util.Date;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;

public interface IEntrepriseServices {

	public Declaration ajouterDeclarationEntreprise(Entreprise entreprise, Double montant, Date date);

}
