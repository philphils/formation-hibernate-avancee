package fr.insee.formation.hibernate.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;
import fr.insee.formation.hibernate.repositories.SousClasseRepository;
import fr.insee.formation.hibernate.services.SecteurServices;

@Service
public class SecteurServicesImpl implements SecteurServices {

	@Autowired
	private SousClasseRepository sousClasseRepository;

	@Override
	@Transactional
	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNaf(String codeNaf) {

		Optional<SousClasse> optionalSousClasse = sousClasseRepository.findByCodeNaf(codeNaf);

		if (optionalSousClasse.isEmpty())
			throw new RuntimeException("Aucune sous-classe ne correspond au code NAF : " + codeNaf);

		return calculerIndiceSecteur(optionalSousClasse.get());
	}

	private AbstractNiveauNomenclature calculerIndiceSecteur(AbstractNiveauNomenclature secteur) {
		// TODO Faire évoluer le batch pour pouvoir calculer les indices à chaque niveau
		// de nomenclature
//		for (Entreprise entreprise :  secteur.getEntreprises()) {
//
//			for (Declaration declaration : entreprise.getDeclarations().values()) {
//
//				IndiceAnnuel indiceAnnuel = secteur.getIndicesAnnuels()
//						.get(Year.from(declaration.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
//
//				IndiceMensuel indiceMensuel = secteur.getIndicesMensuels().get(
//						YearMonth.from(declaration.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
//
//				indiceAnnuel.setValeur(indiceAnnuel.getValeur() + declaration.getMontant());
//
//				indiceMensuel.setValeur(indiceMensuel.getValeur() + declaration.getMontant());
//
//			}
//
//		}

		return secteur;
	}

}
