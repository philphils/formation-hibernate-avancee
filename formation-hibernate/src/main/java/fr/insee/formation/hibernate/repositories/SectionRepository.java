package fr.insee.formation.hibernate.repositories;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import fr.insee.formation.hibernate.model.nomenclature.Section;

public interface SectionRepository extends JpaRepository<Section, Integer> {

	@QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"),
			@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHE_REGION, value = "section_query") })
	public Optional<Section> findByCodeNaf(String codeNaf);

	@QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"),
			@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHE_REGION, value = "section_query") })
	public List<Section> findAll();

}
