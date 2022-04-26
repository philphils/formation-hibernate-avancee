package fr.insee.formation.hibernate.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.hibernate.model.Entreprise;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {

	Optional<Entreprise> findBySirenAndDenomination(String siren, Object object);

}
