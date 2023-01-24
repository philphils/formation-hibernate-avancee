package fr.insee.formation.hibernate.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.Entreprise;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {

	Optional<Entreprise> findBySirenAndDenomination(String siren, Object object);

	/**
	 * Ajouter la requête avec un verrou optimiste forcé (FORCE_INCREMENT)
	 * 
	 * @param idEntreprise
	 * @return
	 */
	//// @formatter:off
	@Query("SELECT ent FROM Entreprise ent "
			+ " WHERE ent.id = :idEntreprise ")
	// @formatter:on
	Entreprise getEntrepriseWithOptimisticLockForceIncrement(Integer idEntreprise);

}
