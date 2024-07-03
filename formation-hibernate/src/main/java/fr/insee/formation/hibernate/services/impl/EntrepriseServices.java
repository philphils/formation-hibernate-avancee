package fr.insee.formation.hibernate.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.repositories.EntrepriseRepository;
import fr.insee.formation.hibernate.services.IEntrepriseServices;
import jakarta.transaction.Transactional;

@Service
public class EntrepriseServices implements IEntrepriseServices {

	@Autowired
	EntrepriseRepository entrepriseRepository;

	@Transactional
	@Override
	public Declaration ajouterDeclarationEntreprise(Entreprise entreprise, Double montant, Date date) {

		Declaration declaration = new Declaration();

		declaration.setDate(date);
		declaration.setMontant(montant);

		entreprise.addDeclaration(declaration);

		entrepriseRepository.save(entreprise);

		return declaration;
	}

}
