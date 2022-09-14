package fr.insee.formation.hibernate.repositories;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.IndiceAnnuel;

public interface IndiceAnnuelRepository extends JpaRepository<IndiceAnnuel, Integer> {

	@Modifying
	@Query("UPDATE IndiceAnnuel SET valeur = 0")
	public void remiseZeroInOneQuery();

	/// @formatter:off
	@Query(" SELECT indice FROM IndiceAnnuel indice "
			+ " JOIN FETCH indice.sousClasse sousClasse "
			+ " JOIN FETCH sousClasse.entreprises entreprises "
			+ " JOIN FETCH entreprises.declarations declarations "
			)
	/// @formatter:on	
	public Stream<IndiceAnnuel> streamAllIndicesAnnuelsWithSousClasseAndEntrepriseAndDeclaration();

}
