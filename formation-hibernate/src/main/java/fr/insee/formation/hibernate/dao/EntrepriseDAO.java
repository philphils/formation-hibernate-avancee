package fr.insee.formation.hibernate.dao;

import java.util.List;

import fr.insee.formation.hibernate.model.Entreprise;

public interface EntrepriseDAO {

	public List<Entreprise> findAllOrderByDateCreation();

	/**
	 * Persiste l {@link Entreprise} en base de donnée
	 * 
	 * @param entreprise
	 */
	public void persist(Entreprise entreprise);

	/**
	 * Met en majuscule la dénomniation de toutes les {@link Entreprise}
	 */
	public void upperDenomination();

	/**
	 * Supprime l {@link Entreprise} dont l'identifiant est en paramètre
	 * 
	 * @param identifiant
	 */
	public void removeEntrepriseById(int identifiant);

}
