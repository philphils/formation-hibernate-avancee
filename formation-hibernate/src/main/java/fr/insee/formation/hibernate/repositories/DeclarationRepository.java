package fr.insee.formation.hibernate.repositories;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.model.Entreprise;

public interface DeclarationRepository extends JpaRepository<Declaration, Integer> {

	public Set<Declaration> findByEntreprise(Entreprise entreprise);

	public Set<Declaration> findByEntrepriseAndDate(Entreprise entreprise, Date date);

	//// @formatter:off
	@Query("SELECT decl FROM Declaration decl "
			+ "JOIN FETCH decl.entreprise entreprise "
			+ "WHERE decl.id = :identifiant ")
	public Optional<Declaration> findByIdWithEntreprise(Integer identifiant);

}
