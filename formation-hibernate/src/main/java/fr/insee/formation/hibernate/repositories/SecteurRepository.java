package fr.insee.formation.hibernate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.hibernate.model.Secteur;

public interface SecteurRepository extends JpaRepository<Secteur, Integer> {

}
