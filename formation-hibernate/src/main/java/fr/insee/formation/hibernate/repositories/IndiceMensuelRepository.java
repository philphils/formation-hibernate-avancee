package fr.insee.formation.hibernate.repositories;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.IndiceMensuel;

public interface IndiceMensuelRepository extends JpaRepository<IndiceMensuel, Integer> {

	@Modifying
	@Query("UPDATE IndiceMensuel SET valeur = 0")
	public void remiseZeroInOneQuery();

	/// @formatter:off
			@Query(" SELECT indice FROM IndiceMensuel indice "
					+ " JOIN FETCH indice.sousClasse sousClasse "
					+ " JOIN FETCH sousClasse.entreprises entreprises "
					+ " JOIN FETCH entreprises.declarations declarations "
					)
		/// @formatter:on
	public Stream<IndiceMensuel> findAllIndicesMensuelWithSousClasseAndEntrepriseAndDeclaration();

}
