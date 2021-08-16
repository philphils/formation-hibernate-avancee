package fr.insee.formation.hibernate.services.impl;

import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.formation.hibernate.dao.SecteurDAO;
import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.IndiceAnnuel;
import fr.insee.formation.hibernate.model.IndiceMensuel;
import fr.insee.formation.hibernate.model.Secteur;
import fr.insee.formation.hibernate.services.SecteurServices;

@Service
public class SecteurServicesImpl implements SecteurServices {

	@Autowired
	private SecteurDAO secteurDAO;

	@Override
	@Transactional
	public Secteur calculerIndicesSecteurByCodeNaf(String codeNaf) {

		Secteur secteur = secteurDAO.findByCodeNaf(codeNaf);

		return calculerIndiceSecteur(secteur);
	}

	private Secteur calculerIndiceSecteur(Secteur secteur) {
		for (Entreprise entreprise : secteur.getEntreprises()) {

			for (Declaration declaration : entreprise.getDeclarations().values()) {

				IndiceAnnuel indiceAnnuel = secteur.getIndicesAnnuels().get(Year.from(declaration.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

				IndiceMensuel indiceMensuel = secteur.getIndicesMensuels()
				        .get(YearMonth.from(declaration.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

				indiceAnnuel.setValeur(indiceAnnuel.getValeur() + declaration.getMontant());

				indiceMensuel.setValeur(indiceMensuel.getValeur() + declaration.getMontant());

			}

		}

		return secteur;
	}

	@Override
	@Transactional
	public Secteur calculerIndicesSecteurByCodeNafRequeteJPQL(String codeNaf) {

		Secteur secteur = secteurDAO.findByCodeNafWithEntreprisesAndDeclarationAndIndicesJPQL(codeNaf);

		return calculerIndiceSecteur(secteur);
	}

	@Override
	@Transactional
	public Secteur calculerIndicesSecteurByCodeNafRequeteCriteria(String codeNaf) {

		Secteur secteur = secteurDAO.findByCodeNafWithEntreprisesAndDeclarationAndIndicesCriteria(codeNaf);
		
		return calculerIndiceSecteur(secteur);
		
	}

}
