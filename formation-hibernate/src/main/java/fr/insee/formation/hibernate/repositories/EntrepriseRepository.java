package fr.insee.formation.hibernate.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.hibernate.model.Entreprise;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {

	Optional<Entreprise> findBySirenAndDenomination(String siren, Object object);

	/**
	 * Creer une méthode qui récupère l' Entreprise avec le bon verrou permettant de
	 * contrôler l'ajout/modification/suppression de déclarations (exo 2 du TP4)
	 * 
	 * @param idEntreprise
	 * @return
	 */

}
