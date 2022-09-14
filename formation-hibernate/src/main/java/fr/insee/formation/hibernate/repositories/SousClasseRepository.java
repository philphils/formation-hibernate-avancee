package fr.insee.formation.hibernate.repositories;

import java.util.Optional;
import java.util.Set;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import fr.insee.formation.hibernate.model.Entreprise;
import fr.insee.formation.hibernate.model.nomenclature.SousClasse;

public interface SousClasseRepository extends JpaRepository<SousClasse, Integer> {

	@QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"))
	public Optional<SousClasse> findByCodeNaf(String codeNaf);

	//// @formatter:off
	@Query(value = "SELECT ssClasse FROM SousClasse ssClasse "
			+ " LEFT JOIN FETCH ssClasse.entreprises ent")
	// @formatter:on
	public Set<SousClasse> findAllWithEntreprises();

	//// @formatter:off
	@Query(value = "SELECT ssClasse FROM SousClasse ssClasse "
			+ " LEFT JOIN FETCH ssClasse.indicesMensuels indMens "
			+ " LEFT JOIN FETCH ssClasse.indicesAnnuels indAnn ")
	// @formatter:on
	public Set<SousClasse> findAllWithIndicesMensuelsAndAnnuels();

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
