package fr.insee.formation.hibernate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.hibernate.model.Secteur;

public interface SecteurRepository extends JpaRepository<Secteur, Integer> {

	/**
	 * TP2 : Exercice 2 : Créer une méthode findByEntrepriseWithAllEntreprises qui
	 * renvoie un Secteur ayant l' {@link Entreprise} donnée en paramètre, avec
	 * toutes ses {@link Entreprise} bien instanciées
	 * 
	 * @param entreprise
	 * @return
	 */

}
