package fr.insee.formation.hibernate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.hibernate.model.Declaration;

public interface DeclarationRepository extends JpaRepository<Declaration, Integer>{

}
