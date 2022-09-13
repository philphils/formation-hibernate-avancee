package fr.insee.formation.hibernate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.insee.formation.hibernate.model.IndiceAnnuel;

public interface IndiceAnnuelRepository extends JpaRepository<IndiceAnnuel, Integer> {

	@Modifying
	@Query("UPDATE IndiceAnnuel SET valeur = 0")
	public void remiseZeroInOneQuery();

}
