package fr.insee.formation.hibernate.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.p6spy.engine.spy.P6DataSource;

import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * Cette classe permet de wrappé (enrobé) la {@link DataSource} pour lui ajouter
 * d'autres fonctionnalité pour les tests seulement (sinon dégradation des
 * performances)
 * 
 * @author SYV27O
 *
 */

@Component
@Slf4j
public class DataSourceTestWrapper implements BeanPostProcessor {

	@Value("${activate.p6spy}")
	Boolean activateP6spy;

	@Value("${activate.datasource-proxy}")
	Boolean activateDatasourceProxy;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		if (beanName.contains("postgres") && bean instanceof DataSource) {

			DataSource dataSource = (DataSource) bean;

			try {
				log.info("Connection URL : {}", dataSource.getConnection().getMetaData().getURL());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (activateDatasourceProxy) {
				// Datasource proxy
				//// @formatter:off
				dataSource = ProxyDataSourceBuilder.create((DataSource) dataSource)
						.name("dataSource")
			            .logQueryBySlf4j(SLF4JLogLevel.DEBUG)
			            .countQuery()          // collect query metrics
			            .proxyResultSet()
			            .multiline()           // enable multiline output
			     .build();
				// @formatter:on
			}

			if (activateP6spy) {
				dataSource = new P6DataSource(dataSource);
			}

			return dataSource;
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
