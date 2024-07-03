package fr.insee.formation.hibernate;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContext;

import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource("Hibernate:application=Statistics")
public class HibernateStatisticsMBean implements InitializingBean {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private Statistics stats;

	@Override
	public void afterPropertiesSet() throws Exception {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		sessionFactory.getStatistics().setStatisticsEnabled(true);
		this.stats = sessionFactory.getStatistics();
	}

	@Autowired
	private ServletContext servletContext;

	@ManagedOperation
	public void clear() {
		stats.clear();
	}

	@ManagedOperation
	public EntityStatistics getEntityStatistics(String entityName) {
		return stats.getEntityStatistics(entityName);
	}

	@ManagedOperation
	public CollectionStatistics getCollectionStatistics(String role) {
		return stats.getCollectionStatistics(role);
	}

	@ManagedOperation
	public CacheRegionStatistics getSecondLevelCacheStatistics(String regionName) {
		return stats.getCacheRegionStatistics(regionName);
	}

	@ManagedOperation
	public QueryStatistics getQueryStatistics(String hql) {
		return stats.getQueryStatistics(hql);
	}

	@ManagedAttribute
	public long getEntityDeleteCount() {
		return stats.getEntityDeleteCount();
	}

	@ManagedAttribute
	public long getEntityInsertCount() {
		return stats.getEntityInsertCount();
	}

	@ManagedAttribute
	public long getEntityLoadCount() {
		return stats.getEntityLoadCount();
	}

	@ManagedAttribute
	public long getEntityFetchCount() {
		return stats.getEntityFetchCount();
	}

	@ManagedAttribute
	public long getEntityUpdateCount() {
		return stats.getEntityUpdateCount();
	}

	@ManagedAttribute
	public long getQueryExecutionCount() {
		return stats.getQueryExecutionCount();
	}

	@ManagedAttribute
	public long getQueryCacheHitCount() {
		return stats.getQueryCacheHitCount();
	}

	@ManagedAttribute
	public long getQueryExecutionMaxTime() {
		return stats.getQueryExecutionMaxTime();
	}

	@ManagedAttribute
	public long getQueryCacheMissCount() {
		return stats.getQueryCacheMissCount();
	}

	@ManagedAttribute
	public long getQueryCachePutCount() {
		return stats.getQueryCachePutCount();
	}

	@ManagedAttribute
	public long getFlushCount() {
		return stats.getFlushCount();
	}

	@ManagedAttribute
	public long getConnectCount() {
		return stats.getConnectCount();
	}

	@ManagedAttribute
	public long getSecondLevelCacheHitCount() {
		return stats.getSecondLevelCacheHitCount();
	}

	@ManagedAttribute
	public long getSecondLevelCacheMissCount() {
		return stats.getSecondLevelCacheMissCount();
	}

	@ManagedAttribute
	public long getSecondLevelCachePutCount() {
		return stats.getSecondLevelCachePutCount();
	}

	@ManagedAttribute
	public long getSessionCloseCount() {
		return stats.getSessionCloseCount();
	}

	@ManagedAttribute
	public long getSessionOpenCount() {
		return stats.getSessionOpenCount();
	}

	@ManagedAttribute
	public long getCollectionLoadCount() {
		return stats.getCollectionLoadCount();
	}

	@ManagedAttribute
	public long getCollectionFetchCount() {
		return stats.getCollectionFetchCount();
	}

	@ManagedAttribute
	public long getCollectionUpdateCount() {
		return stats.getCollectionUpdateCount();
	}

	@ManagedAttribute
	public long getCollectionRemoveCount() {
		return stats.getCollectionRemoveCount();
	}

	@ManagedAttribute
	public long getCollectionRecreateCount() {
		return stats.getCollectionRecreateCount();
	}

	@ManagedAttribute
	public long getStartTime() {
		return stats.getStartTime();
	}

	@ManagedAttribute
	public boolean isStatisticsEnabled() {
		return stats.isStatisticsEnabled();
	}

	@ManagedOperation
	public void setStatisticsEnabled(boolean enable) {
		stats.setStatisticsEnabled(enable);
	}

	@ManagedOperation
	public void logSummary() {
		stats.logSummary();
	}

	@ManagedAttribute
	public String[] getCollectionRoleNames() {
		return stats.getCollectionRoleNames();
	}

	@ManagedAttribute
	public String[] getEntityNames() {
		return stats.getEntityNames();
	}

	@ManagedAttribute
	public String[] getQueries() {
		return stats.getQueries();
	}

	@ManagedAttribute
	public String[] getSecondLevelCacheRegionNames() {
		return stats.getSecondLevelCacheRegionNames();
	}

	@ManagedAttribute
	public long getSuccessfulTransactionCount() {
		return stats.getSuccessfulTransactionCount();
	}

	@ManagedAttribute
	public long getTransactionCount() {
		return stats.getTransactionCount();
	}

	@ManagedAttribute
	public long getCloseStatementCount() {
		return stats.getCloseStatementCount();
	}

	@ManagedAttribute
	public long getPrepareStatementCount() {
		return stats.getPrepareStatementCount();
	}

	@ManagedAttribute
	public long getOptimisticFailureCount() {
		return stats.getOptimisticFailureCount();
	}

	@ManagedAttribute
	public String getQueryExecutionMaxTimeQueryString() {
		return stats.getQueryExecutionMaxTimeQueryString();
	}

}
