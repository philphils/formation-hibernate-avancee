package fr.insee.formation.hibernate.repositories;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	 
	// @formatter:on
	public Optional<Declaration> findByIdWithEntreprise(Integer identifiant);

	public Page<Declaration> findByEntreprise(Entreprise entreprise, Pageable pageable);

	@Query("SELECT decl FROM Declaration decl")
	public Stream<Declaration> findAllStream();

	//// @formatter:off
	@Query("SELECT decl FROM Declaration decl "
			+ "	JOIN FETCH decl.entreprise entreprise "
			+ " JOIN FETCH entreprise.secteur secteur ")
	// @formatter:on
	public Set<Declaration> findAllDeclarationWithEntrepriseWithSecteur();

	//// @formatter:off
	@Query("SELECT decl FROM Declaration decl "
			+ " JOIN FETCH decl.entreprise entreprise "
			+ " JOIN FETCH entreprise.secteur secteur ")
	// @formatter:on
	public Stream<Declaration> streamAllDeclarationWithEntrepriseWithSecteur();

}
