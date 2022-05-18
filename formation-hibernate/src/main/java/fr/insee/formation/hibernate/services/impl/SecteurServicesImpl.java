package fr.insee.formation.hibernate.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.formation.hibernate.dao.SecteurDAO;
import fr.insee.formation.hibernate.model.nomenclature.AbstractNiveauNomenclature;
import fr.insee.formation.hibernate.services.SecteurServices;

@Service
public class SecteurServicesImpl implements SecteurServices {

	@Autowired
	private SecteurDAO secteurDAO;

	@Override
	@Transactional
	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNaf(String codeNaf) {

		AbstractNiveauNomenclature secteur = secteurDAO.findByCodeNaf(codeNaf);

		return calculerIndiceSecteur(secteur);
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

	@Override
	@Transactional
	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNafRequeteJPQL(String codeNaf) {

		AbstractNiveauNomenclature secteur = secteurDAO
				.findByCodeNafWithEntreprisesAndDeclarationAndIndicesJPQL(codeNaf);

		return calculerIndiceSecteur(secteur);
	}

	@Override
	@Transactional
	public AbstractNiveauNomenclature calculerIndicesSecteurByCodeNafRequeteCriteria(String codeNaf) {

		AbstractNiveauNomenclature secteur = secteurDAO
				.findByCodeNafWithEntreprisesAndDeclarationAndIndicesCriteria(codeNaf);

		return calculerIndiceSecteur(secteur);

	}

}
