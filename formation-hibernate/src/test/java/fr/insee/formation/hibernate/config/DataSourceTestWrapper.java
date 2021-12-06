package fr.insee.formation.hibernate.config;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Component
public class DataSourceTestWrapper implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		if (bean instanceof DataSource) {
			//// @formatter:off
			DataSource dataSource = ProxyDataSourceBuilder.create((DataSource) bean)
					.logQueryByCommons(CommonsLogLevel.DEBUG)
					.countQuery()
					.logSlowQueryByCommons(10, TimeUnit.MINUTES)
					.proxyResultSet().build();
			// @formatter:on

			return dataSource;
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
