package fr.insee.formation.hibernate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.Secteur;

public interface SecteurRepository extends JpaRepository<Secteur, Integer> {

	/**
	 * TP2 : Exercice 2 : Modifier la requête pour récupérer un {@link Secteur}
	 * ayant une {@link Entreprise} donnée en paramètre, avec toutes ses
	 * {@link Entreprise}
	 * 
	 * @param entreprise
	 * @return
	 */
	//// @formatter:off
	@Query("SELECT secteur FROM Secteur secteur "
			+ "JOIN secteur.entreprises entreprise "
			+ "JOIN FETCH secteur.entreprises entreprise2 "
			+ "WHERE entreprise = :entreprise ")
	// @formatter:on
	public Secteur findByEntrepriseWithAllEntreprises(Entreprise entreprise);

}
