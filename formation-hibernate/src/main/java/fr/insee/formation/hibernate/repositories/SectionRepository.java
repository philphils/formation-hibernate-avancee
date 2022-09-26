package fr.insee.formation.hibernate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.hibernate.model.nomenclature.Section;

public interface SectionRepository extends JpaRepository<Section, Integer> {

	public Optional<Section> findByCodeNaf(String codeNaf);

	public List<Section> findAll();

}
