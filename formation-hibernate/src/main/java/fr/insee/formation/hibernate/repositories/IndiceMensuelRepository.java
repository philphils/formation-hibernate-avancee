package fr.insee.formation.hibernate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.IndiceMensuel;

public interface IndiceMensuelRepository extends JpaRepository<IndiceMensuel, Integer> {

	@Modifying
	@Query("UPDATE IndiceMensuel SET valeur = 0")
	public void remiseZeroInOneQuery();

}
