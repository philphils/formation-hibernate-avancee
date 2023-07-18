package fr.insee.formation.hibernate.repositories;

import fr.insee.formation.hibernate.model.Declaration;
import fr.insee.formation.hibernate.repositories.custom.JPAHistoRepository;

public interface DeclarationHistoRepository extends JPAHistoRepository<Declaration, Integer> {

}
