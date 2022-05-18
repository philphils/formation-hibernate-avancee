package fr.insee.formation.hibernate.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

public interface SousClasseRepository extends JpaRepository<SousClasse, Integer> {

	public Optional<SousClasse> findByCodeNaf(String codeNaf);

	/**
	 * TP2 : Exercice 2 : Modifier la requête pour récupérer un {@link SousClasse}
	 * ayant une {@link Entreprise} donnée en paramètre, avec toutes ses
	 * {@link Entreprise}
	 * 
	 * @param entreprise
	 * @return
	 */
	//// @formatter:off
	@Query("SELECT sousClasse FROM SousClasse sousClasse "
			+ "JOIN sousClasse.entreprises entreprise "
			+ "JOIN FETCH sousClasse.entreprises entreprise2 "
			+ "WHERE entreprise = :entreprise ")
	// @formatter:on
	public SousClasse findByEntrepriseWithAllEntreprises(Entreprise entreprise);

}
