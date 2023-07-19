package fr.insee.formation.hibernate.repositories.histo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JPAHistoRepository<T, I extends Serializable> extends JpaRepository<T, I> {

	/**
	 * Récupère les révisions d'une entité par son identifiant entre les dates
	 * renseignées. Si les dates son nulles aucune condition n'est ajouté
	 * 
	 * @param id
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @return
	 */
	List<T> getEntityRevisions(I id, Date startRevisionTime, Date endRevisionTime);

	/**
	 * Récupère les révisions d'une entités avec les infos contextuelles
	 * 
	 * @param id
	 * @return
	 */
	List<Object[]> getRevisionsWithInfo(I id);

	/**
	 * Récupère les révisions d'une entité qui avec des modifications portant sur
	 * les propriétés passées en paramètres (demande l'activation des flags de
	 * modification des colonnes)
	 * 
	 * @param id
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @param propertyNames
	 * @return
	 */
	List<T> getEntityRevisionsByProperties(I id, Date startRevisionTime, Date endRevisionTime, String... propertyNames);

	/**
	 * Récupère la première version d'une entité (lors de son insertion)
	 * 
	 * @param id
	 * @return
	 */
	T getFirstEntityRevision(I id);

	/**
	 * Récupère les révisions avec les infos contextuelles d'une entité avec des
	 * modifications portant sur les propriétés passées en paramètres (demande
	 * l'activation des flags de modification des colonnes)
	 * 
	 * @param id
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @param propertyNames
	 * @return
	 */
	List<Object[]> getEntityRevisionsByPropertiesWithInfo(I id, Date startRevisionTime, Date endRevisionTime,
			String... propertyNames);

	/**
	 * Récupère la dernière révision d'une entités
	 * 
	 * @param id
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @param propertyNames
	 * @return
	 */
	T getEntityLastRevisionByProperties(I id, Date startRevisionTime, Date endRevisionTime, String... propertyNames);

	/**
	 * Identique à
	 * {@link JPAHistoRepository#getEntityRevisionsByProperties(I, Date, Date, String...)}
	 * mais permet de passer plusieurs Ids en paramètre
	 * 
	 * @param ids
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @param propertyNames
	 * @return
	 */
	List<T> getEntitiesRevisionsByProperties(List<I> ids, Date startRevisionTime, Date endRevisionTime,
			String... propertyNames);

	/**
	 * Récupère toutes les entités dont les propriétés en parmètre ont été modifiées
	 * 
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @param propertyNames
	 * @return
	 */
	List<Object[]> getAllEntitiesRevisionsByPropertiesWithInfo(Date startRevisionTime, Date endRevisionTime,
			String... propertyNames);

	/**
	 * Récupère les entités dont les propriétés ont été modifié et pour lesquelles
	 * le champ "contexte" de REV_INFO a la valeur en paramètre
	 * 
	 * @param contexte
	 * @param startRevisionTime
	 * @param endRevisionTime
	 * @param propertyNames
	 * @return
	 */
	List<T> getAllEntitiesRevisionsByContexteAndProperties(String contexte, Date startRevisionTime,
			Date endRevisionTime, String... propertyNames);

}
